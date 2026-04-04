package com.fooddelivery.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fooddelivery.common.Constants;
import com.fooddelivery.common.PageResult;
import com.fooddelivery.dto.OrderCreateRequest;
import com.fooddelivery.entity.*;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.mapper.*;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.service.ShopService;
import com.fooddelivery.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final DishMapper dishMapper;
    private final ShopMapper shopMapper;
    private final UserAddressMapper addressMapper;
    private final ShopService shopService;
    
    @Override
    @Transactional
    public Order createOrder(OrderCreateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        
        // 获取店铺信息
        Shop shop = shopMapper.selectById(request.getShopId());
        if (shop == null || shop.getStatus() != Constants.SHOP_STATUS_OPEN) {
            throw new BusinessException("店铺不存在或已休息");
        }
        
        // 获取地址信息
        UserAddress address = addressMapper.selectById(request.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }
        
        // 计算订单金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (OrderCreateRequest.CartItem item : request.getItems()) {
            Dish dish = dishMapper.selectById(item.getDishId());
            if (dish == null || !dish.getShopId().equals(request.getShopId())) {
                throw new BusinessException("菜品不存在");
            }
            if (dish.getStatus() != Constants.DISH_STATUS_ON) {
                throw new BusinessException("菜品 " + dish.getName() + " 已下架");
            }
            
            BigDecimal subtotal = dish.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
            
            OrderItem orderItem = new OrderItem();
            orderItem.setDishId(dish.getId());
            orderItem.setDishName(dish.getName());
            orderItem.setDishImage(dish.getImage());
            orderItem.setPrice(dish.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setSubtotal(subtotal);
            orderItems.add(orderItem);
        }
        
        // 检查起送价
        if (totalAmount.compareTo(shop.getMinPrice()) < 0) {
            throw new BusinessException("未达到起送价 ¥" + shop.getMinPrice());
        }
        
        BigDecimal payAmount = totalAmount.add(shop.getDeliveryFee());
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(IdUtil.getSnowflakeNextIdStr());
        order.setUserId(userId);
        order.setShopId(shop.getId());
        order.setTotalAmount(totalAmount);
        order.setDeliveryFee(shop.getDeliveryFee());
        order.setPayAmount(payAmount);
        order.setStatus(Constants.ORDER_STATUS_UNPAID);
        order.setContactName(address.getContactName());
        order.setContactPhone(address.getContactPhone());
        order.setDeliveryAddress(buildFullAddress(address));
        order.setRemark(request.getRemark());
        
        orderMapper.insert(order);
        
        // 保存订单明细
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }
        
        log.info("用户创建订单: userId={}, orderId={}, orderNo={}", userId, order.getId(), order.getOrderNo());
        return order;
    }
    
    private String buildFullAddress(UserAddress address) {
        StringBuilder sb = new StringBuilder();
        if (address.getProvince() != null) sb.append(address.getProvince());
        if (address.getCity() != null) sb.append(address.getCity());
        if (address.getDistrict() != null) sb.append(address.getDistrict());
        sb.append(address.getDetail());
        return sb.toString();
    }
    
    @Override
    public PageResult<Order> getMyOrders(int page, int size, Integer status) {
        Long userId = SecurityUtil.getCurrentUserId();
        Page<Order> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreatedAt);
        
        Page<Order> result = orderMapper.selectPage(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    public Order getOrderDetail(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }
    
    @Override
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId)
        );
    }
    
    @Override
    @Transactional
    public void payOrder(Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        Order order = orderMapper.selectById(id);
        
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != Constants.ORDER_STATUS_UNPAID) {
            throw new BusinessException("订单状态不正确");
        }
        
        order.setStatus(Constants.ORDER_STATUS_PENDING);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        log.info("用户支付订单: userId={}, orderId={}", userId, id);
    }
    
    @Override
    @Transactional
    public void cancelOrder(Long id, String reason) {
        Long userId = SecurityUtil.getCurrentUserId();
        Order order = orderMapper.selectById(id);
        
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() > Constants.ORDER_STATUS_PENDING) {
            throw new BusinessException("订单已在处理中，无法取消");
        }
        
        order.setStatus(Constants.ORDER_STATUS_CANCELLED);
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(reason);
        orderMapper.updateById(order);
        
        log.info("用户取消订单: userId={}, orderId={}, reason={}", userId, id, reason);
    }
    
    @Override
    public Map<String, Long> getShopOrderStats() {
        Shop shop = shopService.getMyShop();
        if (shop == null) {
            throw new BusinessException("您还没有店铺");
        }
        
        Map<String, Long> stats = new HashMap<>();
        
        // 待接单数量
        Long pending = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getShopId, shop.getId())
                        .eq(Order::getStatus, Constants.ORDER_STATUS_PENDING)
        );
        stats.put("pending", pending);
        
        // 备餐中数量
        Long preparing = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getShopId, shop.getId())
                        .eq(Order::getStatus, Constants.ORDER_STATUS_PREPARING)
        );
        stats.put("preparing", preparing);
        
        // 已完成数量
        Long completed = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getShopId, shop.getId())
                        .eq(Order::getStatus, Constants.ORDER_STATUS_COMPLETED)
        );
        stats.put("completed", completed);
        
        return stats;
    }
    
    @Override
    public PageResult<Order> getShopOrders(int page, int size, Integer status) {
        Shop shop = shopService.getMyShop();
        if (shop == null) {
            throw new BusinessException("您还没有店铺");
        }
        
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getShopId, shop.getId())
                .ne(Order::getStatus, Constants.ORDER_STATUS_UNPAID);
        
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreatedAt);
        
        Page<Order> result = orderMapper.selectPage(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    @Transactional
    public void acceptOrder(Long id) {
        Shop shop = shopService.getMyShop();
        Order order = orderMapper.selectById(id);
        
        if (order == null || !order.getShopId().equals(shop.getId())) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != Constants.ORDER_STATUS_PENDING) {
            throw new BusinessException("订单状态不正确");
        }
        
        order.setStatus(Constants.ORDER_STATUS_PREPARING);
        order.setAcceptTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        log.info("商家接单: shopId={}, orderId={}", shop.getId(), id);
    }
    
    @Override
    @Transactional
    public void prepareOrder(Long id) {
        Shop shop = shopService.getMyShop();
        Order order = orderMapper.selectById(id);
        
        if (order == null || !order.getShopId().equals(shop.getId())) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != Constants.ORDER_STATUS_PREPARING) {
            throw new BusinessException("订单状态不正确");
        }
        
        order.setStatus(Constants.ORDER_STATUS_READY);
        orderMapper.updateById(order);
        
        log.info("商家备餐完成: shopId={}, orderId={}", shop.getId(), id);
    }
    
    @Override
    @Transactional
    public void readyOrder(Long id) {
        prepareOrder(id);
    }
    
    @Override
    public PageResult<Order> getAvailableOrders(int page, int size) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, Constants.ORDER_STATUS_READY)
                .isNull(Order::getRiderId)
                .orderByAsc(Order::getCreatedAt);
        
        Page<Order> result = orderMapper.selectPage(pageParam, wrapper);
        
        // 填充店铺信息
        for (Order order : result.getRecords()) {
            fillShopInfo(order);
        }
        
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    public PageResult<Order> getRiderOrders(int page, int size, Integer status) {
        Long userId = SecurityUtil.getCurrentUserId();
        Page<Order> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getRiderId, userId);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreatedAt);
        
        Page<Order> result = orderMapper.selectPage(pageParam, wrapper);
        
        // 填充店铺信息
        for (Order order : result.getRecords()) {
            fillShopInfo(order);
        }
        
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
    
    @Override
    @Transactional
    public void pickupOrder(Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        Order order = orderMapper.selectById(id);
        
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != Constants.ORDER_STATUS_READY) {
            throw new BusinessException("订单状态不正确");
        }
        if (order.getRiderId() != null && !order.getRiderId().equals(userId)) {
            throw new BusinessException("订单已被其他骑手接单");
        }
        
        order.setRiderId(userId);
        order.setStatus(Constants.ORDER_STATUS_DELIVERING);
        order.setPickupTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        log.info("骑手接单: riderId={}, orderId={}", userId, id);
    }
    
    @Override
    @Transactional
    public void deliverOrder(Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        Order order = orderMapper.selectById(id);
        
        if (order == null || !userId.equals(order.getRiderId())) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != Constants.ORDER_STATUS_DELIVERING) {
            throw new BusinessException("订单状态不正确");
        }
        
        order.setStatus(Constants.ORDER_STATUS_COMPLETED);
        order.setDeliverTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        // 更新店铺月销量
        Shop shop = shopMapper.selectById(order.getShopId());
        shop.setMonthlySales(shop.getMonthlySales() + 1);
        shopMapper.updateById(shop);
        
        // 更新菜品销量
        List<OrderItem> items = getOrderItems(id);
        for (OrderItem item : items) {
            Dish dish = dishMapper.selectById(item.getDishId());
            if (dish != null) {
                dish.setSales(dish.getSales() + item.getQuantity());
                dishMapper.updateById(dish);
            }
        }
        
        log.info("骑手送达订单: riderId={}, orderId={}", userId, id);
    }
    
    /**
     * 填充订单的店铺信息
     */
    private void fillShopInfo(Order order) {
        if (order.getShopId() != null) {
            Shop shop = shopMapper.selectById(order.getShopId());
            if (shop != null) {
                order.setShopName(shop.getName());
                order.setShopPhone(shop.getPhone());
                // 拼接店铺完整地址
                StringBuilder address = new StringBuilder();
                if (shop.getProvince() != null) address.append(shop.getProvince());
                if (shop.getCity() != null) address.append(shop.getCity());
                if (shop.getDistrict() != null) address.append(shop.getDistrict());
                if (shop.getAddress() != null) address.append(shop.getAddress());
                order.setShopAddress(address.toString());
            }
        }
    }
}
