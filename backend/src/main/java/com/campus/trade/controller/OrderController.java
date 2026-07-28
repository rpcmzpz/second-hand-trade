package com.campus.trade.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
            @RequestParam(required = false) String status) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (buyerId != null) {
            wrapper.eq(Order::getBuyerId, buyerId);
        }
        if (sellerId != null) {
            wrapper.eq(Order::getSellerId, sellerId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return Result.success(orderService.page(pageParam, wrapper));
    }

    @GetMapping("/{id}")
    public Result<Order> getById(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
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
