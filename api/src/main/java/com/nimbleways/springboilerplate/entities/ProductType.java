package com.nimbleways.springboilerplate.entities;

/**
 * Represents the inventory rules applied to a product.
 *
 * <p>Supported types:</p>
 * <ul>
 *   <li>{@link #NORMAL}: standard products (no special rules)</li>
 *   <li>{@link #SEASONAL}: available only during a specific season window</li>
 *   <li>{@link #EXPIRABLE}: available until an expiration date</li>
 * </ul>
 *
 * <p>
 * In the database, the type is stored as a String in the {@code products.type} column.
 * This enum provides a safe conversion method to keep business code type-safe
 * without changing the DB schema.
 * </p>
 */
public enum ProductType {

    /**
     * Standard product without any special rules.
     */
    NORMAL,

    /**
     * Product available only during a specific period (season).
     */
    SEASONAL,

    /**
     * Product that expires after a given expiration date.
     */
    EXPIRABLE;

    /**
     * Converts a database value into {@link ProductType}.
     *
     * <p>
     * This method is case-insensitive and trims spaces.
     * Example: {@code " normal "} will be converted to {@link #NORMAL}.
     * </p>
     *
     * @param value the raw type value stored in database
     * @return a valid {@link ProductType}
     * @throws IllegalArgumentException if the value is null/blank or does not map to any enum constant
     */
    public static ProductType fromDbValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Product type is null/blank");
        }

        try {
            return ProductType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown product type: '" + value + "'", ex);
        }
    }
}
