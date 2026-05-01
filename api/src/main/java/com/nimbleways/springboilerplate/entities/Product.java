package com.nimbleways.springboilerplate.entities;

import com.nimbleways.springboilerplate.utils.enums.OrderType;
import lombok.*;

import java.time.LocalDate;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "lead_time", nullable = false)
    private Integer leadTime;

    @Column(name = "available", nullable = false)
    private Integer available;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private OrderType type;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "season_start_date")
    private LocalDate seasonStartDate;

    @Column(name = "season_end_date")
    private LocalDate seasonEndDate;
}
