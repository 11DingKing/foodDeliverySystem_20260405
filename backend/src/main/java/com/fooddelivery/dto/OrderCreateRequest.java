package com.fooddelivery.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class OrderCreateRequest {
    @NotNull(message = "店铺ID不能为空")
    private Long shopId;
    
    @NotNull(message = "地址ID不能为空")
    private Long addressId;
    
    @NotEmpty(message = "购物车不能为空")
    private List<CartItem> items;
    
    private String remark;
    
    @Data
    public static class CartItem {
        @NotNull(message = "菜品ID不能为空")
        private Long dishId;
        
        @NotNull(message = "数量不能为空")
        private Integer quantity;
    }
}
