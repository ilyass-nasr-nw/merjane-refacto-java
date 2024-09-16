package com.nimbleways.springboilerplate.helpers;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ProductHelper {



    public static Map<String,Object> defaultProductValues(final Map<String, Object> overrideValues ) {

        Map<String, Object> productValues = new HashMap<>();
        productValues.put("leadTime", 5);
        productValues.put("available", 100);
        productValues.put("type", ProductType.NORMAL);
        productValues.put("name", "MACBOOK M1");
        productValues.put("expiryDate", LocalDate.of(2024, 12, 31));
        productValues.put("seasonStartDate", LocalDate.of(2024, 6, 1));
        productValues.put("seasonEndDate", LocalDate.of(2024, 8, 31));
        productValues.put("flashSaleStartDate", LocalDate.of(2024, 9, 1));
        productValues.put("flashSaleEndDate", LocalDate.of(2024, 9, 30));


        if(!overrideValues.isEmpty()) {
            productValues.putAll(overrideValues);
        }
        return productValues;
    }



    /**
     * Creates a new Product instance and sets its fields based on the provided values.
     *
     * @param values a map where keys are field names and values are the values to set in the Product instance.
     * @return a Product object with fields set according to the values provided.
     */
    public static Product createProduct(Map<String, Object> values) {
        Product product = new Product();

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();

            try {
                Field field = Product.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(product, fieldValue);

            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Error setting field value", e);
            }
        }

        return product;
    }

}


