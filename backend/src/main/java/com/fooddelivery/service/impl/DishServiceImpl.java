package com.fooddelivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fooddelivery.common.Constants;
import com.fooddelivery.dto.CategoryRequest;
import com.fooddelivery.dto.DishRequest;
import com.fooddelivery.entity.Dish;
import com.fooddelivery.entity.DishCategory;
import com.fooddelivery.entity.Shop;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.mapper.DishCategoryMapper;
import com.fooddelivery.mapper.DishMapper;
import com.fooddelivery.service.DishService;
import com.fooddelivery.service.ShopService;
import com.fooddelivery.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {
    
    private final DishMapper dishMapper;
    private final DishCategoryMapper categoryMapper;
    private final ShopService shopService;
    
    private Long getMyShopId() {
        Shop shop = shopService.getMyShop();
        if (shop == null) {
            throw new BusinessException("您还没有店铺");
        }
        return shop.getId();
    }
    
    @Override
    public List<DishCategory> getCategories(Long shopId) {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<DishCategory>()
                        .eq(DishCategory::getShopId, shopId)
                        .orderByAsc(DishCategory::getSort)
        );
    }
    
    @Override
    public void createCategory(CategoryRequest request) {
        Long shopId = getMyShopId();
        
        DishCategory category = new DishCategory();
        category.setShopId(shopId);
        category.setName(request.getName());
        category.setSort(request.getSort() != null ? request.getSort() : 0);
        
        categoryMapper.insert(category);
        log.info("商家添加菜品分类: shopId={}, categoryId={}", shopId, category.getId());
    }
    
    @Override
    public void updateCategory(Long id, CategoryRequest request) {
        Long shopId = getMyShopId();
        DishCategory category = categoryMapper.selectById(id);
        
        if (category == null || !category.getShopId().equals(shopId)) {
            throw new BusinessException("分类不存在");
        }
        
        category.setName(request.getName());
        category.setSort(request.getSort());
        categoryMapper.updateById(category);
        
        log.info("商家更新菜品分类: shopId={}, categoryId={}", shopId, id);
    }
    
    @Override
    public void deleteCategory(Long id) {
        Long shopId = getMyShopId();
        DishCategory category = categoryMapper.selectById(id);
        
        if (category == null || !category.getShopId().equals(shopId)) {
            throw new BusinessException("分类不存在");
        }
        
        // 检查分类下是否有菜品
        Long count = dishMapper.selectCount(
                new LambdaQueryWrapper<Dish>().eq(Dish::getCategoryId, id)
        );
        if (count > 0) {
            throw new BusinessException("该分类下还有菜品，无法删除");
        }
        
        categoryMapper.deleteById(id);
        log.info("商家删除菜品分类: shopId={}, categoryId={}", shopId, id);
    }
    
    @Override
    public List<Dish> getDishesByShop(Long shopId) {
        return dishMapper.selectList(
                new LambdaQueryWrapper<Dish>()
                        .eq(Dish::getShopId, shopId)
                        .eq(Dish::getStatus, Constants.DISH_STATUS_ON)
                        .orderByDesc(Dish::getSales)
        );
    }
    
    @Override
    public List<Dish> getDishesByCategory(Long categoryId) {
        return dishMapper.selectList(
                new LambdaQueryWrapper<Dish>()
                        .eq(Dish::getCategoryId, categoryId)
                        .eq(Dish::getStatus, Constants.DISH_STATUS_ON)
        );
    }
    
    @Override
    public Dish getById(Long id) {
        return dishMapper.selectById(id);
    }
    
    @Override
    public void createDish(DishRequest request) {
        Long shopId = getMyShopId();
        
        Dish dish = new Dish();
        dish.setShopId(shopId);
        dish.setCategoryId(request.getCategoryId());
        dish.setName(request.getName());
        dish.setImage(request.getImage());
        dish.setDescription(request.getDescription());
        dish.setPrice(request.getPrice());
        dish.setOriginalPrice(request.getOriginalPrice());
        dish.setStatus(request.getStatus() != null ? request.getStatus() : Constants.DISH_STATUS_ON);
        dish.setSales(0);
        
        dishMapper.insert(dish);
        log.info("商家添加菜品: shopId={}, dishId={}", shopId, dish.getId());
    }
    
    @Override
    public void updateDish(Long id, DishRequest request) {
        Long shopId = getMyShopId();
        Dish dish = dishMapper.selectById(id);
        
        if (dish == null || !dish.getShopId().equals(shopId)) {
            throw new BusinessException("菜品不存在");
        }
        
        dish.setCategoryId(request.getCategoryId());
        dish.setName(request.getName());
        dish.setImage(request.getImage());
        dish.setDescription(request.getDescription());
        dish.setPrice(request.getPrice());
        dish.setOriginalPrice(request.getOriginalPrice());
        if (request.getStatus() != null) {
            dish.setStatus(request.getStatus());
        }
        
        dishMapper.updateById(dish);
        log.info("商家更新菜品: shopId={}, dishId={}", shopId, id);
    }
    
    @Override
    public void deleteDish(Long id) {
        Long shopId = getMyShopId();
        Dish dish = dishMapper.selectById(id);
        
        if (dish == null || !dish.getShopId().equals(shopId)) {
            throw new BusinessException("菜品不存在");
        }
        
        dishMapper.deleteById(id);
        log.info("商家删除菜品: shopId={}, dishId={}", shopId, id);
    }
    
    @Override
    public void updateDishStatus(Long id, Integer status) {
        Long shopId = getMyShopId();
        Dish dish = dishMapper.selectById(id);
        
        if (dish == null || !dish.getShopId().equals(shopId)) {
            throw new BusinessException("菜品不存在");
        }
        
        dish.setStatus(status);
        dishMapper.updateById(dish);
        log.info("商家更新菜品状态: shopId={}, dishId={}, status={}", shopId, id, status);
    }
}
