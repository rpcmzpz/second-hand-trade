package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.common.BusinessException;
import com.campus.trade.entity.Order;
import com.campus.trade.entity.Review;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.mapper.ReviewMapper;
import com.campus.trade.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Review createReview(Long orderId, Long reviewerId, Long revieweeId, Integer rating, String content) {
        // 评价必须挂在真实订单上，且只能由该订单的买家或卖家发起
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(400, "订单不存在");
        }
        boolean isBuyer = order.getBuyerId().equals(reviewerId);
        boolean isSeller = order.getSellerId().equals(reviewerId);
        if (!isBuyer && !isSeller) {
            throw new BusinessException(403, "无权评价该订单");
        }
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new BusinessException(400, "订单完成后才能评价");
        }
        // 被评价人只能是交易的另一方，不采信前端传入的值
        Long expectedRevieweeId = isBuyer ? order.getSellerId() : order.getBuyerId();
        if (!expectedRevieweeId.equals(revieweeId)) {
            throw new BusinessException(400, "被评价人不正确");
        }

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
