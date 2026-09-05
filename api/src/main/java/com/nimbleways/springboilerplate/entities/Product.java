package com.nimbleways.springboilerplate.entities;

import lombok.*;

import java.time.LocalDate;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
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

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ProductType type;

    @Column(name = "name")
    private String name;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "season_start_date")
    private LocalDate seasonStartDate;

    @Column(name = "season_end_date")
    private LocalDate seasonEndDate;

    public boolean isInStock() {
        return available > 0;
    }

    public void decrementStock() {
        available = available - 1;
    }

    public void markOutOfStock() {
        available = 0;
    }

    public boolean hasLeadTime() {
        return leadTime > 0;
    }

    /** The expiry date itself counts as expired. */
    public boolean hasExpiredOn(LocalDate date) {
        return !expiryDate.isAfter(date);
    }

    /** The first and last days of the season count as out of season. */
    public boolean isInSeasonOn(LocalDate date) {
        return date.isAfter(seasonStartDate) && date.isBefore(seasonEndDate);
    }

    public boolean seasonStartsAfter(LocalDate date) {
        return seasonStartDate.isAfter(date);
    }

    public LocalDate restockDateFrom(LocalDate date) {
        return date.plusDays(leadTime);
    }
}
