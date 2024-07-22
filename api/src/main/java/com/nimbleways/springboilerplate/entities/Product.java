package com.nimbleways.springboilerplate.entities;

import lombok.*;

import java.time.LocalDate;

import javax.persistence.*;

import com.nimbleways.springboilerplate.enums.ProductType;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "lead_time")
    private Integer leadTime;

    @Column(name = "available")
    private Integer available;

    @Column(name = "type")
    private ProductType type; // Change it to Enum

    @Column(name = "name")
    private String name;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "season_start_date")
    private LocalDate seasonStartDate;

    @Column(name = "season_end_date")
    private LocalDate seasonEndDate;
    
    // New Properties for the Flashable Type
    @Column(name = "flash_start_date")
    private LocalDate flashStartDate;

    @Column(name = "flash_end_date")
    private LocalDate flashEndDate;

    @Column(name = "max_quantity")
    private Integer maxQuantity;
}
