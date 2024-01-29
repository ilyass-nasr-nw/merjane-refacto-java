package com.nimbleways.springboilerplate.entities;

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
    @Column(name = "id")
    private Long id;

    @Column(name = "lead_time")
    private Integer leadTime;

    @Column(name = "available")
    private Integer available;

    @Column(name = "type")
    private String type;

    @Column(name = "name")
    private String name;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "season_start_date")
    private LocalDate seasonStartDate;

    @Column(name = "season_end_date")
    private LocalDate seasonEndDate;

    @Column(name = "flash_sale_start_date")
    private LocalDate flashSaleStartDate;

    @Column(name = "flash_sale_end_date")
    private LocalDate flashSaleEndDate;

    @Column(name = "max_items_in_flash_sale")
    private Integer maxItemsInFlashSale;

    /**
     * Normal Product
     * @param leadTime
     * @param available
     * @param name
     */
    public Product(Long id, Integer leadTime, Integer available,String type, String name) {
        this.id = id;
        this.leadTime = leadTime;
        this.available = available;
        this.name = name;
        this.type = type;
    }

    /**
     * Seasonal Product Type
     * @param leadTime
     * @param available
     * @param name
     * @param seasonStartDate
     * @param seasonEndDate
     */
    public Product(Long id, Integer leadTime, Integer available, String type, String name, LocalDate seasonStartDate, LocalDate seasonEndDate) {
        this.id = id;
        this.leadTime = leadTime;
        this.available = available;
        this.name = name;
        this.type = type;
        this.seasonStartDate = seasonStartDate;
        this.seasonEndDate = seasonEndDate;
    }

    /**
     * Expired Product
     * @param leadTime
     * @param available
     * @param name
     * @param expiryDate
     */
    public Product(Long id, Integer leadTime, Integer available, String type, String name, LocalDate expiryDate) {
        this.id = id;
        this.leadTime = leadTime;
        this.available = available;
        this.name = name;
        this.type = type;
        this.expiryDate = expiryDate;
    }

    /**
     * FlashSale product
     * @param leadTime
     * @param available
     * @param name
     * @param flashSaleStartDate
     * @param flashSaleEndDate
     * @param maxItemsInFlashSale
     */
    public Product(Long id, Integer leadTime, Integer available, String type, String name, LocalDate flashSaleStartDate, LocalDate flashSaleEndDate, Integer maxItemsInFlashSale) {
        this.id = id;
        this.leadTime = leadTime;
        this.available = available;
        this.name = name;
        this.type = type;
        this.flashSaleStartDate = flashSaleStartDate;
        this.flashSaleEndDate = flashSaleEndDate;
        this.maxItemsInFlashSale = maxItemsInFlashSale;
    }
}
