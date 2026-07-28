package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.common.BusinessException;
import com.campus.trade.entity.Product;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public Page<Product> searchProducts(String keyword, Long categoryId, Integer page, Integer size) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getTitle, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public Product getProductDetail(Long id) {
        Product product = this.getById(id);
        if (product != null) {
            product.setViewCount(product.getViewCount() == null ? 1 : product.getViewCount() + 1);
            this.updateById(product);
        }
        return product;
    }

    @Override
    public Product publishProduct(Product product, Long userId) {
        product.setSellerId(userId);
        product.setViewCount(0);
        product.setFavoriteCount(0);
        product.setStatus("ON_SALE");
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        this.save(product);
        return product;
    }

    @Override
    public void updateProduct(Long id, Product product, Long userId) {
        Product exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException(404, "商品不存在");
        }
        if (!exist.getSellerId().equals(userId)) {
            throw new BusinessException(403, "无权修改该商品");
        }
        product.setProductId(id);
        product.setSellerId(null);  // 禁止修改卖家
        product.setUpdateTime(LocalDateTime.now());
        this.updateById(product);
    }

    @Override
    public void deleteProduct(Long id, Long userId) {
        Product exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException(404, "商品不存在");
        }
        if (!exist.getSellerId().equals(userId)) {
            throw new BusinessException(403, "无权删除该商品");
        }
        this.removeById(id);
    }
}
