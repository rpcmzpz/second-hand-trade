package com.campus.trade.dto;

import com.campus.trade.entity.Product;
import java.util.List;

public class SemanticSearchDTO {

    private List<Product> products;
    private String expandedKeywords;

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
    public String getExpandedKeywords() { return expandedKeywords; }
    public void setExpandedKeywords(String expandedKeywords) { this.expandedKeywords = expandedKeywords; }
}
