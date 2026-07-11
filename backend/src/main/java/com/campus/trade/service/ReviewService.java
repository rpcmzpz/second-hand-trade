package com.campus.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.entity.Review;

public interface ReviewService extends IService<Review> {

    Review createReview(Long orderId, Long reviewerId, Long revieweeId, Integer rating, String content);
}
