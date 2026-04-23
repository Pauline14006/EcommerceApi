package com.ws101.gallamora.torres.EcommerceApi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Product model - holds all the product info
 * like name, price, category, etc.
 *
 * @author P.M A. Gallamora
 * @author P.G C. Torress
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    /** Unique identifier for the product */
    private Long id;

    /** Name of the product */
    private String name;

    /** Short description of the product */
    private String description;

    /** Price of the product */
    private double price;

    /** Category the product belongs to (e.g., Electronics, Clothing) */
    private String category;

    /** How many units are available in stock */
    private int stockQuantity;

    /** URL of the product image (optional) */
    private String imageUrl;
}
