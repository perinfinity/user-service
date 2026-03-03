# auth-api

Handles user registration, authentication (password-based and 2FA via email code), and JWT token issuance. Runs on port **8080**.

## Tech Stack

- Spring Boot 3.4.13 · Java 17
- Spring Security + JWT (`jjwt 0.11.5`)
- Spring Data JPA — H2 (dev) / MySQL (prod)
- SpringDoc OpenAPI 2.7.0 (Swagger UI)

## Running the Service

```bash
mvn spring-boot:run
```

The service starts at `http://localhost:8080`.

## API Documentation (Swagger UI)

Interactive docs are available once the service is running:

| Resource | URL |
|----------|-----|
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| OpenAPI YAML | `http://localhost:8080/v3/api-docs.yaml` |

All Swagger endpoints are publicly accessible (no JWT required).

## Endpoints

Base path: `/api/auth`

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/api/auth/signup` | Public | Register a new user. Returns a JWT token and user details. |
| `POST` | `/api/auth/login` | Public | Authenticate with email + password. Returns a JWT token. |
| `POST` | `/api/auth/send-code` | Public | Send a 2FA verification code to the given email address. |
| `POST` | `/api/auth/verify-code` | Public | Verify the 2FA code and receive a JWT token. |
| `GET`  | `/api/admin/**` | `ROLE_ADMIN` | Admin-only routes. |

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | H2 in-memory | JDBC connection URL |
| `DB_USER` | — | Database username |
| `DB_PASS` | — | Database password |
| `DB_DRIVER` | H2 driver | JDBC driver class |
| `JWT_SECRET_KEY` | — | Base64-encoded JWT signing key |

## Test unitaire

Redige toujours des test unitaire pour chaque classe

## Commentaire

Ne commante que les code complex mais renomme toujours bien les methode et variable sans forcement les commenter. 
