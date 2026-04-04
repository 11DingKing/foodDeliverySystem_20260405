package com.fooddelivery.service.impl;

import com.fooddelivery.dto.PasswordUpdateRequest;
import com.fooddelivery.dto.UserUpdateRequest;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.mapper.UserMapper;
import com.fooddelivery.service.UserService;
import com.fooddelivery.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public User getCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }
    
    @Override
    public void updateProfile(UserUpdateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = new User();
        user.setId(userId);
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        userMapper.updateById(user);
        
        log.info("用户更新个人信息: userId={}", userId);
    }
    
    @Override
    public void updatePassword(PasswordUpdateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userMapper.selectById(userId);
        
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(updateUser);
        
        log.info("用户修改密码: userId={}", userId);
    }
    
    @Override
    public void updateAvatar(String avatarUrl) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = new User();
        user.setId(userId);
        user.setAvatar(avatarUrl);
        userMapper.updateById(user);
        
        log.info("用户更新头像: userId={}", userId);
    }
}
