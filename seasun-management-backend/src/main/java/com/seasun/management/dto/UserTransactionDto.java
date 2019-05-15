package com.seasun.management.dto;

public class UserTransactionDto {
    private Long totalPeople;

    private Long servingPeople;

    private Long leavingPeople;
    
    private Long totalIntern;

    public Long getTotalPeople() {
        return totalPeople;
    }

    public void setTotalPeople(Long totalPeople) {
        this.totalPeople = totalPeople;
    }

    public Long getServingPeople() {
        return servingPeople;
    }

    public void setServingPeople(Long servingPeople) {
        this.servingPeople = servingPeople;
    }

    public Long getLeavingPeople() {
        return leavingPeople;
    }

    public void setLeavingPeople(Long leavingPeople) {
        this.leavingPeople = leavingPeople;
    }
    
    public Long getTotalIntern() {
		return totalIntern;
	}
    
    public void setTotalIntern(Long totalIntern) {
		this.totalIntern = totalIntern;
	}
}
