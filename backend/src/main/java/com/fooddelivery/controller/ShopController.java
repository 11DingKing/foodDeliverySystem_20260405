package com.fooddelivery.controller;

import com.fooddelivery.annotation.OperationLogger;
import com.fooddelivery.common.PageResult;
import com.fooddelivery.common.Result;
import com.fooddelivery.dto.ShopRequest;
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
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {
    
    private final ShopService shopService;
    private final DishService dishService;
    
    @GetMapping
    public Result<PageResult<Shop>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.success(shopService.listShops(page, size, keyword));
    }
    
    @GetMapping("/{id}")
    public Result<Shop> get(@PathVariable Long id) {
        return Result.success(shopService.getById(id));
    }
    
    @GetMapping("/{id}/categories")
    public Result<List<DishCategory>> getCategories(@PathVariable Long id) {
        return Result.success(dishService.getCategories(id));
    }
    
    @GetMapping("/{id}/dishes")
    public Result<List<Dish>> getDishes(@PathVariable Long id) {
        return Result.success(dishService.getDishesByShop(id));
    }
    
    @GetMapping("/my")
    public Result<Shop> getMyShop() {
        return Result.success(shopService.getMyShop());
    }
    
    @PostMapping("/apply")
    @OperationLogger(module = "店铺管理", operation = "申请开店")
    public Result<Void> apply(@Valid @RequestBody ShopRequest request) {
        shopService.applyShop(request);
        return Result.success();
    }
    
    @PutMapping("/my")
    @OperationLogger(module = "店铺管理", operation = "更新店铺信息")
    public Result<Void> update(@Valid @RequestBody ShopRequest request) {
        shopService.updateShop(request);
        return Result.success();
    }
    
    @PutMapping("/my/status")
    @OperationLogger(module = "店铺管理", operation = "更新店铺状态")
    public Result<Void> updateStatus(@RequestParam Integer status) {
        shopService.updateStatus(status);
        return Result.success();
    }
}
