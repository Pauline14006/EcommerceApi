package com.ws101.gallamora.torres.EcommerceApi.repository;

import com.ws101.gallamora.torres.EcommerceApi.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link Category} entities.
 * <p>
 * Extends {@link JpaRepository} to get all standard CRUD operations for free.
 * Spring generates the concrete implementation class at startup.
 * </p>
 *
 * @author P.M A. Gallamora
 * @author P.G C. Torres
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds a category by its name (case-insensitive).
     * Used in the service layer to look up or create a category by name when a
     * product is saved.
     *
     * @param name the category name to search for
     * @return an {@link Optional} containing the Category if found, or empty
     */
    Optional<Category> findByNameIgnoreCase(String name);
}
