package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.common.BusinessException;
import com.campus.trade.entity.Order;
import com.campus.trade.entity.Product;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional
    public Order createOrder(Long buyerId, Long productId, String address) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(400, "商品不存在");
        }

        if (product.getSellerId().equals(buyerId)) {
            throw new BusinessException(400, "不能购买自己的商品");
        }

        // 原子“抢单”：把 “在售” 作为更新条件交给数据库判断，
        // 影响行数为 0 说明已被别人抢先买走，避免两个人同时下单买到同一件商品
        LambdaUpdateWrapper<Product> occupy = new LambdaUpdateWrapper<>();
        occupy.eq(Product::getProductId, productId)
                .eq(Product::getStatus, "ON_SALE")
                .set(Product::getStatus, "SOLD")
                .set(Product::getUpdateTime, LocalDateTime.now());
        if (productMapper.update(null, occupy) == 0) {
            throw new BusinessException(400, "手慢了，该商品已被他人买走或已下架");
        }

        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setSellerId(product.getSellerId());
        order.setProductId(productId);
        order.setAmount(product.getPrice());
        order.setAddress(address);
        order.setStatus("PENDING");
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        this.save(order);

        return order;
    }

    @Override
    public void updateOrderStatus(Long orderId, String newStatus, Long userId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException(400, "订单不存在");
        }

        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException(403, "无权操作该订单");
        }

        order.setStatus(newStatus);
        order.setUpdateTime(LocalDateTime.now());
        this.updateById(order);
    }
}
