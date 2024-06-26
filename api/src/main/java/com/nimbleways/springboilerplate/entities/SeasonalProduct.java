package com.nimbleways.springboilerplate.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@Data
@DiscriminatorValue("SEASONAL")
public class SeasonalProduct extends Product {
    @Column(name = "season_start_date")
    private LocalDate seasonStartDate;

    @Column(name = "season_end_date")
    private LocalDate seasonEndDate;

    @Override
    public void process() {
        // Logic for processing seasonal products
        LocalDate now = LocalDate.now();
        if (now.isAfter(this.getSeasonStartDate()) && now.isBefore(this.getSeasonEndDate()) && this.getAvailable() > 0) {
            this.setAvailable(this.getAvailable() - 1);
        } else if (now.plusDays(this.getLeadTime()).isAfter(this.getSeasonEndDate())) {
            this.setAvailable(0);
        }
    }
}
