package com.fooddelivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {
    @NotBlank(message = "联系人不能为空")
    private String contactName;
    
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;
    
    @NotBlank(message = "请选择省")
    private String province;
    
    @NotBlank(message = "请选择市")
    private String city;
    
    @NotBlank(message = "请选择区")
    private String district;
    
    @NotBlank(message = "详细地址不能为空")
    private String detail;
    
    private Boolean isDefault;
}
