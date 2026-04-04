package com.fooddelivery.service;

import com.fooddelivery.common.PageResult;
import com.fooddelivery.dto.OrderCreateRequest;
import com.fooddelivery.entity.Order;
import com.fooddelivery.entity.OrderItem;
import java.util.List;

public interface OrderService {
    // 用户
    Order createOrder(OrderCreateRequest request);
    PageResult<Order> getMyOrders(int page, int size, Integer status);
    Order getOrderDetail(Long id);
    List<OrderItem> getOrderItems(Long orderId);
    void payOrder(Long id);
    void cancelOrder(Long id, String reason);
    
    // 商家
    PageResult<Order> getShopOrders(int page, int size, Integer status);
    java.util.Map<String, Long> getShopOrderStats();
    void acceptOrder(Long id);
    void prepareOrder(Long id);
    void readyOrder(Long id);
    
    // 骑手
    PageResult<Order> getAvailableOrders(int page, int size);
    PageResult<Order> getRiderOrders(int page, int size, Integer status);
    void pickupOrder(Long id);
    void deliverOrder(Long id);
}
