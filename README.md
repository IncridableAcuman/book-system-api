# 📚 Book System (Fullstack Monorepo)

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-blue.svg)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-6.0-blue.svg)](https://www.typescriptlang.org/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-v4-38bdf8.svg)](https://tailwindcss.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-6379-dc382d.svg)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ed.svg)](https://www.docker.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A production-ready fullstack book management application built with **Spring Boot (Java 17)** and **React 19 (TypeScript + Vite + Tailwind CSS)**. The system includes robust JWT authentication with refresh token rotation, Redis token management, email password reset, complete user-specific book CRUD operations, genre/language filtering, and 1-click cloud deployment via **Render Blueprint** and **Neon PostgreSQL**.

---

## 🌟 Key Features

### 🔐 Authentication & Security
- **JWT Authentication**: Short-lived access tokens paired with secure `HttpOnly` refresh token cookies.
- **Token Rotation & Revocation**: Refresh tokens managed and blacklisted using Redis.
- **Password Recovery**: Secure password reset workflow powered by Gmail SMTP and time-limited tokens.
- **Password Hashing**: BCrypt encryption for user credentials.
- **CORS & Cookie Configuration**: Production-ready SameSite & Secure cookie controls.

### 📖 Book Management & Filtering
- **User-isolated CRUD**: Each authenticated user manages their own personal catalog of books.
- **Categorization**: Books classified by **Genre** (`SCIENCE`, `ARTISTIC`, `MOTIVATION`, `ROMANCE`, `FANTASY`, `HISTORICAL`) and **Language** (`EN`, `RU`, `UZB`).
- **Flexible Filters**: Filter books by ID, Author, Language, and Genre.
- **Input Validation**: Jakarta Bean Validation (`@Valid`, size limits, not blank, positive price/year).

### 💻 Modern Frontend Experience
- Built with **React 19**, **TypeScript**, and **Vite**.
- Styled with **Tailwind CSS v4** and **Lucide React** icons.
- Robust form handling with **React Hook Form** and **Zod** schema validation.
- Seamless authentication lifecycle with **Axios interceptors** handling automatic token refresh.

### 🚀 DevOps & Deployment
- **Multi-Stage Dockerfile**: Slim, optimized production build using Eclipse Temurin JRE 17 Alpine.
- **Docker Compose**: One-command local environment running PostgreSQL, Redis, and Backend.
- **Render Blueprint (`render.yaml`)**: Automated deployment of the Spring Boot container and React static site.

---

## 🛠️ Tech Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend** | Java 17, Spring Boot, Spring Security, Spring Data JPA, Spring Mail, Hibernate, JJWT |
| **Frontend** | React 19, TypeScript, Vite, Tailwind CSS v4, React Router DOM v7, React Hook Form, Zod, Axios |
| **Databases & Cache** | PostgreSQL (Neon / Local), Redis (Upstash / Local) |
| **DevOps & Cloud** | Docker, Docker Compose, Render (Web Service + Static Site CDN) |
| **Build Tools** | Gradle 8 (`./gradlew`), npm |

---

## 📂 Project Structure

```text
book-system-api/
├── backend/                        # Spring Boot Application
│   ├── Dockerfile                  # Multi-stage Docker build
│   ├── docker-compose.yaml         # Local PostgreSQL + Redis + Backend stack
│   ├── build.gradle                # Gradle dependencies & plugins
│   ├── .env.example                # Backend environment template
│   └── src/
│       ├── main/
│       │   ├── java/com/app/backend/
│       │   │   ├── config/         # Security, CORS, JWT, Redis configurations
│       │   │   ├── controller/     # AuthController, BookController
│       │   │   ├── dto/            # Request & response data transfer objects
│       │   │   ├── entity/         # User, Book JPA entities
│       │   │   ├── enums/          # Genres, Languages, Role
│       │   │   ├── exception/      # Global exception handler & custom exceptions
│       │   │   ├── repository/     # Spring Data JPA repositories
│       │   │   ├── service/        # AuthService, BookService, TokenService
│       │   │   └── util/           # JwtUtil, CookieUtil, MailUtil, DatabaseUrlParser
│       │   └── resources/
│       │       └── application.yaml # Spring configuration with env defaults
│       └── test/                   # Unit and integration tests
├── frontend/                       # React SPA Application
│   ├── index.html                  # HTML entry point
│   ├── package.json                # Dependencies and scripts
│   ├── vite.config.ts              # Vite configuration (with backend proxy)
│   ├── .env.example                # Frontend environment template
│   └── src/
│       ├── api/                    # Axios instance & API service functions
│       ├── components/             # Reusable UI components (books, common, layout)
│       ├── context/                # AuthContext & useAuth hook
│       ├── pages/                  # Auth (login, register, reset) & Dashboard pages
│       └── types/                  # TypeScript interfaces (auth, book)
└── render.yaml                     # Render Blueprint (Infrastructure as Code)
```

---

## ⚙️ Local Development Setup

### Prerequisites
- **Java 17 JDK** or higher
- **Node.js 18+** and **npm**
- **Docker & Docker Compose** (Optional, recommended for database and cache)

---

### Option 1: Quickstart with Docker Compose (Recommended)

Run PostgreSQL, Redis, and the Backend API together with a single command:

1. Copy and configure the environment variables:
   ```bash
   cp backend/.env.example backend/.env
   ```

2. Start all services:
   ```bash
   cd backend
   docker compose up -d
   ```

3. Start the Frontend development server:
   ```bash
   cd ../frontend
   npm install
   npm run dev
   ```

Frontend will be accessible at: `http://localhost:5173`  
Backend API will be accessible at: `http://localhost:8080`

---

### Option 2: Run Services Manually

#### 1. Backend Setup

1. Make sure **PostgreSQL** (port `5432`) and **Redis** (port `6379`) are running locally.
2. Configure `backend/.env` (or configure via environment variables):
   ```bash
   cp backend/.env.example backend/.env
   ```
3. Run the Spring Boot application using the Gradle wrapper:
   ```bash
   cd backend
   ./gradlew bootRun
   ```

#### 2. Frontend Setup

1. Install frontend dependencies:
   ```bash
   cd frontend
   npm install
   ```
2. Start the Vite dev server:
   ```bash
   npm run dev
   ```
> In local development, Vite proxies requests from `/api` directly to `http://localhost:8080` automatically.

---

## 🔑 Environment Variables Reference

### Backend (`backend/.env`)

| Variable | Description | Default / Example |
| :--- | :--- | :--- |
| `PORT` | Server listening port | `8080` |
| `DB_URL` | Neon or local PostgreSQL JDBC / connection URL | `jdbc:postgresql://localhost:5432/book_system` |
| `DB_USER` | Database username | `postgres` |
| `DB_PASS` | Database password | `password` |
| `REDIS_URL` | Redis connection URL (e.g., Upstash) | `rediss://default:xxx@ep-xxx.upstash.io:6379` |
| `REDIS_HOST` | Fallback Redis host (if `REDIS_URL` not set) | `localhost` |
| `REDIS_PORT` | Fallback Redis port | `6379` |
| `SECRET` | JWT secret key (min 32 characters) | *Random 256-bit key* |
| `ACCESS_TIME`| Access token expiry in ms (15 min) | `900000` |
| `REFRESH_TIME`| Refresh token expiry in ms (7 days) | `604800000` |
| `RESET_TIME` | Reset password token expiry in ms (15 min) | `900000` |
| `MAIL_HOST` | SMTP server host | `smtp.gmail.com` |
| `MAIL_PORT` | SMTP server port | `587` |
| `MAIL_USER` | Gmail address for sending emails | `your-email@gmail.com` |
| `MAIL_PASS` | Gmail App Password (16 characters) | `your-google-app-password` |
| `ALLOWED_ORIGINS` | Allowed CORS origins (comma-separated) | `http://localhost:5173,https://your-frontend.onrender.com` |
| `COOKIE_SECURE` | Set `true` in production (HTTPS) | `false` (local) / `true` (prod) |
| `COOKIE_SAME_SITE` | Cookie SameSite attribute | `Lax` (local) / `None` (prod cross-site) |

### Frontend (`frontend/.env`)

| Variable | Description | Default / Example |
| :--- | :--- | :--- |
| `VITE_API_URL` | Backend API base URL (leave blank in dev for proxy) | `https://book-system-backend.onrender.com` |

---

## 📡 API Endpoints Reference

### 🔐 Authentication (`/api/auth`)

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :---: |
| `POST` | `/api/auth/register` | Register a new user | ❌ |
| `POST` | `/api/auth/login` | Login with username/email & password | ❌ |
| `GET` | `/api/auth/refresh` | Refresh access token using `refreshToken` cookie | ❌ (Cookie) |
| `POST` | `/api/auth/logout` | Invalidate current session and clear cookies | ❌ (Cookie) |
| `POST` | `/api/auth/forgot-password` | Request password reset link to registered email | ❌ |
| `PUT` | `/api/auth/reset-password` | Reset password using email verification token | ❌ |

### 📖 Books (`/api/book`)

> All `/api/book` endpoints require a valid JWT `Bearer <token>` in the `Authorization` header.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/book` | Create a new book for current user |
| `GET` | `/api/book` | Get all books belonging to current user |
| `GET` | `/api/book/list/{id}` | Get book details by ID |
| `GET` | `/api/book/list/authors/{author}` | Filter user's books by author name |
| `GET` | `/api/book/list/languages/{languages}` | Filter books by language (`EN`, `RU`, `UZB`) |
| `GET` | `/api/book/list/genres/{genres}` | Filter books by genre (`SCIENCE`, `ARTISTIC`, etc.) |
| `PATCH`| `/api/book/edit/{id}` | Update book details by ID |
| `DELETE`| `/api/book/{id}` | Delete book by ID |

---

## 📝 Example Requests & Responses

### 1. Register User
`POST /api/auth/register`
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecretPassword123"
}
```

### 2. Login User
`POST /api/auth/login`
```json
{
  "emailOrUsername": "john_doe",
  "password": "SecretPassword123"
}
```
**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "role": "USER"
  }
}
```
*(Also sets `refreshToken` in an HttpOnly cookie)*

### 3. Create Book
`POST /api/book`  
*Header: `Authorization: Bearer <accessToken>`*
```json
{
  "title": "Clean Architecture",
  "content": "A Craftsman's Guide to Software Structure and Design",
  "author": "Robert C. Martin",
  "publisher": "Prentice Hall",
  "publishedYear": 2017,
  "price": 34.99,
  "genres": "SCIENCE",
  "languages": "EN"
}
```

---

## 🌐 Production Deployment (Render + Neon)

This project is pre-configured for seamless cloud deployment using **Render Blueprints** (`render.yaml`).

### Step 1: Create a PostgreSQL Database on Neon
1. Register for free at [neon.tech](https://neon.tech) and create a project.
2. In the Neon Dashboard, copy your connection string (either format works out-of-the-box):
   - PostgreSQL URL: `postgresql://neondb_owner:password@ep-sample-123.eu-central-1.aws.neon.tech/neondb?sslmode=require`
   - JDBC URL: `jdbc:postgresql://ep-sample-123.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require`

### Step 2: (Optional) Set Up Redis on Upstash
1. Register at [upstash.com](https://upstash.com) and create a free Redis database.
2. Copy the `rediss://...` REST or Redis connection URL.

### Step 3: Deploy via Render Blueprint
1. Push your repository to GitHub.
2. Go to your [Render Dashboard](https://dashboard.render.com).
3. Click **New +** > **Blueprint**.
4. Connect this repository (`IncridableAcuman/book-system-api`).
5. Render reads `render.yaml` and sets up both:
   - **`book-system-backend`** (Docker Web Service)
   - **`book-system-frontend`** (Static Site CDN)
6. Supply the required environment values in the Render setup screen:
   - `DB_URL`: Your Neon database connection URL
   - `DB_USER` & `DB_PASS`: (Optional if embedded in `DB_URL`)
   - `REDIS_URL`: (Optional) Upstash Redis URL
   - `MAIL_USER` & `MAIL_PASS`: Your Gmail address and Google App Password
   - `ALLOWED_ORIGINS`: Your Render frontend URL (e.g., `https://book-system-frontend.onrender.com`)
   - `VITE_API_URL`: Your Render backend URL (e.g., `https://book-system-backend.onrender.com`)

Render will automatically build and deploy both services!

---

## 👨‍💻 Author

**Izzat Abdusharipov**
- GitHub: [@IncridableAcuman](https://github.com/IncridableAcuman)

---

## 📄 License

This project is open-source and licensed under the [MIT License](LICENSE).
