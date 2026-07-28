package com.campus.trade.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.dto.CreateReviewDTO;
import com.campus.trade.entity.Review;
import com.campus.trade.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public Result<Review> create(@Valid @RequestBody CreateReviewDTO dto, HttpServletRequest request) {
        Long reviewerId = (Long) request.getAttribute("userId");
        Review review = reviewService.createReview(dto.getOrderId(), reviewerId,
                dto.getRevieweeId(), dto.getRating(), dto.getContent());
        return Result.success("评价成功", review);
    }

    @GetMapping("/list")
    public Result<Page<Review>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long revieweeId) {
        Page<Review> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        if (revieweeId != null) {
            wrapper.eq(Review::getRevieweeId, revieweeId);
        }
        wrapper.orderByDesc(Review::getCreateTime);
        return Result.success(reviewService.page(pageParam, wrapper));
    }

    @GetMapping("/order/{orderId}")
    public Result<Object> getByOrderId(@PathVariable Long orderId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getOrderId, orderId);
        return Result.success(reviewService.list(wrapper));
    }
}
