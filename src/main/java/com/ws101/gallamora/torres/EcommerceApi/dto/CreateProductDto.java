package com.ws101.gallamora.torres.EcommerceApi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * DTO for product creation requests.
 *
 * @author P.M A. Gallamora
 * @author P.G C. Torres
 */
public record CreateProductDto(

        @NotBlank(message = "Product name is required")
        @Size(min = 2, message = "Product name must be at least 2 characters")
        String name,

        String description,

        @Positive(message = "Price must be greater than zero")
        double price,

        @NotBlank(message = "Category name is required")
        String categoryName,

        @PositiveOrZero(message = "Stock quantity must be zero or positive")
        int stockQuantity,

        String imageUrl
) {}