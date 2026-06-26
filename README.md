# SPAIGI Backend

Lightweight Spring Boot contact backend for the SPAIGI website.

## Run Locally

```bash
cp .env.example .env
mvn spring-boot:run
```

The backend automatically imports `.env` from the `spaigi-backend` root folder.
Required variables:

```env
RESEND_API_KEY=re_your_api_key
CONTACT_FROM_EMAIL=SPAIGI Website <noreply@spaigi.uz>
CONTACT_TO_EMAIL=contact@spaigi.uz
CONTACT_SUBJECT_PREFIX=SPAIGI Website
```

`CONTACT_FROM_EMAIL` must use a domain verified in Resend.

## Run With Docker

```bash
docker compose up -d --build
docker compose ps
docker compose logs -f backend
```

The compose file reads secrets from `.env` and exposes the backend on port `8080`.

## CI/CD

The GitHub Actions workflow in `.github/workflows/deploy.yml` builds the backend with Java 21,
then deploys to Hetzner over SSH.

Required GitHub repository secrets:

```text
SERVER_HOST
SERVER_USER
SERVER_SSH_KEY
```

The first server setup must be done once:

```bash
cd ~
git clone <backend-repository-url> spaigi-backend
cd ~/spaigi-backend
nano .env
docker compose up -d --build
```

Keep `.env` only on the server. It is intentionally ignored by git.

Nginx on the frontend server should proxy `/api/` to this backend:

```nginx
location /api/ {
  proxy_pass http://127.0.0.1:8080/api/;
  proxy_set_header Host $host;
  proxy_set_header X-Real-IP $remote_addr;
  proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
  proxy_set_header X-Forwarded-Proto $scheme;
}
```

## Endpoint

```http
POST /api/contact
```

The frontend should proxy `/api/` to this service.
