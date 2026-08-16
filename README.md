# Cornell Marketplace
Full-stack marketplace app for Cornell students to list, browse, and manage items for sale. Built to get hands-on with authentication, authorization, and cloud storage in a real full-stack setup.

## What it does
- Users sign up, log in, and post listings with photos
- Only the person who created a listing can edit or delete it (enforced server-side, not just hidden in the UI)
- Search across listings
- Images upload directly to S3 instead of being stored in the DB

## Stack
Backend: Java, Spring Boot, Spring Security, JWT, PostgreSQL, Hibernate/JPA
Frontend: React, TypeScript
Storage: AWS S3

## Screenshots
[listings page]
[create listing form]

## Running it locally

You'll need Java 17+, Node 18+, PostgreSQL, and AWS credentials (or a mocked S3 setup).

Set these env vars first: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `AWS_ACCESS_KEY`, `AWS_SECRET_KEY`, `S3_BUCKET`

Backend:
\`\`\`bash
cd backend
mvn spring-boot:run
\`\`\`

Frontend:
\`\`\`bash
cd frontend
npm install
npm run dev
\`\`\`

## Notes
Seeded with [XXX] sample listings to test search and load. Auth uses stateless JWTs — no server-side sessions.

Live: [link]
