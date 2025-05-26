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
    
    
    
    public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Integer getLeadTime() {
		return leadTime;
	}


	public void setLeadTime(Integer leadTime) {
		this.leadTime = leadTime;
	}


	public Integer getAvailable() {
		return available;
	}


	public void setAvailable(Integer available) {
		this.available = available;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public LocalDate getExpiryDate() {
		return expiryDate;
	}


	public void setExpiryDate(LocalDate expiryDate) {
		this.expiryDate = expiryDate;
	}


	public LocalDate getSeasonStartDate() {
		return seasonStartDate;
	}


	public void setSeasonStartDate(LocalDate seasonStartDate) {
		this.seasonStartDate = seasonStartDate;
	}


	public LocalDate getSeasonEndDate() {
		return seasonEndDate;
	}


	public void setSeasonEndDate(LocalDate seasonEndDate) {
		this.seasonEndDate = seasonEndDate;
	}


	public boolean isAvailable() {
        return available > 0 && !isExpired() && !isOutOfSeason();
    }
    
    
    public boolean isExpired() {
        return "EXPIRABLE".equals(type) && 
               expiryDate != null && 
               LocalDate.now().isAfter(expiryDate);
    }
    
    
    public boolean isOutOfSeason() {
        if (!"SEASONAL".equals(type)) {
            return false;
        }
        
        LocalDate now = LocalDate.now();
        
        // Si la date actuelle est après la fin de saison
        if (seasonEndDate != null && now.isAfter(seasonEndDate)) {
            return true;
        }
        
        // Si la date actuelle est avant le début de saison
        if (seasonStartDate != null && now.isBefore(seasonStartDate)) {
            return true;
        }
        
        return false;
    }
    
    
    public boolean isLeadTimeExceedingSeasonEnd() {
        if (!"SEASONAL".equals(type) || seasonEndDate == null) {
            return false;
        }
        
        LocalDate deliveryDate = LocalDate.now().plusDays(leadTime);
        return deliveryDate.isAfter(seasonEndDate);
    }
    
    
    public void decrementAvailable() {
        if (available > 0) {
            available--;
        }
    }
    
    
    
    
    
}
