# Cloud Pre-deployment Checklist

This checklist should be completed before deploying the Issue Tracker API to AWS Lightsail, EC2, or a low-cost VPS.

## 1. Files and Secrets

- Do not commit `.env`.
- Commit `.env.example` only.
- Use strong values for database passwords and `JWT_SECRET` on the server.
- Do not upload `.env` when sharing the project as a ZIP file.

## 2. Recommended Cloud Compose File

Use `docker-compose.prod.yml` for cloud deployment.

```bash
cp .env.example .env
# edit .env before running

docker compose -f docker-compose.prod.yml up -d --build
```

Why use the production compose file:

- The MySQL container is not exposed to the public internet.
- The Spring Boot application runs with the `prod` profile.
- Containers restart automatically unless stopped manually.

## 3. Server Firewall Rules

For the first portfolio deployment, open only the minimum ports:

| Port | Purpose | Public? |
|---|---|---|
| 22 | SSH | Restrict to your IP if possible |
| 8080 | Spring Boot API / Swagger | Yes, for portfolio testing |
| 3306 | MySQL | No |

When you add Nginx and HTTPS later, open `80` and `443`, then close direct public access to `8080`.

## 4. First ADMIN Account

Normal signup creates `USER` accounts only.

If you need one initial admin account for a cloud demo, enable admin bootstrap once in `.env`:

```env
ADMIN_BOOTSTRAP_ENABLED=true
ADMIN_USER_ID=admin01
ADMIN_EMAIL=admin@example.com
ADMIN_NAME=Admin
ADMIN_PASSWORD=change-this-admin-password
```

Start the application once. After the admin account is created, set this back to:

```env
ADMIN_BOOTSTRAP_ENABLED=false
```

Then restart the app:

```bash
docker compose -f docker-compose.prod.yml up -d
```

## 5. Health Check

After deployment, verify the app:

```bash
docker ps
docker compose -f docker-compose.prod.yml logs -f app
```

```http
GET http://<server-ip>:8080/actuator/health
```

Expected response:

```json
{
  "status": "UP"
}
```

## 6. Swagger Check

Swagger UI:

```text
http://<server-ip>:8080/swagger-ui/index.html
```

Test flow:

1. Login with an existing user.
2. Copy the `accessToken`.
3. Click `Authorize`.
4. Paste the token.
5. Call a protected API.

## 7. Before Paying for Cloud

Run these commands locally one more time:

```bash
mvn test
docker compose up -d --build
docker compose logs -f app
```

Then test:

- `/actuator/health`
- `/swagger-ui/index.html`
- `/api/auth/signup`
- `/api/auth/login`
- Protected API with JWT
- ADMIN-only API with ADMIN token
- ADMIN-only API with USER token should return `403`

## 8. Shutdown to Avoid Cost

When you no longer need the server, stop or delete the cloud instance from the cloud provider console.

On the server, you can stop containers with:

```bash
docker compose -f docker-compose.prod.yml down
```

This stops containers but keeps the MySQL volume.
