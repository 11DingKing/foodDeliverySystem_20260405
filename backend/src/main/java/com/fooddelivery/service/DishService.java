package com.fooddelivery.service;

import com.fooddelivery.dto.CategoryRequest;
import com.fooddelivery.dto.DishRequest;
import com.fooddelivery.entity.Dish;
import com.fooddelivery.entity.DishCategory;
import java.util.List;

public interface DishService {
    // 分类管理
    List<DishCategory> getCategories(Long shopId);
    void createCategory(CategoryRequest request);
    void updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
    
    // 菜品管理
    List<Dish> getDishesByShop(Long shopId);
    List<Dish> getDishesByCategory(Long categoryId);
    Dish getById(Long id);
    void createDish(DishRequest request);
    void updateDish(Long id, DishRequest request);
    void deleteDish(Long id);
    void updateDishStatus(Long id, Integer status);
}
