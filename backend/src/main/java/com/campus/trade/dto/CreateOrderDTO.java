package com.campus.trade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateOrderDTO {

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotBlank(message = "收货地址不能为空")
    private String address;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
