package com.nimbleways.springboilerplate.entities.product;

import com.nimbleways.springboilerplate.enums.ProductType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Table(name = "products")
@SuperBuilder
public abstract class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "lead_time")
    private Integer leadTime;

    @Column(name = "available")
    private Integer available;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ProductType type;
}
