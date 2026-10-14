# Hospital Management System - Backend

Spring Boot 3.4 REST API with Spring Security 6, JWT Authentication, and MySQL / Cloud Database.

## Features
- **Online Cloud Database**: Pre-configured for Aiven Cloud MySQL with SSL.
- **Render Ready**: Includes `Dockerfile` and `render.yaml` for 1-click or automated web service deployment.
- **CORS Configured**: Allows cross-origin requests from Vercel frontends and local environments.
- **Automatic Seed Data**: Bootstraps administrator, doctors, departments, and sample records on first startup.

## Environment Variables

| Variable | Description | Default (Pre-configured) |
|---|---|---|
| `PORT` | Web server listening port | `8080` (Auto set by Render) |
| `SPRING_DATASOURCE_URL` | JDBC Connection URL | `jdbc:mysql://mysql-2ae0a1ea-hospitaldatabse.h.aivencloud.com:21813/defaultdb?sslMode=REQUIRED&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `avnadmin` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | Set in Render Environment Variables |
| `JWT_SECRET` | 256-bit secret key for JWT signing | Pre-configured secure fallback |

## Deploy to Render

### Option A: Using Web Service (Standard)
1. Go to [Render Dashboard](https://dashboard.render.com).
2. Click **"New +"** -> **"Web Service"**.
3. Connect your repository: `Hospital_backend`.
4. Choose **Docker** as the Environment / Runtime.
5. In Environment Variables, you can optionally configure:
   - `PORT`: `8080`
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `JWT_SECRET`
6. Click **Create Web Service**.

### Option B: Using Blueprints
1. In Render, click **"New +"** -> **"Blueprint"**.
2. Select repository: `Hospital_backend`.
3. Render will read `render.yaml` and configure everything automatically.
