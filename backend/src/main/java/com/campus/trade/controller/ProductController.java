package com.campus.trade.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.common.Result;
import com.campus.trade.entity.Category;
import com.campus.trade.entity.Product;
import com.campus.trade.service.CategoryService;
import com.campus.trade.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result<Page<Product>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getTitle, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        return Result.success(productService.page(pageParam, wrapper));
    }

    @GetMapping("/categories")
    public Result<List<Category>> categories() {
        return Result.success(categoryService.list());
    }

    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product != null) {
            product.setViewCount(product.getViewCount() == null ? 1 : product.getViewCount() + 1);
            productService.updateById(product);
        }
        return Result.success(product);
    }

    @PostMapping
    public Result<Product> add(@RequestBody Product product, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        product.setSellerId(userId);
        product.setViewCount(0);
        product.setFavoriteCount(0);
        product.setStatus("ON_SALE");
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productService.save(product);
        return Result.success("发布成功", product);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Product product, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Product exist = productService.getById(id);
        if (exist == null || !exist.getSellerId().equals(userId)) {
            return Result.error(403, "无权操作");
        }
        product.setProductId(id);
        product.setSellerId(null);
        product.setUpdateTime(LocalDateTime.now());
        productService.updateById(product);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Product exist = productService.getById(id);
        if (exist == null || !exist.getSellerId().equals(userId)) {
            return Result.error(403, "无权操作");
        }
        productService.removeById(id);
        return Result.success("删除成功");
    }
}
