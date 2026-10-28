# 📚 Book System API

A RESTful Book Management System built with Spring Boot that provides APIs for managing books, authors, categories, and users. The project follows clean architecture principles and includes validation, exception handling, and database integration.

## 🚀 Features

* 📖 Book Management (CRUD Operations)
* 👤 Author Management
* 🏷️ Category Management
* 🔍 Search and Filter Books
* ✅ Request Validation
* ⚠️ Global Exception Handling
* 🗄️ PostgreSQL Database Integration
* 🔐 Authentication & Authorization (if enabled)
* 📄 RESTful API Design

## 🛠️ Tech Stack

### Backend

* Java 21
* Spring Boot
* Spring Data JPA
* Spring Validation
* Spring Security (Optional)
* PostgreSQL
* Maven / Gradle

### Tools

* Postman
* Git & GitHub
* Docker (Optional)

---

## 📂 Project Structure

```text
src
├── controller
├── service
├── repository
├── entity
├── dto
├── exception
├── config
└── validation
```

## ⚙️ Installation

### Clone Repository

```bash
git clone https://github.com/IncridableAcuman/book-system-api.git
cd book-system-api
```

### Configure Database

Update your `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/book_system
spring.datasource.username=postgres
spring.datasource.password=password

spring.jpa.hibernate.ddl-auto=update
```

### Run Application

Maven:

```bash
./mvnw spring-boot:run
```

Gradle:

```bash
./gradlew bootRun
```

Application will start at:

```text
http://localhost:8080
```

## 📚 API Endpoints

### Books

| Method | Endpoint        | Description     |
| ------ | --------------- | --------------- |
| GET    | /api/books      | Get all books   |
| GET    | /api/books/{id} | Get book by id  |
| POST   | /api/books      | Create new book |
| PUT    | /api/books/{id} | Update book     |
| DELETE | /api/books/{id} | Delete book     |

### Authors

| Method | Endpoint          |
| ------ | ----------------- |
| GET    | /api/authors      |
| POST   | /api/authors      |
| PUT    | /api/authors/{id} |
| DELETE | /api/authors/{id} |

### Categories

| Method | Endpoint             |
| ------ | -------------------- |
| GET    | /api/categories      |
| POST   | /api/categories      |
| PUT    | /api/categories/{id} |
| DELETE | /api/categories/{id} |

## 📋 Example Request

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "category": "Programming",
  "publishedYear": 2008
}
```

## 🌐 Deployment (Render + Neon PostgreSQL)

### 1. Neon PostgreSQL Sozlash (Database)
1. [neon.tech](https://neon.tech) saytida ro'yxatdan o'ting va yangi loyiha yarating.
2. Dashboard-dan **Connection Details** qismiga o'ting.
3. PostgreSQL ulanish satrini nusxalang:
   - Misol: `postgresql://neondb_owner:password@ep-sample-123.eu-central-1.aws.neon.tech/neondb?sslmode=require`
   - Yoki JDBC formati: `jdbc:postgresql://ep-sample-123.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require`
   *(Ikkala format ham loyihada avtomatik tarzda qo'llab-quvvatlanadi)*

### 2. Render-ga Deployment qilish

Loyiha ildizida `render.yaml` (Blueprint) fayli mavjud. Render-da 2 xil usulda deploy qilish mumkin:

#### A variant: Render Blueprint orqali (Tavsiya etiladi - 1 marta bosish bilan)
1. GitHub-ga o'zgarishlarni push qiling.
2. [render.com](https://render.com) dashboard-ga kiring.
3. **New +** tugmasini bosing va **Blueprint** ni tanlang.
4. Repository-ni ulang. Render `render.yaml` faylini topib, backend va frontend xizmatlarini avtomatik ro'yxatga oladi.
5. So'ralgan environment o'zgaruvchilarini kiriting (`DB_URL`, `DB_USER`, `DB_PASS`, `MAIL_USER`, `MAIL_PASS`).

#### B variant: Qo'lda (Manual) sozlash

**Backend (Web Service):**
- **Type**: Web Service (Docker)
- **Root Directory**: `backend` (yoki context sifatida `backend`)
- **Runtime**: Docker (`backend/Dockerfile`)
- **Environment Variables**:
  - `PORT`: `8080`
  - `DB_URL`: Neon connection string yoki JDBC URL
  - `DB_USER`: Neon foydalanuvchi nomi (agar URL ichida bo'lmasa)
  - `DB_PASS`: Neon paroli (agar URL ichida bo'lmasa)
  - `SECRET`: JWT secret (kamida 32 ta belgi)
  - `ACCESS_TIME`: `900000`
  - `REFRESH_TIME`: `604800000`
  - `RESET_TIME`: `900000`
  - `MAIL_USER`: Gmail emailingiz
  - `MAIL_PASS`: Google App Password
  - `ALLOWED_ORIGINS`: Frontend manzili (masalan, `https://your-frontend.onrender.com`)
  - `COOKIE_SECURE`: `true`
  - `COOKIE_SAME_SITE`: `None`
  - `REDIS_URL`: *(Ixtiyoriy)* Upstash Redis yoki boshqa Redis xizmati URL manzili

**Frontend (Static Site):**
- **Type**: Static Site
- **Root Directory**: `frontend`
- **Build Command**: `npm run build`
- **Publish Directory**: `dist`
- **Environment Variables**:
  - `VITE_API_URL`: Backend Render URL (masalan: `https://book-system-backend.onrender.com`)
- **Redirects/Rewrites**: `/*` -> `/index.html` (loyihadagi `_redirects` fayli orqali avtomatik ishlaydi)

---

## 🔮 Future Improvements

* JWT Authentication
* Role-Based Authorization
* Docker Support
* Unit & Integration Tests
* Swagger/OpenAPI Documentation
* Pagination & Sorting
* Book Reviews and Ratings

## 👨‍💻 Author

Izzat Abdusharipov

GitHub:
https://github.com/IncridableAcuman

## 📄 License

This project is licensed under the MIT License.
