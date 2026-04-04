package com.fooddelivery.controller;

import com.fooddelivery.annotation.OperationLogger;
import com.fooddelivery.common.Result;
import com.fooddelivery.dto.CategoryRequest;
import com.fooddelivery.dto.DishRequest;
import com.fooddelivery.entity.Dish;
import com.fooddelivery.entity.DishCategory;
import com.fooddelivery.entity.Shop;
import com.fooddelivery.service.DishService;
import com.fooddelivery.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
public class DishController {
    
    private final DishService dishService;
    private final ShopService shopService;
    
    // 分类管理
    @GetMapping("/categories")
    public Result<List<DishCategory>> getCategories() {
        Shop shop = shopService.getMyShop();
        return Result.success(dishService.getCategories(shop.getId()));
    }
    
    @PostMapping("/categories")
    @OperationLogger(module = "菜品管理", operation = "添加分类")
    public Result<Void> createCategory(@Valid @RequestBody CategoryRequest request) {
        dishService.createCategory(request);
        return Result.success();
    }
    
    @PutMapping("/categories/{id}")
    @OperationLogger(module = "菜品管理", operation = "更新分类")
    public Result<Void> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        dishService.updateCategory(id, request);
        return Result.success();
    }
    
    @DeleteMapping("/categories/{id}")
    @OperationLogger(module = "菜品管理", operation = "删除分类")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        dishService.deleteCategory(id);
        return Result.success();
    }
    
    // 菜品管理
    @GetMapping("/dishes")
    public Result<List<Dish>> getDishes() {
        Shop shop = shopService.getMyShop();
        return Result.success(dishService.getDishesByShop(shop.getId()));
    }
    
    @GetMapping("/dishes/{id}")
    public Result<Dish> getDish(@PathVariable Long id) {
        return Result.success(dishService.getById(id));
    }
    
    @PostMapping("/dishes")
    @OperationLogger(module = "菜品管理", operation = "添加菜品")
    public Result<Void> createDish(@Valid @RequestBody DishRequest request) {
        dishService.createDish(request);
        return Result.success();
    }
    
    @PutMapping("/dishes/{id}")
    @OperationLogger(module = "菜品管理", operation = "更新菜品")
    public Result<Void> updateDish(@PathVariable Long id, @Valid @RequestBody DishRequest request) {
        dishService.updateDish(id, request);
        return Result.success();
    }
    
    @DeleteMapping("/dishes/{id}")
    @OperationLogger(module = "菜品管理", operation = "删除菜品")
    public Result<Void> deleteDish(@PathVariable Long id) {
        dishService.deleteDish(id);
        return Result.success();
    }
    
    @PutMapping("/dishes/{id}/status")
    @OperationLogger(module = "菜品管理", operation = "更新菜品状态")
    public Result<Void> updateDishStatus(@PathVariable Long id, @RequestParam Integer status) {
        dishService.updateDishStatus(id, status);
        return Result.success();
    }
}
