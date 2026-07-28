package com.campus.trade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.entity.Product;

public interface ProductService extends IService<Product> {

    Page<Product> searchProducts(String keyword, Long categoryId, Integer page, Integer size);

    Product getProductDetail(Long id);

    Product publishProduct(Product product, Long userId);

    void updateProduct(Long id, Product product, Long userId);

    void deleteProduct(Long id, Long userId);
}
