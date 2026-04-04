package com.fooddelivery.controller;

import com.fooddelivery.annotation.OperationLogger;
import com.fooddelivery.common.Result;
import com.fooddelivery.dto.AddressRequest;
import com.fooddelivery.entity.UserAddress;
import com.fooddelivery.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {
    
    private final AddressService addressService;
    
    @GetMapping
    public Result<List<UserAddress>> list() {
        return Result.success(addressService.getMyAddresses());
    }
    
    @GetMapping("/{id}")
    public Result<UserAddress> get(@PathVariable Long id) {
        return Result.success(addressService.getById(id));
    }
    
    @PostMapping
    @OperationLogger(module = "地址管理", operation = "添加地址")
    public Result<Void> create(@Valid @RequestBody AddressRequest request) {
        addressService.create(request);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    @OperationLogger(module = "地址管理", operation = "更新地址")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody AddressRequest request) {
        addressService.update(id, request);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    @OperationLogger(module = "地址管理", operation = "删除地址")
    public Result<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return Result.success();
    }
    
    @PutMapping("/{id}/default")
    @OperationLogger(module = "地址管理", operation = "设置默认地址")
    public Result<Void> setDefault(@PathVariable Long id) {
        addressService.setDefault(id);
        return Result.success();
    }
}
