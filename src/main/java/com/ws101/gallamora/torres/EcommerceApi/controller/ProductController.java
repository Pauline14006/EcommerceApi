package com.ws101.gallamora.torres.EcommerceApi.controller;

import com.ws101.gallamora.torres.EcommerceApi.model.Product;
import com.ws101.gallamora.torres.EcommerceApi.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for product-related endpoints.
 *
 * @author P.M A. Gallamora
 * @author P.G C. Torres
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Returns all products persisted in the database.
     * Public endpoint - no authentication required.
     *
     * @return 200 OK with a list of all products
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * Returns a single product by its database ID.
     * Public endpoint - no authentication required.
     *
     * @param id the product ID from the URL path
     * @return 200 OK and the product, or 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Filters products by a given type and value.
     * Public endpoint - no authentication required.
     *
     * @param filterType  what to filter by
     * @param filterValue the value to filter with
     * @return 200 OK with matching products, or 400 Bad Request
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
     * Creates a new product. Admin only.
     *
     * @param product      the product data from the request body
     * @param categoryName the category the product should belong to
     * @return 201 Created and the saved product, or 400 Bad Request
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestBody Product product,
            @RequestParam String categoryName) {

        if (product.getName() == null || product.getName().length() < 2) {
            return ResponseEntity.badRequest().build();
        }
        if (product.getPrice() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (categoryName == null || categoryName.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (product.getStockQuantity() < 0) {
            return ResponseEntity.badRequest().build();
        }

        Product created = productService.createProduct(product, categoryName);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Replaces all fields of an existing product. Admin only.
     *
     * @param id           the product ID from the URL path
     * @param product      the replacement product data
     * @param categoryName the updated category name
     * @return 200 OK and the updated product, or 404 Not Found
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product,
            @RequestParam String categoryName) {
        Product updated = productService.updateProduct(id, product, categoryName);
        return ResponseEntity.ok(updated);
    }

    /**
     * Partially updates a product. Admin only.
     *
     * @param id      the product ID from the URL path
     * @param product the partial product data
     * @return 200 OK and the patched product, or 404 Not Found
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Product> patchProduct(@PathVariable Long id, @RequestBody Product product) {
        Product patched = productService.patchProduct(id, product);
        return ResponseEntity.ok(patched);
    }

    /**
     * Deletes a product by its ID. Admin only.
     *
     * @param id the product ID from the URL path
     * @return 204 No Content if deleted, or 404 Not Found
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}