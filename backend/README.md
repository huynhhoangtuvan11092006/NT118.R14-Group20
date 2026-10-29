# BandUp Backend — Authentication & User Profile

Node.js 22+ · Express 5 · TypeScript · PostgreSQL 16 · Prisma 6.19 · Argon2id · JWT (HS256).

> This package extends the existing **Backend Foundation** and preserves the initial five-table migration. Authentication adds a **sixth table**, `refresh_tokens`, via an additive migration.

## 1. Run in Windows PowerShell

From `E:\cc\NT118.R14-Group20\backend` (or your own backend path):

```powershell
# Keep your existing .env if you already have one.
# New checkout only: Copy-Item .env.example .env
# Generate TWO DIFFERENT secrets; paste each value into its matching .env line:
node -e "console.log(require('node:crypto').randomBytes(48).toString('base64url'))"
node -e "console.log(require('node:crypto').randomBytes(48).toString('base64url'))"
```

Edit `.env` and **add** the two lines below to your existing PostgreSQL configuration:

```dotenv
JWT_ACCESS_SECRET=YOUR_FIRST_GENERATED_RANDOM_STRING
JWT_REFRESH_SECRET=YOUR_SECOND_DIFFERENT_GENERATED_RANDOM_STRING
```

The secrets must be different and at least 32 characters. The server rejects the `CHANGE_ME...` placeholders from `.env.example`. Do not add them to Git or paste them into chats. `.env` is gitignored.

```powershell
# Docker Desktop must be running
# Start your existing PostgreSQL data volume (do NOT use docker compose down -v)
docker compose up -d db

# First install after this feature (your previous registry was unreachable):
npm install --registry=https://registry.npmjs.org/ --no-audit --no-fund

# Checks and additive migration
npm run db:validate
npm run db:migrate
npm run db:generate
npm run db:verify
npm run typecheck
npm run lint
npm run test:contracts

# Start API
npm run dev
```

Health check: `GET http://localhost:3000/health` should return `{"status":"ok","database":"connected"}`.

**Important Git note:** `npm install` updates the committed `package-lock.json` for `argon2`, `jsonwebtoken`, `@types/jsonwebtoken`. Include that updated lockfile in the PR. Do not run `npm ci` until the lockfile has been refreshed. Do not use `prisma db push` in place of the migration.

## 2. Endpoints

| Method | URL | Auth | Request | Success |
|---|---|---|---|---|
| POST | `/auth/register` | Public | `email`, `password`, `fullName` | 201 user + token pair |
| POST | `/auth/login` | Public | `email`, `password` | 200 user + token pair |
| POST | `/auth/refresh` | Public (refresh JWT in body) | `refreshToken` | 200 rotated pair |
| POST | `/auth/logout` | Public (refresh JWT in body) | `refreshToken` | 204 |
| GET | `/users/me` | **Bearer access JWT** | - | 200 user |
| PUT | `/users/me` | **Bearer access JWT** | any of `fullName`, `avatarUrl`, `currentBand`, `expectedExamDate`, `dailyStudyMinutes` | 200 user |
| PUT | `/users/target-band` | **Bearer access JWT** | `targetBand` | 200 user |

All JSON fields are camelCase. The login email is normalized to lowercase, and newly registered account emails are stored lowercase. `passwordHash` is **never** included in API responses.

- Password: 8–128 characters, Argon2id hash with per-password random salt.
- Band scores: number from 0 to 9 in steps of 0.5 (e.g., `6.5`, `7`, `7.5`).
- `expectedExamDate`: `YYYY-MM-DD`; profile nullable fields can be cleared with `null`.
- JWT access token: 15 minutes. Refresh token: 7 days, rotated on use and stored as a SHA-256 digest in `refresh_tokens`. Refresh token reuse is rejected.
- Private routes: `Authorization: Bearer <accessToken>`.
- Error statuses: 400 validation, 401 unauthorized / invalid credentials, 409 duplicate email, 500 unexpected server error.
- An existing access token remains valid until its 15-minute expiry after logout. Logout revokes the specified refresh token, not every device session.

### Request samples

Register:

```http
POST http://localhost:3000/auth/register
Content-Type: application/json

{"email":"student@example.com","password":"YourExamplePassword#123","fullName":"Nguyen Van A"}
```

Response (shortened):

```json
{
  "user": {"id": "uuid", "email": "student@example.com", "fullName": "Nguyen Van A"},
  "accessToken": "<signed-access-JWT>",
  "refreshToken": "<signed-refresh-JWT>",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

Login:

```http
POST http://localhost:3000/auth/login
Content-Type: application/json

{"email":"student@example.com","password":"YourExamplePassword#123"}
```

Private profile:

```http
GET http://localhost:3000/users/me
Authorization: Bearer <accessToken>
```

Update personal profile:

```http
PUT http://localhost:3000/users/me
Authorization: Bearer <accessToken>
Content-Type: application/json

{"fullName":"Nguyen Van B","dailyStudyMinutes":60,"expectedExamDate":"2027-05-01"}
```

Update target band:

```http
PUT http://localhost:3000/users/target-band
Authorization: Bearer <accessToken>
Content-Type: application/json

{"targetBand":7.5}
```

Refresh (old token cannot be used again):

```http
POST http://localhost:3000/auth/refresh
Content-Type: application/json

{"refreshToken":"<refresh-JWT>"}
```

Logout:

```http
POST http://localhost:3000/auth/logout
Content-Type: application/json

{"refreshToken":"<latest-refresh-JWT>"}
```

## 3. Postman

Import `backend/postman/BandUp_Auth.postman_collection.json` into Postman. Set collection variables:

- `baseUrl`: `http://localhost:3000`
- `testEmail`: a **new unique email** each run (register returns 409 if reused).
- `testPassword`: a sample password, not a personal account password.

Run requests **01 through 08** in order. Register/login/refresh automatically set the collection's token variables. The test `03 Protected without token` expects HTTP **401**. `06 Target Band` expects the band saved as **7.5**.

Do not commit or share exported collections that contain real auth tokens or personal passwords.

## 4. Integration tests against real database

With Docker DB healthy, generated Prisma Client present, and `.env` set:

```powershell
npm run test:auth
```

This builds the TypeScript project, starts an ephemeral Express listener, and tests registration, duplicate email, hashed Argon2id password persistence, login, missing/forged bearer token rejection, profile/target-band updates, token rotation, replay rejection and logout. Creates a unique `@example.invalid` user and removes that user and its refresh tokens after completion. **Run only on a disposable development/test database**, not production; avoid running the suite simultaneously with other schema migrations.

## 5. Database and security notes

- The original migration `20261029000000_init` must remain unchanged. The new `20261029010000_auth_refresh_tokens` migration is **additive**.
- `users` now has one-to-many `refresh_tokens`, with cascading deletion. `db:verify` expects **6 tables and 5 foreign keys**.
- Local auth is independent from the app's guest mode. Firebase/Social sign-in is not implemented in this task; existing rows without a password hash cannot log in using local password.
- This starter API is for local development. Before public deployment: use HTTPS, rate limiting on auth routes, email verification, password reset, refresh-token replay response/central session revocation policy, account recovery, secret rotation, log monitoring, and managed secrets. Do not put tokens into app logs or plain SharedPreferences; use a secure mobile storage solution.
- Android Emulator reaches Windows host using `http://10.0.2.2:3000` for local emulator tests (physical devices need an accessible host address and appropriate firewall/security settings). Production requires HTTPS.
- PostgreSQL credentials in `.env.example` are only for local development; rotate for deployment.
- Prisma and @prisma/client are pinned to 6.19.0 to avoid breaking major-version configuration changes.

## 6. Troubleshooting

- `Invalid environment variables: JWT_ACCESS_SECRET...`: add two different, random secrets to `.env`, save and restart the server.
- `npm install ETIMEDOUT 192.168.137.134:4873`: use `npm install --registry=https://registry.npmjs.org/`.
- `P1001`: start Docker Desktop / `docker compose up -d db` and verify `DATABASE_URL`.
- `P2021` missing `refresh_tokens`: run `npm run db:migrate` then `npm run db:generate`.
- `EADDRINUSE`: stop old `npm run dev` process or change `PORT`.
- `401` on private route: use *access* JWT, not refresh JWT, in `Authorization` header.
- `401` on `/auth/refresh`: token expired, revoked or already used; log in again.
