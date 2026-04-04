package com.fooddelivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ShopRequest {
    @NotBlank(message = "店铺名称不能为空")
    private String name;
    
    @NotBlank(message = "店铺分类不能为空")
    private String category;
    
    private String logo;
    
    private String description;
    
    @NotBlank(message = "联系电话不能为空")
    private String phone;
    
    @NotBlank(message = "请选择省")
    private String province;
    
    @NotBlank(message = "请选择市")
    private String city;
    
    @NotBlank(message = "请选择区")
    private String district;
    
    @NotBlank(message = "详细地址不能为空")
    private String address;
    
    @NotNull(message = "起送价不能为空")
    private BigDecimal minPrice;
    
    @NotNull(message = "配送费不能为空")
    private BigDecimal deliveryFee;
}
