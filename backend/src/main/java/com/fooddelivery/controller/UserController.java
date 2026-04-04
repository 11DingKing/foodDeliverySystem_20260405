package com.fooddelivery.controller;

import com.fooddelivery.annotation.OperationLogger;
import com.fooddelivery.common.Result;
import com.fooddelivery.dto.PasswordUpdateRequest;
import com.fooddelivery.dto.UserUpdateRequest;
import com.fooddelivery.entity.User;
import com.fooddelivery.service.FileService;
import com.fooddelivery.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final FileService fileService;
    
    @GetMapping("/info")
    public Result<User> getCurrentUser() {
        return Result.success(userService.getCurrentUser());
    }
    
    @PutMapping("/profile")
    @OperationLogger(module = "用户管理", operation = "更新个人信息")
    public Result<Void> updateProfile(@Valid @RequestBody UserUpdateRequest request) {
        userService.updateProfile(request);
        return Result.success();
    }
    
    @PutMapping("/password")
    @OperationLogger(module = "用户管理", operation = "修改密码")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordUpdateRequest request) {
        userService.updatePassword(request);
        return Result.success();
    }
    
    @PostMapping("/avatar")
    @OperationLogger(module = "用户管理", operation = "更新头像")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String url = fileService.upload(file);
        userService.updateAvatar(url);
        return Result.success(url);
    }
}
