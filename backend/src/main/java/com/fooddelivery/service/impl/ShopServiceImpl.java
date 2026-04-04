package com.fooddelivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fooddelivery.common.Constants;
import com.fooddelivery.common.PageResult;
import com.fooddelivery.dto.ShopRequest;
import com.fooddelivery.entity.Shop;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.mapper.ShopMapper;
import com.fooddelivery.mapper.UserMapper;
import com.fooddelivery.service.ShopService;
import com.fooddelivery.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    
    private final ShopMapper shopMapper;
    private final UserMapper userMapper;
    
    @Override
    public PageResult<Shop> listShops(int page, int size, String keyword) {
        Page<Shop> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<Shop>()
                .eq(Shop::getStatus, Constants.SHOP_STATUS_OPEN);
        
        if (StringUtils.hasText(keyword)) {
            // 搜索店铺名称、描述或分类
            wrapper.and(w -> w
                .like(Shop::getName, keyword)
                .or()
                .like(Shop::getDescription, keyword)
                .or()
                .like(Shop::getCategory, keyword)
            );
        }
        
        wrapper.orderByDesc(Shop::getMonthlySales);
        
        Page<Shop> result = shopMapper.selectPage(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    public Shop getById(Long id) {
        Shop shop = shopMapper.selectById(id);
        if (shop == null) {
            throw new BusinessException("店铺不存在");
        }
        return shop;
    }
    
    @Override
    public Shop getMyShop() {
        Long userId = SecurityUtil.getCurrentUserId();
        return shopMapper.selectOne(
                new LambdaQueryWrapper<Shop>().eq(Shop::getUserId, userId)
        );
    }
    
    @Override
    @Transactional
    public void applyShop(ShopRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        
        // 检查是否已有店铺
        Shop existing = shopMapper.selectOne(
                new LambdaQueryWrapper<Shop>().eq(Shop::getUserId, userId)
        );
        if (existing != null) {
            throw new BusinessException("您已拥有店铺");
        }
        
        // 检查店铺名称是否重复
        Shop nameExists = shopMapper.selectOne(
                new LambdaQueryWrapper<Shop>().eq(Shop::getName, request.getName())
        );
        if (nameExists != null) {
            throw new BusinessException("店铺名称已存在，请更换一个名称");
        }
        
        Shop shop = new Shop();
        shop.setUserId(userId);
        shop.setName(request.getName());
        shop.setCategory(request.getCategory());
        shop.setLogo(request.getLogo());
        shop.setDescription(request.getDescription());
        shop.setPhone(request.getPhone());
        shop.setProvince(request.getProvince());
        shop.setCity(request.getCity());
        shop.setDistrict(request.getDistrict());
        shop.setAddress(request.getAddress());
        shop.setMinPrice(request.getMinPrice());
        shop.setDeliveryFee(request.getDeliveryFee());
        shop.setStatus(Constants.SHOP_STATUS_OPEN); // 自动通过，简化流程
        
        shopMapper.insert(shop);
        
        // 更新用户角色为商家
        User user = new User();
        user.setId(userId);
        user.setRole(Constants.ROLE_MERCHANT);
        userMapper.updateById(user);
        
        log.info("用户申请开店成功: userId={}, shopId={}", userId, shop.getId());
    }
    
    @Override
    public void updateShop(ShopRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Shop shop = getMyShop();
        if (shop == null) {
            throw new BusinessException("您还没有店铺");
        }
        
        shop.setName(request.getName());
        shop.setCategory(request.getCategory());
        shop.setLogo(request.getLogo());
        shop.setDescription(request.getDescription());
        shop.setPhone(request.getPhone());
        shop.setProvince(request.getProvince());
        shop.setCity(request.getCity());
        shop.setDistrict(request.getDistrict());
        shop.setAddress(request.getAddress());
        shop.setMinPrice(request.getMinPrice());
        shop.setDeliveryFee(request.getDeliveryFee());
        
        shopMapper.updateById(shop);
        log.info("商家更新店铺信息: userId={}, shopId={}", userId, shop.getId());
    }
    
    @Override
    public void updateStatus(Integer status) {
        Long userId = SecurityUtil.getCurrentUserId();
        Shop shop = getMyShop();
        if (shop == null) {
            throw new BusinessException("您还没有店铺");
        }
        
        shop.setStatus(status);
        shopMapper.updateById(shop);
        log.info("商家更新店铺状态: userId={}, shopId={}, status={}", userId, shop.getId(), status);
    }
}
