package com.nimbleways.springboilerplate.entities.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("NORMAL")
@SuperBuilder
public class NormalProduct extends Product {
}
