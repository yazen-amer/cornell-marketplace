# Cornell Marketplace

A marketplace for Cornell students to post items, search listings, and upload photos. Accounts require Cornell email verification.

Built with React, TypeScript, and Vite; Java 17, Spring Boot, Spring Security, and PostgreSQL on the backend. Photos are stored in S3.

## Screenshots

![Seeded campus marketplace homepage](docs/screenshots/marketplace-homepage.jpg)

The local demo has 12 fictional listings with condition notes, campus pickup locations, and stock photos. It runs against the Spring backend and an H2 database.

![Creating a local demo account](docs/screenshots/marketplace-registration.jpg)

![Email verification after account creation](docs/screenshots/marketplace-verification.jpg)

The demo account was registered and verified through the app. A local SMTP mailbox captured the code; no email was sent to Cornell. Screenshots are from October 11, 2026.

![Searching the seeded listings](docs/screenshots/marketplace-search.jpg)

## Features

Students register with a Cornell email address, verify a six-digit email code, and log in with a JWT. A listing includes a title, description, price, photo, pickup location, and seller. Sellers can edit or delete their own listings; the API checks ownership using the authenticated user.

Account and listing behavior:

- Passwords are hashed with BCrypt. Email addresses are normalized before registration, verification, and login.
- Registration and code resends are transactional. If SMTP delivery fails, the database changes roll back; a failed resend preserves the previous code.
- Password hashes and verification codes are excluded from user and listing JSON.
- The frontend preserves a session during a network failure and offers a retry. An invalid or expired token clears the session.
- Search matches part of a listing title, ignoring case (`findByTitleContainingIgnoreCase`).

## Run the seeded local demo

You only need Java 17, Node 24, and Python with [uv](https://docs.astral.sh/uv/). No PostgreSQL, S3, or external email account is needed for browsing these example listings.

From the repo root, run these in separate terminals:

```bash
# Local SMTP capture: prints verification codes, never forwards email.
uv run --with aiosmtpd --no-project python scripts/demo_mailbox.py

# Starts the Spring backend with the explicit demo profile and 12 listings.
python scripts/seed_data.py

cd frontend
npm ci
npm run dev
```

Open the Vite URL. Register a fictional `@cornell.edu` address, then enter the code printed by the local mailbox. The demo uses the normal registration, verification, login, and search endpoints.

The `demo` profile binds the backend to `127.0.0.1` and uses an in-memory H2 database. Restarting clears demo accounts and reseeds the listings. The seed seller is unverified and has a random hashed password, so it cannot log in. Running the seed again doesn’t duplicate listings or overwrite records. The local demo JWT key is public test configuration; do not use this profile for a public deployment. S3 uploads still require the normal S3 setup.

Data is in `backend/src/main/resources/demo/listings.json`; photos and source URLs are in `frontend/public/demo/`. The homepage and descriptions label these as fictional listings. Normal startup without the `demo` profile does not seed listings or replace production settings.

## Local setup

Use Java 17, Node 24, a PostgreSQL database, an S3 bucket, and an SMTP account that can send verification emails.

The backend reads process environment variables. Spring Boot does **not** automatically load `backend/.env`; use [backend/.env.example](backend/.env.example) as a reference and set the values in your shell.

| Service | Environment variables |
| --- | --- |
| PostgreSQL | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` |
| JWT | `JWT_SECRET` |
| S3 | `AWS_ACCESS_KEY`, `AWS_SECRET_KEY`, `AWS_BUCKET`, `AWS_REGION` |
| Email | `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM` |

`JWT_SECRET` must be a Base64-encoded random key of at least 32 bytes. `JWT_EXPIRATION` is in milliseconds and defaults to one hour. Use a mail sender permitted by your SMTP provider.

Start the backend:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

On macOS/Linux, use `bash ./mvnw spring-boot:run`. In a second terminal:

```bash
cd frontend
npm ci
npm run dev
```

Vite proxies `/auth`, `/users`, and `/listings` to `http://127.0.0.1:8080`. Registration needs working email delivery; verification is required before login.

## Checks

```powershell
cd backend
.\mvnw.cmd test
cd ../frontend
npm test
npm run build
npm run lint
```

Use `bash ./mvnw test` on macOS/Linux. Backend tests use H2 and the application’s BCrypt, JWT, and security filters. Email and S3 calls are mocked. They cover registration through login, malformed requests, verification expiry, email rollback, secret serialization, and listing permissions. Frontend tests cover authentication responses and session loading. Demo tests check the listings, title search, repeated seeding, and the seed seller’s unverified status.

These tests do not verify a deployed PostgreSQL database, email delivery, or S3 permissions.

## Hosting and current limits

Set `VITE_API_URL` to the public backend origin before building the frontend, with **no `/api` suffix**. Set backend `CORS_ALLOWED_ORIGINS` to the frontend origin. Alternatively, reverse-proxy the three API route prefixes to the backend and leave `VITE_API_URL` empty. The Vite development proxy is absent from a production build; an SPA fallback must not turn API requests into HTML.

The app handles listings and accounts; it has no checkout or in-app messaging. Listing retrieval and search are unpaginated. Hibernate currently uses `ddl-auto=update` rather than versioned database migrations.

The repo has no reproducible search benchmark or search index migration.
