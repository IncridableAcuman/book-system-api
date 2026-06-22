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
