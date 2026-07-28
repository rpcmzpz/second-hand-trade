package com.campus.trade.dto;

public class PriceResponseDTO {

    private Double suggestedPrice;
    private String priceRange;
    private String reasoning;

    public Double getSuggestedPrice() { return suggestedPrice; }
    public void setSuggestedPrice(Double suggestedPrice) { this.suggestedPrice = suggestedPrice; }
    public String getPriceRange() { return priceRange; }
    public void setPriceRange(String priceRange) { this.priceRange = priceRange; }
    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }
}
