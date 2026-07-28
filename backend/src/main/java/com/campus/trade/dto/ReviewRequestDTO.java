package com.campus.trade.dto;

import jakarta.validation.constraints.NotBlank;

public class ReviewRequestDTO {

    @NotBlank(message = "商品描述不能为空")
    private String description;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
