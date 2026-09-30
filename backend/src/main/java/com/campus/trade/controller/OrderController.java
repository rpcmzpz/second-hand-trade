package com.campus.trade.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.BusinessException;
import com.campus.trade.common.Result;
import com.campus.trade.dto.CreateOrderDTO;
import com.campus.trade.entity.Order;
import com.campus.trade.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result<Order> create(@Valid @RequestBody CreateOrderDTO dto, HttpServletRequest request) {
        Long buyerId = (Long) request.getAttribute("userId");
        Order order = orderService.createOrder(buyerId, dto.getProductId(), dto.getAddress());
        return Result.success("下单成功", order);
    }

    @GetMapping("/list")
    public Result<Page<Order>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long buyerId,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        // 只允许查询与自己相关的订单，参数由前端传入的一律以当前登录用户为准
        boolean onlyBought = buyerId != null && buyerId.equals(userId);
        boolean onlySold = sellerId != null && sellerId.equals(userId);
        if (onlyBought) {
            wrapper.eq(Order::getBuyerId, userId);
        } else if (onlySold) {
            wrapper.eq(Order::getSellerId, userId);
        } else {
            wrapper.and(w -> w.eq(Order::getBuyerId, userId).or().eq(Order::getSellerId, userId));
        }

        if (StringUtils.hasText(status)) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return Result.success(orderService.page(pageParam, wrapper));
    }

    @GetMapping("/{id}")
    public Result<Order> getById(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        Order order = orderService.getById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        boolean isAdmin = "admin".equals(role);
        boolean isParticipant = order.getBuyerId().equals(userId) || order.getSellerId().equals(userId);
        if (!isAdmin && !isParticipant) {
            throw new BusinessException(403, "无权查看该订单");
        }
        return Result.success(order);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                     @RequestBody Map<String, String> params,
                                     HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String newStatus = params.get("status");
        orderService.updateOrderStatus(id, newStatus, userId);
        return Result.success("状态更新成功");
    }
}
