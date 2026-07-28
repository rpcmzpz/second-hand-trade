package com.campus.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.entity.Order;

public interface OrderService extends IService<Order> {

    Order createOrder(Long buyerId, Long productId, String address);

    void updateOrderStatus(Long orderId, String newStatus, Long userId);
}
