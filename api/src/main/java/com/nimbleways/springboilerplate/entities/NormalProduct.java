package com.nimbleways.springboilerplate.entities;

import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Data
@DiscriminatorValue("NORMAL")
public class NormalProduct extends Product {
    @Override
    public void process() {
        // Logic for processing normal products
        if (this.getAvailable() > 0) {
            this.setAvailable(this.getAvailable() - 1);
        }
    }
}
