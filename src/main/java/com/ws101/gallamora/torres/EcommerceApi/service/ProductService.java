package com.ws101.gallamora.torres.EcommerceApi.service;

import com.ws101.gallamora.torres.EcommerceApi.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles all the business logic for products
 * like getting, adding, updating, and deleting
 *
 * @author P.M A. Gallamora
 * @author P.G C. Torress
 */
@Service
public class ProductService {

    // In-memory storage - this is our "database" for now
    private List<Product> productList = new ArrayList<>();

    // Counter to generate unique IDs for new products
    private long idCounter = 1;

    /**
     * Constructor - pre-loads 10 sample products when the app starts.
     */
    public ProductService() {
        productList.add(new Product(idCounter++, "Sony Headphones", "Wireless noise-cancelling headphones", 59.99,
                "Electronics", 30, "pics/product1.png"));
        productList.add(new Product(idCounter++, "Controller", "Wireless gaming controller", 20.00, "Electronics", 50,
                "pics/product2.webp"));
        productList.add(new Product(idCounter++, "Nintendo Switch", "Portable gaming console", 120.00, "Electronics",
                15, "pics/product3.webp"));
        productList.add(new Product(idCounter++, "Mechanical Keyboard", "RGB mechanical gaming keyboard", 45.00,
                "Electronics", 25, "pics/product4.webp"));
        productList.add(new Product(idCounter++, "Gaming Mouse", "High DPI gaming mouse", 35.00, "Electronics", 40,
                "pics/product1.png"));
        productList.add(new Product(idCounter++, "Monitor", "27-inch Full HD monitor", 200.00, "Electronics", 10,
                "pics/product2.webp"));
        productList.add(new Product(idCounter++, "USB Hub", "7-port USB 3.0 hub", 15.00, "Accessories", 60,
                "pics/product3.webp"));
        productList.add(
                new Product(idCounter++, "Webcam", "1080p HD webcam", 55.00, "Electronics", 20, "pics/product4.webp"));
        productList.add(new Product(idCounter++, "Mousepad", "Large gaming mousepad", 12.00, "Accessories", 80,
                "pics/product1.png"));
        productList.add(new Product(idCounter++, "Laptop Stand", "Adjustable aluminum laptop stand", 25.00,
                "Accessories", 35, "pics/product2.webp"));
    }

    /** Returns all products in the list */
    public List<Product> getAllProducts() {
        return productList;
    }

    /** Finds a product by its ID */
    public Optional<Product> getProductById(Long id) {
        return productList.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst();
    }

    /** Creates a new product and adds it to the list */
    public Product createProduct(Product product) {
        product.setId(idCounter++);
        productList.add(product);
        return product;
    }

    /** Replaces an entire product with new data (PUT) */
    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        Optional<Product> existing = getProductById(id);
        if (existing.isPresent()) {
            Product product = existing.get();
            product.setName(updatedProduct.getName());
            product.setDescription(updatedProduct.getDescription());
            product.setPrice(updatedProduct.getPrice());
            product.setCategory(updatedProduct.getCategory());
            product.setStockQuantity(updatedProduct.getStockQuantity());
            product.setImageUrl(updatedProduct.getImageUrl());
            return Optional.of(product);
        }
        return Optional.empty();
    }

    /**
     * Partially updates a product - only updates fields that are not null (PATCH)
     */
    public Optional<Product> patchProduct(Long id, Product patch) {
        Optional<Product> existing = getProductById(id);
        if (existing.isPresent()) {
            Product product = existing.get();
            if (patch.getName() != null)
                product.setName(patch.getName());
            if (patch.getDescription() != null)
                product.setDescription(patch.getDescription());
            if (patch.getPrice() > 0)
                product.setPrice(patch.getPrice());
            if (patch.getCategory() != null)
                product.setCategory(patch.getCategory());
            if (patch.getStockQuantity() >= 0)
                product.setStockQuantity(patch.getStockQuantity());
            if (patch.getImageUrl() != null)
                product.setImageUrl(patch.getImageUrl());
            return Optional.of(product);
        }
        return Optional.empty();
    }

    /** Deletes a product by ID */
    public boolean deleteProduct(Long id) {
        return productList.removeIf(product -> product.getId().equals(id));
    }

    /** Filters products by filterType and filterValue */
    public List<Product> filterProducts(String filterType, String filterValue) {
        return switch (filterType.toLowerCase()) {
            case "category" -> productList.stream()
                    .filter(p -> p.getCategory().equalsIgnoreCase(filterValue))
                    .toList();
            case "name" -> productList.stream()
                    .filter(p -> p.getName().toLowerCase().contains(filterValue.toLowerCase()))
                    .toList();
            case "price" -> productList.stream()
                    .filter(p -> p.getPrice() <= Double.parseDouble(filterValue))
                    .toList();
            default -> new ArrayList<>();
        };
    }
}