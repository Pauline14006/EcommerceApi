package com.ws101.gallamora.torres.EcommerceApi.repository;

import com.ws101.gallamora.torres.EcommerceApi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for User entity.
 *
 * @author P.M A. Gallamora
 * @author P.G C. Torres
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}