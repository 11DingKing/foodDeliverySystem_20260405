package com.fooddelivery.controller;

import com.fooddelivery.annotation.OperationLogger;
import com.fooddelivery.common.Result;
import com.fooddelivery.dto.RiderApplicationRequest;
import com.fooddelivery.entity.RiderApplication;
import com.fooddelivery.service.RiderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rider")
@RequiredArgsConstructor
public class RiderController {
    
    private final RiderService riderService;
    
    @PostMapping("/apply")
    @OperationLogger(module = "骑手管理", operation = "申请成为骑手")
    public Result<Void> apply(@Valid @RequestBody RiderApplicationRequest request) {
        riderService.apply(request);
        return Result.success();
    }
    
    @GetMapping("/application")
    public Result<RiderApplication> getApplication() {
        return Result.success(riderService.getMyApplication());
    }
}
