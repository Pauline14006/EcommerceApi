package com.ws101.gallamora.torres.EcommerceApi.controller;

import com.ws101.gallamora.torres.EcommerceApi.model.Product;
import com.ws101.gallamora.torres.EcommerceApi.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for product API endpoints.
 * Handles all requests going to /api/v1/products.
 *
 * @author P.M A. Gallamora
 * @author P.G C. Torres
 */
@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Returns all products in the list.
     *
     * @return ResponseEntity with 200 OK and list of all products
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Returns a single product by its ID.
     *
     * @param id the product ID from the URL path
     * @return ResponseEntity with 200 OK and the product, or 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Filters products by a given type and value.
     *
     * @param filterType  what to filter by (category, name, price)
     * @param filterValue the value to filter with
     * @return ResponseEntity with 200 OK and matching products, or 400 Bad Request
     */
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

    /**
     * Creates a new product.
     *
     * @param product the product data from the request body
     * @return ResponseEntity with 201 Created and the new product, or 400 Bad
     *         Request
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        // Validate name - required, minimum 2 characters
        if (product.getName() == null || product.getName().length() < 2) {
            return ResponseEntity.badRequest().build();
        }
        // Validate price - must be positive
        if (product.getPrice() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        // Validate category - required
        if (product.getCategory() == null || product.getCategory().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        // Validate stock - must be non-negative
        if (product.getStockQuantity() < 0) {
            return ResponseEntity.badRequest().build();
        }
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Replaces the entire product with new data.
     *
     * @param id      the product ID from the URL path
     * @param product the new product data from the request body
     * @return ResponseEntity with 200 OK and updated product, or 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Optional<Product> updated = productService.updateProduct(id, product);
        if (updated.isPresent()) {
            return ResponseEntity.ok(updated.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Partially updates a product.
     *
     * @param id      the product ID from the URL path
     * @param product the partial product data from the request body
     * @return ResponseEntity with 200 OK and updated product, or 404 Not Found
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Product> patchProduct(@PathVariable Long id, @RequestBody Product product) {
        Optional<Product> patched = productService.patchProduct(id, product);
        if (patched.isPresent()) {
            return ResponseEntity.ok(patched.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a product by ID.
     *
     * @param id the product ID from the URL path
     * @return ResponseEntity with 204 No Content if deleted, or 404 Not Found
     */
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