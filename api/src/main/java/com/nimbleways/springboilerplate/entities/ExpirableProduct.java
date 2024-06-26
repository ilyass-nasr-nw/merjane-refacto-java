package com.nimbleways.springboilerplate.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@Data
@DiscriminatorValue("EXPIRABLE")
public class ExpirableProduct extends Product {
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Override
    public void process() {
        // Logic for processing expirable products
        if (this.getAvailable() > 0 && this.getExpiryDate().isAfter(LocalDate.now())) {
            this.setAvailable(this.getAvailable() - 1);
        } else if (this.getExpiryDate().isAfter(LocalDate.now())){
            this.setAvailable(0);
        }
    }
}
