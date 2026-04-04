package com.fooddelivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("rider_application")
public class RiderApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String realName;
    
    private String idCard;
    
    private String phone;
    
    private Integer status;
    
    private String rejectReason;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
