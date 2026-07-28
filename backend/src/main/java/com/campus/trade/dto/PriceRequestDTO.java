package com.campus.trade.dto;

import jakarta.validation.constraints.NotBlank;

public class PriceRequestDTO {

    @NotBlank(message = "商品标题不能为空")
    private String title;

    @NotBlank(message = "商品成色不能为空")
    private String condition;

    @NotBlank(message = "商品分类不能为空")
    private String category;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
