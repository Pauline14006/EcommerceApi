package com.ws101.gallamora.torres.EcommerceApi.repository;

import com.ws101.gallamora.torres.EcommerceApi.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Order} entities.
 * <p>
 * Extends {@link JpaRepository} which provides standard CRUD operations.
 * Spring generates the concrete implementation at runtime.
 * </p>
 *
 * @author P.M A. Gallamora
 * @author P.G C. Torres
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds all orders placed by a specific customer name (case-insensitive).
     * Uses Spring Data's method-naming convention.
     *
     * @param customerName the name of the customer whose orders to retrieve
     * @return list of orders belonging to that customer
     */
    List<Order> findByCustomerNameIgnoreCase(String customerName);
}
