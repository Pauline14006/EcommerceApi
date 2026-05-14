# Lab 8 – Database Integration & Fetch API

**Authors:** P.M A. Gallamora · P.G C. Torres  
**Course:** WS101 – Web Services

---

## Overview

This lab transitions the e-commerce backend from in-memory `ArrayList` storage (Lab 7) to a persistent **MySQL** database using **Spring Data JPA / Hibernate**. The frontend is updated to consume all API endpoints dynamically using the **Fetch API** with proper `async/await` and error handling.

---

## Database Schema

Three entities are defined, modelling real-world e-commerce relationships.

### `categories`
| Column | Type         | Constraints            |
|--------|--------------|------------------------|
| id     | BIGINT       | PK, AUTO_INCREMENT     |
| name   | VARCHAR(255) | NOT NULL, UNIQUE       |

### `products`
| Column       | Type    | Constraints                         |
|--------------|---------|-------------------------------------|
| id           | BIGINT  | PK, AUTO_INCREMENT                  |
| name         | VARCHAR | NOT NULL                            |
| description  | TEXT    |                                     |
| price        | DOUBLE  | NOT NULL                            |
| stock_quantity | INT   | NOT NULL                            |
| image_url    | VARCHAR |                                     |
| category_id  | BIGINT  | FK → categories(id)  *(Many-to-One)*|

### `orders`
| Column        | Type      | Constraints        |
|---------------|-----------|--------------------|
| id            | BIGINT    | PK, AUTO_INCREMENT |
| customer_name | VARCHAR   | NOT NULL           |
| created_at    | TIMESTAMP | NOT NULL           |

### `order_items`
| Column     | Type   | Constraints                     |
|------------|--------|---------------------------------|
| id         | BIGINT | PK, AUTO_INCREMENT              |
| order_id   | BIGINT | FK → orders(id)  *(Many-to-One)*|
| product_id | BIGINT | FK → products(id)               |
| quantity   | INT    | NOT NULL                        |
| unit_price | DOUBLE | NOT NULL                        |

### Relationships

```
Category  ──< Product     (One-to-Many)
Order     ──< OrderItem   (One-to-Many)
Product   ──< OrderItem   (One-to-Many, via FK)
```

---

## API Endpoints

All endpoints are prefixed with `/api/v1/products`.

| Method | URL                  | Description                          |
|--------|----------------------|--------------------------------------|
| GET    | `/`                  | Get all products                     |
| GET    | `/{id}`              | Get product by ID                    |
| GET    | `/filter`            | Filter by `filterType` & `filterValue` (query params) |
| POST   | `/?categoryName=X`   | Create a new product                 |
| PUT    | `/{id}?categoryName=X` | Full update of a product           |
| PATCH  | `/{id}`              | Partial update of a product          |
| DELETE | `/{id}`              | Delete a product                     |

---

## Setup Instructions

### 1. Create the MySQL Database

```sql
CREATE DATABASE ecommerce_db;
```

### 2. Configure Credentials

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=<your_username>
spring.datasource.password=<your_password>
```

### 3. Run the Backend

```bash
./gradlew bootRun
```

Hibernate will auto-create the tables on first run (`ddl-auto=update`).

### 4. Open the Frontend

Open `frontend/index.html` with **VS Code Live Server** on port `5500`.

---

## Git Workflow

```
main
 └── feat:db-integration   ← All Lab 8 changes
```

Branches are NOT deleted after merge per lab requirements.


### Step 2 — Login
```
POST http://localhost:8080/login
Content-Type: application/x-www-form-urlencoded

username=testadmin&password=password123
```
Expected: `302 Redirect` — Check the **Cookies** tab in Postman. You should see a `JSESSIONID` cookie set.

> ⚙️ In Postman Settings: make sure **"Automatically follow redirects"** and **"Save cookies"** are turned ON.

---

### Step 3 — Access a protected endpoint (with session)
```
POST http://localhost:8080/api/v1/products
Content-Type: application/json

{
  "name": "Test Product",
  "price": 25.00,
  "categoryName": "Electronics",
  "stockQuantity": 10
}
```
Expected: `201 Created` — product is created ✅

---

### Step 4 — Delete the cookie (simulate logout)

In Postman, go to **Cookies** → delete the `JSESSIONID` cookie manually.

Then try the same request again:
```
POST http://localhost:8080/api/v1/products
```
Expected: `401 Unauthorized` or redirect to login ✅

---

### Step 5 — Test validation error
```
POST http://localhost:8080/api/v1/products
Content-Type: application/json

{
  "name": "X",
  "price": -5.00,
  "categoryName": "Electronics",
  "stockQuantity": -1
}
```
Expected: `400 Bad Request` with error details showing which fields failed ✅

