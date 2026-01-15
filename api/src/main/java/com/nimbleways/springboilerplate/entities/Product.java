package com.nimbleways.springboilerplate.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Product entity representing an item that can be ordered and stored in inventory.
 *
 * <p>Business meaning:
 * <ul>
 *   <li>{@code available} = number of units currently in stock</li>
 *   <li>{@code leadTime} = number of days required for restocking</li>
 *   <li>{@code type} = inventory rules category (NORMAL, SEASONAL, EXPIRABLE)</li>
 * </ul>
 * </p>
 */
@Entity
@Table(name = "products")
@Access(AccessType.FIELD)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {

    /**
     * Unique identifier of the product (primary key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Restocking delay in days.
     * <p>
     * Used to notify the customer when the product is out of stock.
     * </p>
     */
    @Column(name = "lead_time")
    private Integer leadTime;

    /**
     * Current available stock units.
     * <p>
     * This value is decremented when an order is processed successfully for this product.
     * </p>
     */
    @Column(name = "available")
    private Integer available;

    /**
     * Product category stored as a String.
     * <p>
     * Expected values: NORMAL, SEASONAL, EXPIRABLE.
     * This is stored as text in DB to avoid schema changes.
     * Use {@link #getProductType()} for type-safe access.
     * </p>
     */
    @Column(name = "type", length = 20)
    private String type;

    /**
     * Human-readable product name.
     */
    @Column(name = "name", length = 255)
    private String name;

    /**
     * Expiration date of the product.
     * <p>
     * Only meaningful for EXPIRABLE products.
     * A product is considered unavailable once the expiration date is reached/passed.
     * </p>
     */
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    /**
     * Season start date.
     * <p>
     * Only meaningful for SEASONAL products.
     * If the product is requested outside its season, it can be considered unavailable.
     * </p>
     */
    @Column(name = "season_start_date")
    private LocalDate seasonStartDate;

    /**
     * Season end date.
     * <p>
     * Only meaningful for SEASONAL products.
     * If a restock lead time would exceed this date, the product is considered unavailable.
     * </p>
     */
    @Column(name = "season_end_date")
    private LocalDate seasonEndDate;

    /**
     * Type-safe accessor for the product type.
     * <p>
     * This method does not impact database mapping since {@code type} is stored as String.
     * </p>
     *
     * @return the product type as {@link ProductType}
     * @throws IllegalArgumentException if {@code type} is null/blank or invalid
     */
    @Transient
    public ProductType getProductType() {
        return ProductType.fromDbValue(this.type);
    }

    /**
     * Type-safe setter for the product type.
     * <p>
     * Updates the underlying String field persisted in the database.
     * </p>
     *
     * @param productType the product type enum
     */
    public void setProductType(ProductType productType) {
        this.type = productType != null ? productType.name() : null;
    }
}
