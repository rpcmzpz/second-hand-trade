package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.common.BusinessException;
import com.campus.trade.entity.Review;
import com.campus.trade.mapper.ReviewMapper;
import com.campus.trade.service.ReviewService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    @Override
    public Review createReview(Long orderId, Long reviewerId, Long revieweeId, Integer rating, String content) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getOrderId, orderId)
                .eq(Review::getReviewerId, reviewerId);
        if (this.count(wrapper) > 0) {
            throw new BusinessException(400, "您已经评价过该订单");
        }

        Review review = new Review();
        review.setOrderId(orderId);
        review.setReviewerId(reviewerId);
        review.setRevieweeId(revieweeId);
        review.setRating(rating);
        review.setContent(content);
        review.setCreateTime(LocalDateTime.now());
        this.save(review);
        return review;
    }
}
