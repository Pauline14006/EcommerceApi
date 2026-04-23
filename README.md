# EcommerceApi - Lab 7

A simple RESTful API for an e-commerce product catalog built with Spring Boot.
Uses in-memory storage (no database needed).

---

## How to Run

1. Make sure you have **Java 21** installed
2. Open the project folder in VS Code or IntelliJ
3. Run this command in the terminal:

```
./gradlew bootRun
```

4. The API will start at: `http://localhost:8080`

---

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/v1/products | Get all products |
| GET | /api/v1/products/{id} | Get one product by ID |
| GET | /api/v1/products/filter?filterType=category&filterValue=Electronics | Filter products |
| POST | /api/v1/products | Create a new product |
| PUT | /api/v1/products/{id} | Replace a product |
| PATCH | /api/v1/products/{id} | Partially update a product |
| DELETE | /api/v1/products/{id} | Delete a product |

---

## Sample Requests

### GET all products
```
GET http://localhost:8080/api/v1/products
```

### GET one product
```
GET http://localhost:8080/api/v1/products/1
```

### Filter by category
```
GET http://localhost:8080/api/v1/products/filter?filterType=category&filterValue=Electronics
```

### Filter by name
```
GET http://localhost:8080/api/v1/products/filter?filterType=name&filterValue=headphones
```

### Filter by max price
```
GET http://localhost:8080/api/v1/products/filter?filterType=price&filterValue=50
```

### POST - Create product
```json
POST http://localhost:8080/api/v1/products
Content-Type: application/json

{
  "name": "New Product",
  "description": "A great product",
  "price": 29.99,
  "category": "Electronics",
  "stockQuantity": 10,
  "imageUrl": "pics/product1.png"
}
```

### PUT - Replace product
```json
PUT http://localhost:8080/api/v1/products/1
Content-Type: application/json

{
  "name": "Updated Name",
  "description": "Updated description",
  "price": 49.99,
  "category": "Electronics",
  "stockQuantity": 20,
  "imageUrl": "pics/product1.png"
}
```

### PATCH - Partial update
```json
PATCH http://localhost:8080/api/v1/products/1
Content-Type: application/json

{
  "price": 39.99
}
```

### DELETE - Delete product
```
DELETE http://localhost:8080/api/v1/products/1
```

---

## HTTP Status Codes Used

| Scenario | Status Code |
|----------|-------------|
| Successful GET | 200 OK |
| Successful POST | 201 Created |
| Successful DELETE | 204 No Content |
| Product not found | 404 Not Found |
| Invalid input | 400 Bad Request |
| Server error | 500 Internal Server Error |

---

## Known Limitations

- Data is stored in memory only — all data resets when the app restarts
- No database or file persistence
- No authentication or authorization

---

## Dependencies

- Spring Boot 3.5.0
- Spring Web
- Lombok
