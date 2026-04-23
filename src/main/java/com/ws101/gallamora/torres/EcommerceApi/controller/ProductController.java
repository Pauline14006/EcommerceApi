package com.ws101.gallamora.torres.EcommerceApi.controller;

import com.ws101.gallamora.torres.EcommerceApi.model.Product;
import com.ws101.gallamora.torres.EcommerceApi.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for product API endpoints
 * handles all requests going to /api/v1/products
 */
@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /** GET /api/v1/products - Returns all products */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /** GET /api/v1/products/{id} - Returns one product by ID */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /** GET /api/v1/products/filter - Filters products */
    @GetMapping("/filter")
    public ResponseEntity<List<Product>> filterProducts(
            @RequestParam String filterType,
            @RequestParam String filterValue) {
        if (filterType == null || filterType.isBlank() || filterValue == null || filterValue.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        List<Product> results = productService.filterProducts(filterType, filterValue);
        return ResponseEntity.ok(results);
    }

    /** POST /api/v1/products - Creates a new product */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (product.getPrice() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (product.getCategory() == null || product.getCategory().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (product.getStockQuantity() < 0) {
            return ResponseEntity.badRequest().build();
        }
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** PUT /api/v1/products/{id} - Replaces entire product */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Optional<Product> updated = productService.updateProduct(id, product);
        if (updated.isPresent()) {
            return ResponseEntity.ok(updated.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /** PATCH /api/v1/products/{id} - Partially updates a product */
    @PatchMapping("/{id}")
    public ResponseEntity<Product> patchProduct(@PathVariable Long id, @RequestBody Product product) {
        Optional<Product> patched = productService.patchProduct(id, product);
        if (patched.isPresent()) {
            return ResponseEntity.ok(patched.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /** DELETE /api/v1/products/{id} - Deletes a product */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}