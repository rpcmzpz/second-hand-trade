package com.campus.trade.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.trade.common.Result;
import com.campus.trade.entity.Category;
import com.campus.trade.entity.Product;
import com.campus.trade.service.CategoryService;
import com.campus.trade.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public Result<List<Category>> list() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSortOrder);
        return Result.success(categoryService.list(wrapper));
    }

    @PostMapping
    public Result<Category> add(@RequestBody Category category) {
        if (!StringUtils.hasText(category.getCategoryName())) {
            return Result.error(400, "分类名称不能为空");
        }
        Category exist = categoryService.getOne(
                new LambdaQueryWrapper<Category>().eq(Category::getCategoryName, category.getCategoryName()));
        if (exist != null) {
            return Result.error(400, "分类名称已存在");
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(99);
        }
        categoryService.save(category);
        return Result.success("添加成功", category);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Category category) {
        Category exist = categoryService.getById(id);
        if (exist == null) {
            return Result.error(404, "分类不存在");
        }
        if (StringUtils.hasText(category.getCategoryName())) {
            Category duplicate = categoryService.getOne(
                    new LambdaQueryWrapper<Category>()
                            .eq(Category::getCategoryName, category.getCategoryName())
                            .ne(Category::getCategoryId, id));
            if (duplicate != null) {
                return Result.error(400, "分类名称已存在");
            }
        }
        category.setCategoryId(id);
        categoryService.updateById(category);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (productService.count(
                new LambdaQueryWrapper<Product>().eq(Product::getCategoryId, id)) > 0) {
            return Result.error(400, "该分类下有商品，无法删除");
        }
        categoryService.removeById(id);
        return Result.success("删除成功");
    }
}
