package com.fooddelivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fooddelivery.common.Constants;
import com.fooddelivery.dto.RiderApplicationRequest;
import com.fooddelivery.entity.RiderApplication;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.mapper.RiderApplicationMapper;
import com.fooddelivery.mapper.UserMapper;
import com.fooddelivery.service.RiderService;
import com.fooddelivery.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiderServiceImpl implements RiderService {
    
    private final RiderApplicationMapper applicationMapper;
    private final UserMapper userMapper;
    
    @Override
    @Transactional
    public void apply(RiderApplicationRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        
        // 检查是否已有申请
        RiderApplication existing = applicationMapper.selectOne(
                new LambdaQueryWrapper<RiderApplication>()
                        .eq(RiderApplication::getUserId, userId)
                        .ne(RiderApplication::getStatus, Constants.APPLICATION_REJECTED)
        );
        
        if (existing != null) {
            if (existing.getStatus() == Constants.APPLICATION_PENDING) {
                throw new BusinessException("您已提交申请，请等待审核");
            }
            if (existing.getStatus() == Constants.APPLICATION_APPROVED) {
                throw new BusinessException("您已是骑手");
            }
        }
        
        RiderApplication application = new RiderApplication();
        application.setUserId(userId);
        application.setRealName(request.getRealName());
        application.setIdCard(request.getIdCard());
        application.setPhone(request.getPhone());
        application.setStatus(Constants.APPLICATION_APPROVED); // 自动通过，简化流程
        
        applicationMapper.insert(application);
        
        // 更新用户角色为骑手
        User user = new User();
        user.setId(userId);
        user.setRole(Constants.ROLE_RIDER);
        userMapper.updateById(user);
        
        log.info("用户申请成为骑手: userId={}", userId);
    }
    
    @Override
    public RiderApplication getMyApplication() {
        Long userId = SecurityUtil.getCurrentUserId();
        return applicationMapper.selectOne(
                new LambdaQueryWrapper<RiderApplication>()
                        .eq(RiderApplication::getUserId, userId)
                        .orderByDesc(RiderApplication::getCreatedAt)
                        .last("LIMIT 1")
        );
    }
}
