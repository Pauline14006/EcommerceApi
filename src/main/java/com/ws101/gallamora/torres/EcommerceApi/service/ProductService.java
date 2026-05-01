package com.ws101.gallamora.torres.EcommerceApi.service;

import com.ws101.gallamora.torres.EcommerceApi.model.Category;
import com.ws101.gallamora.torres.EcommerceApi.model.Product;
import com.ws101.gallamora.torres.EcommerceApi.repository.CategoryRepository;
import com.ws101.gallamora.torres.EcommerceApi.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Handles all business logic for products.
 * <p>
 * Task 3: The ArrayList-based in-memory storage from Lab 7 has been replaced with
 * Spring Data JPA repositories. All data is now persisted to MySQL via
 * {@link ProductRepository} and {@link CategoryRepository}.
 * </p>
 *
 * @author P.M A. Gallamora
 * @author P.G C. Torres
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Resolves a category by name. If the category already exists in the database
     * it is reused; otherwise a new Category record is created and saved.
     *
     * @param categoryName the name of the category to resolve
     * @return the existing or newly created {@link Category}
     */
    private Category resolveCategory(String categoryName) {
        return categoryRepository.findByNameIgnoreCase(categoryName)
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setName(categoryName);
                    return categoryRepository.save(newCategory);
                });
    }

    /**
     * Populates the transient {@code categoryName} field on a Product so that
     * JSON responses include the category name without triggering a lazy-load.
     *
     * @param product the product whose categoryName field should be set
     * @return the same product with categoryName populated
     */
    private Product populateCategoryName(Product product) {
        if (product.getCategory() != null) {
            product.setCategoryName(product.getCategory().getName());
        }
        return product;
    }

    // ─── Read Operations ───────────────────────────────────────────────────────

    /**
     * Returns all products persisted in the database.
     *
     * @return list of all products with their categoryName field set
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::populateCategoryName)
                .toList();
    }

    /**
     * Finds a single product by its ID.
     *
     * @param id the product ID to look up
     * @return an Optional containing the product (with categoryName) if found
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::populateCategoryName);
    }

    /**
     * Filters products based on a filter type and value.
     * Supported filter types: {@code category}, {@code name}, {@code price}.
     *
     * @param filterType  the field to filter by
     * @param filterValue the value to match
     * @return list of matching products with categoryName populated
     * @throws IllegalArgumentException if filterType is unrecognised or
     *                                  filterValue is not a valid number for price
     */
    @Transactional(readOnly = true)
    public List<Product> filterProducts(String filterType, String filterValue) {
        List<Product> results = switch (filterType.toLowerCase()) {
            case "category" -> productRepository.findByCategory_NameIgnoreCase(filterValue);
            case "name"     -> productRepository.findByNameContainingIgnoreCase(filterValue);
            case "price" -> {
                try {
                    yield productRepository.findByPriceLessThanOrEqual(Double.parseDouble(filterValue));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Price filter value must be a valid number: " + filterValue);
                }
            }
            default -> throw new IllegalArgumentException("Unknown filter type: " + filterType);
        };
        return results.stream().map(this::populateCategoryName).toList();
    }

    // ─── Write Operations ─────────────────────────────────────────────────────

    /**
     * Creates a new product and persists it to the database.
     * <p>
     * The {@code categoryName} string from the request is resolved to an actual
     * {@link Category} entity (creating one if it does not yet exist).
     * </p>
     *
     * @param product        the product data to persist
     * @param categoryName   the name of the category the product belongs to
     * @return the saved product with its generated ID and categoryName populated
     */
    @Transactional
    public Product createProduct(Product product, String categoryName) {
        Category category = resolveCategory(categoryName);
        product.setCategory(category);
        Product saved = productRepository.save(product);
        return populateCategoryName(saved);
    }

    /**
     * Replaces all fields of an existing product with the supplied data.
     *
     * @param id             the ID of the product to update
     * @param updatedProduct the new product data
     * @param categoryName   the (possibly new) category name
     * @return the updated product with categoryName populated
     * @throws EntityNotFoundException if no product with the given ID exists
     */
    @Transactional
    public Product updateProduct(Long id, Product updatedProduct, String categoryName) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setStockQuantity(updatedProduct.getStockQuantity());
        existing.setImageUrl(updatedProduct.getImageUrl());
        existing.setCategory(resolveCategory(categoryName));

        Product saved = productRepository.save(existing);
        return populateCategoryName(saved);
    }

    /**
     * Partially updates a product – only fields that are non-null / positive are changed.
     *
     * @param id    the ID of the product to patch
     * @param patch the partial product data
     * @return the patched product with categoryName populated
     * @throws EntityNotFoundException if no product with the given ID exists
     */
    @Transactional
    public Product patchProduct(Long id, Product patch) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        if (patch.getName() != null)        existing.setName(patch.getName());
        if (patch.getDescription() != null) existing.setDescription(patch.getDescription());
        if (patch.getPrice() > 0)           existing.setPrice(patch.getPrice());
        if (patch.getStockQuantity() >= 0)  existing.setStockQuantity(patch.getStockQuantity());
        if (patch.getImageUrl() != null)    existing.setImageUrl(patch.getImageUrl());

        // Update category only when a categoryName was explicitly provided
        if (patch.getCategoryName() != null && !patch.getCategoryName().isBlank()) {
            existing.setCategory(resolveCategory(patch.getCategoryName()));
        }

        Product saved = productRepository.save(existing);
        return populateCategoryName(saved);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id the ID of the product to delete
     * @throws EntityNotFoundException       if no product with the given ID exists
     * @throws DataIntegrityViolationException if the product is referenced by an order
     */
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}
