import psycopg2
import requests
import random
import os

# --- Connection config ---
# Set these in your terminal before running, e.g.:
#   set DB_PASSWORD=your_actual_password   (Windows PowerShell: $env:DB_PASSWORD="...")
DB_HOST = "localhost"
DB_PORT = "5432"
DB_NAME = "cornellmarketplace"
DB_USER = "postgres"
DB_PASSWORD = os.environ.get("DB_PASSWORD")  # reads from env var, never hardcoded

if not DB_PASSWORD:
    raise Exception("Set DB_PASSWORD as an environment variable before running this script.")

TARGET_ROW_COUNT = 2000

conn = psycopg2.connect(
    host=DB_HOST, port=DB_PORT, dbname=DB_NAME, user=DB_USER, password=DB_PASSWORD
)
cur = conn.cursor()

# --- Step 1: Get existing image URLs and the seed_bot user's ID ---
cur.execute("SELECT image_url FROM listing WHERE image_url IS NOT NULL")
existing_image_urls = [row[0] for row in cur.fetchall()]

cur.execute("SELECT id FROM users WHERE email = %s", ("seedbot@cornellmarketplace.com",))
seller_row = cur.fetchone()
if not seller_row:
    raise Exception("seed_bot user not found -- run the original seed_data.py first.")
seller_id = seller_row[0]

print(f"Found {len(existing_image_urls)} reusable images, seller_id={seller_id}")

# --- Step 2: Pull product data from DummyJSON for title/description/price variety ---
response = requests.get("https://dummyjson.com/products?limit=194")
products = response.json()["products"]

# --- Step 3: Generate rows, cycling through products and images ---
# Hibernate manages IDs via a DB sequence (not a native auto-increment column),
# so since we're inserting with raw SQL, we pull the next ID from that sequence
# ourselves for every row instead of leaving it blank.
rows_to_insert = []
for i in range(TARGET_ROW_COUNT):
    product = products[i % len(products)]
    image_url = existing_image_urls[i % len(existing_image_urls)]

    cur.execute("SELECT nextval('listing_seq')")
    next_id = cur.fetchone()[0]

    title = f"{product['title']} #{i}"
    description = product["description"]
    price = round(product["price"] * random.uniform(0.8, 1.2), 2)  # slight variation

    rows_to_insert.append((next_id, title, description, price, image_url, seller_id))

# --- Step 4: Bulk insert ---
insert_query = """
    INSERT INTO listing (id, title, description, price, image_url, seller_id)
    VALUES (%s, %s, %s, %s, %s, %s)
"""
cur.executemany(insert_query, rows_to_insert)
conn.commit()

print(f"Inserted {len(rows_to_insert)} rows.")

cur.close()
conn.close()