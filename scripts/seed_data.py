import requests
import json

BASE_URL = "http://localhost:8080"  # confirm this matches your backend's actual port

# --- Step A: Auth ---
register_payload = {
    "username": "seed_bot",
    "email": "seedbot@cornellmarketplace.com",
    "password": "SeedPassword123!"
}
requests.post(f"{BASE_URL}/auth/register", json=register_payload)

login_payload = {
    "email": "seedbot@cornellmarketplace.com",
    "password": "SeedPassword123!"
}
login_response = requests.post(f"{BASE_URL}/auth/login", json=login_payload)
token = login_response.json()["token"]
headers = {"Authorization": f"Bearer {token}"}
print("Got token:", token[:20], "...")

# --- Step B: Fetch sample product data ---
response = requests.get("https://dummyjson.com/products?limit=100")
data = response.json()
products = data["products"]

# --- Step C: Create a listing for each product ---
success_count = 0
fail_count = 0

for product in products:
    listing_request = {
        "title": product["title"],
        "description": product["description"],
        "price": product["price"]
    }

    image_url = product["images"][0]
    image_bytes = requests.get(image_url).content

    files = {
        "listingRequest": (None, json.dumps(listing_request), "application/json"),
        "imageUpload": ("image.jpg", image_bytes, "image/jpeg")
    }

    resp = requests.post(f"{BASE_URL}/listings", headers=headers, files=files)

    if resp.status_code == 200 or resp.status_code == 201:
        success_count += 1
        print(f"[OK] {product['title']}")
    else:
        fail_count += 1
        print(f"[FAIL {resp.status_code}] {product['title']} -> {resp.text[:200]}")

print(f"\nDone. {success_count} succeeded, {fail_count} failed.")