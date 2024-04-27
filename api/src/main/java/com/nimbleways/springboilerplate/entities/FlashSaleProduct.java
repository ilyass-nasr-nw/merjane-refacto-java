package com.nimbleways.springboilerplate.entities;

import com.nimbleways.springboilerplate.exceptions.FlashSaleOverException;

import java.time.LocalDate;

public class FlashSaleProduct extends Product{
    private LocalDate endDate;

    public FlashSaleProduct(int available, LocalDate endDate) {
        this.setAvailable(available);
        this.endDate = endDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void processOrder() {
        if (getAvailable() > 0 && LocalDate.now().isBefore(endDate)) {
            setAvailable(getAvailable() - 1);
        } else {
            throw new FlashSaleOverException("Flash sale is over");
        }
    }
}
