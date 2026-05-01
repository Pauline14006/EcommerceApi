package com.ws101.gallamora.torres.EcommerceApi.repository;

import com.ws101.gallamora.torres.EcommerceApi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Product} entities.
 * <p>
 * Extends {@link JpaRepository} which automatically provides standard CRUD operations
 * ({@code save()}, {@code findById()}, {@code findAll()}, {@code deleteById()}, etc.)
 * without any implementation code needed.
 * Spring generates the implementation class at runtime.
 * </p>
 *
 * @author P.M A. Gallamora
 * @author P.G C. Torres
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds all products whose category name matches the given value (case-insensitive).
     * Uses Spring Data's method-naming convention – no @Query needed.
     *
     * @param name the category name to filter by (e.g., "Electronics")
     * @return list of products belonging to that category
     */
    List<Product> findByCategory_NameIgnoreCase(String name);

    /**
     * Finds all products whose name contains the given keyword (case-insensitive).
     * Uses Spring Data's method-naming convention.
     *
     * @param keyword the search term to look for inside product names
     * @return list of products whose names contain the keyword
     */
    List<Product> findByNameContainingIgnoreCase(String keyword);

    /**
     * Finds all products within a given price range using JPQL.
     * <p>
     * Note: This query targets the <em>Entity class</em> ({@code Product}) and its
     * <em>Java field</em> ({@code p.price}), not the database table or column name.
     * </p>
     *
     * @param min the minimum price (inclusive)
     * @param max the maximum price (inclusive)
     * @return list of products whose price falls within [min, max]
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max")
    List<Product> findByPriceBetween(@Param("min") double min, @Param("max") double max);

    /**
     * Finds all products at or below the given maximum price using JPQL.
     *
     * @param max the maximum price (inclusive)
     * @return list of products whose price is less than or equal to max
     */
    @Query("SELECT p FROM Product p WHERE p.price <= :max")
    List<Product> findByPriceLessThanOrEqual(@Param("max") double max);
}
