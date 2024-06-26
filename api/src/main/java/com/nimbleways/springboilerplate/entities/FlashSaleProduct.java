package com.nimbleways.springboilerplate.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@Data
@DiscriminatorValue("FLASHSALE")
public class FlashSaleProduct extends Product {
    @Column(name = "flash_sale_start_date")
    private LocalDate flashSaleStartDate;

    @Column(name = "flash_sale_end_date")
    private LocalDate flashSaleEndDate;

    @Column(name = "max_quantity")
    private Integer maxQuantity;

    @Override
    public void process() {
        // Logic for processing flashSale products
        LocalDate now = LocalDate.now();
        if (now.isAfter(this.getFlashSaleStartDate()) && now.isBefore(this.getFlashSaleEndDate()) && this.getAvailable() > 0 && this.getMaxQuantity() > 0) {
            this.setAvailable(this.getAvailable() - 1);
            this.setMaxQuantity(this.getMaxQuantity() - 1);
        } else if (now.isAfter(this.getFlashSaleEndDate()) || this.getMaxQuantity() == 0 ) {
//            ns.sendExpirationNotification(p.getName(), p.getFlashSaleEndDate());
            this.setAvailable(0);
        }
//        else if (this.maxQuantity == 0) {
//            ns.sendOutOfStockNotification(p.getName());
//            this.setAvailable(0);
//        }
    }
}