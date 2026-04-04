package com.fooddelivery.controller;

import com.fooddelivery.annotation.OperationLogger;
import com.fooddelivery.common.PageResult;
import com.fooddelivery.common.Result;
import com.fooddelivery.dto.OrderCreateRequest;
import com.fooddelivery.entity.Order;
import com.fooddelivery.entity.OrderItem;
import com.fooddelivery.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    // 用户订单
    @PostMapping
    @OperationLogger(module = "订单管理", operation = "创建订单")
    public Result<Order> create(@Valid @RequestBody OrderCreateRequest request) {
        return Result.success(orderService.createOrder(request));
    }
    
    @GetMapping
    public Result<PageResult<Order>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        return Result.success(orderService.getMyOrders(page, size, status));
    }
    
    @GetMapping("/{id}")
    public Result<Order> get(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(id));
    }
    
    @GetMapping("/{id}/items")
    public Result<List<OrderItem>> getItems(@PathVariable Long id) {
        return Result.success(orderService.getOrderItems(id));
    }
    
    @PostMapping("/{id}/pay")
    @OperationLogger(module = "订单管理", operation = "支付订单")
    public Result<Void> pay(@PathVariable Long id) {
        orderService.payOrder(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/cancel")
    @OperationLogger(module = "订单管理", operation = "取消订单")
    public Result<Void> cancel(@PathVariable Long id, @RequestParam(required = false) String reason) {
        orderService.cancelOrder(id, reason);
        return Result.success();
    }
    
    // 商家订单
    @GetMapping("/shop")
    public Result<PageResult<Order>> shopOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        return Result.success(orderService.getShopOrders(page, size, status));
    }
    
    @GetMapping("/shop/stats")
    public Result<java.util.Map<String, Long>> shopStats() {
        return Result.success(orderService.getShopOrderStats());
    }
    
    @PostMapping("/{id}/accept")
    @OperationLogger(module = "订单管理", operation = "商家接单")
    public Result<Void> accept(@PathVariable Long id) {
        orderService.acceptOrder(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/ready")
    @OperationLogger(module = "订单管理", operation = "备餐完成")
    public Result<Void> ready(@PathVariable Long id) {
        orderService.readyOrder(id);
        return Result.success();
    }
    
    // 骑手订单
    @GetMapping("/available")
    public Result<PageResult<Order>> availableOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(orderService.getAvailableOrders(page, size));
    }
    
    @GetMapping("/rider")
    public Result<PageResult<Order>> riderOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        return Result.success(orderService.getRiderOrders(page, size, status));
    }
    
    @PostMapping("/{id}/pickup")
    @OperationLogger(module = "订单管理", operation = "骑手接单")
    public Result<Void> pickup(@PathVariable Long id) {
        orderService.pickupOrder(id);
        return Result.success();
    }
    
    @PostMapping("/{id}/deliver")
    @OperationLogger(module = "订单管理", operation = "确认送达")
    public Result<Void> deliver(@PathVariable Long id) {
        orderService.deliverOrder(id);
        return Result.success();
    }
}
