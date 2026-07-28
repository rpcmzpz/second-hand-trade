package com.campus.trade.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateReviewDTO {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "被评价人ID不能为空")
    private Long revieweeId;

    @NotNull(message = "评分不能为空")
    @Min(1) @Max(5)
    private Integer rating;

    private String content;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getRevieweeId() { return revieweeId; }
    public void setRevieweeId(Long revieweeId) { this.revieweeId = revieweeId; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
