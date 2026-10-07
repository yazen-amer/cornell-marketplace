# Cornell Marketplace

A marketplace for Cornell students with verified email signup, JWT login, listings, search, and S3 photos. Backend: Java 17, Spring Boot, PostgreSQL. Frontend: React, TypeScript, Vite.

## Configuration and local setup

The backend reads **process environment variables**; Spring Boot does not automatically load `backend/.env`. Set the values from `backend/.env.example` in your shell or hosting dashboard. Required names are `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `AWS_ACCESS_KEY`, `AWS_SECRET_KEY`, `AWS_BUCKET`, and `AWS_REGION`.

`JWT_SECRET` must be a Base64-encoded random key of at least 32 bytes. `JWT_EXPIRATION` is milliseconds and defaults to 3600000. Verification requires working SMTP settings: `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, and `MAIL_FROM` (a sender permitted by the mail provider). Set connection credentials for your own PostgreSQL database; Hibernate currently uses `ddl-auto=update`.

```bash
cd backend
# Windows: .\mvnw.cmd spring-boot:run
bash ./mvnw spring-boot:run
```

In a second terminal:

```bash
cd frontend
npm ci
npm run dev
```

The frontend's Vite server proxies `/auth`, `/users`, and `/listings` to `http://localhost:8080`. Signup sends a six-digit verification code to the Cornell address. The user must verify before logging in. Login and verification normalize email case and surrounding whitespace consistently.

## Production routing

Set **VITE_API_URL** to the public HTTPS backend origin before building the frontend, such as `https://your-api.example.com` (no `/api` suffix: backend routes begin with `/auth`, `/users`, and `/listings`). Set backend `CORS_ALLOWED_ORIGINS` to the frontend origin. Rebuild after changing Vite environment variables. Never use `localhost` as the API address in a hosted frontend: it refers to each visitor's computer.

Alternatively, configure your host to reverse-proxy `/auth`, `/users`, and `/listings` to the backend, preserving paths, and leave `VITE_API_URL` empty. The Vite dev proxy is not included in a production build. Configure SPA fallback for frontend navigation routes only; do not send API requests to the frontend HTML page.

## Failure recovery and API behavior

If sending the registration email fails, registration rolls back and returns HTTP 503; retry after fixing SMTP. Failed resends preserve the previously issued code. Accounts already stranded by earlier failed sends can request another code on `/verify` once mail configuration is fixed.

Missing/invalid auth fields return 400; duplicate emails return 409; unverified login returns 403; bad credentials and invalid/expired sessions return 401. Email delivery has bounded SMTP timeouts. Session network failures show a retry action and preserve the token; auth rejections clear it. Verification codes and password hashes are excluded from user/listing JSON.

Register through `/auth/register`; the old direct `POST /users` endpoint was removed because it bypassed verified registration. Display usernames are separate from the email used as Spring Security's identity. Missing listings return 404 and attempts to modify someone else's listing return 403.

## Tests

```bash
cd backend
# Windows: .\mvnw.cmd test
bash ./mvnw test
cd ../frontend
npm test
npm run build
npm run lint
```

Backend tests boot the actual application with an isolated H2 database, real BCrypt/JWT/security filters, and mocked outbound email/S3 calls. They exercise register ? verify ? login ? profile, malformed requests, duplicate signup, expired codes, mail rollback, secret serialization, and listing permissions. No production database, SMTP, AWS or personal credentials are used. Frontend tests cover auth error responses, session loading, token rejection and stale responses. Production PostgreSQL, mail delivery, and S3 uploads still require hosting credentials and a live smoke test.
