package com.fooddelivery.service;

import com.fooddelivery.common.PageResult;
import com.fooddelivery.dto.ShopRequest;
import com.fooddelivery.entity.Shop;

public interface ShopService {
    PageResult<Shop> listShops(int page, int size, String keyword);
    Shop getById(Long id);
    Shop getMyShop();
    void applyShop(ShopRequest request);
    void updateShop(ShopRequest request);
    void updateStatus(Integer status);
}
