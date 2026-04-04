package com.fooddelivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fooddelivery.dto.AddressRequest;
import com.fooddelivery.entity.UserAddress;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.mapper.UserAddressMapper;
import com.fooddelivery.service.AddressService;
import com.fooddelivery.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    
    private final UserAddressMapper addressMapper;
    
    @Override
    public List<UserAddress> getMyAddresses() {
        Long userId = SecurityUtil.getCurrentUserId();
        return addressMapper.selectList(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
                        .orderByDesc(UserAddress::getIsDefault)
                        .orderByDesc(UserAddress::getCreatedAt)
        );
    }
    
    @Override
    public UserAddress getById(Long id) {
        UserAddress address = addressMapper.selectById(id);
        if (address == null || !address.getUserId().equals(SecurityUtil.getCurrentUserId())) {
            throw new BusinessException("地址不存在");
        }
        return address;
    }
    
    @Override
    @Transactional
    public void create(AddressRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        
        UserAddress address = new UserAddress();
        address.setUserId(userId);
        address.setContactName(request.getContactName());
        address.setContactPhone(request.getContactPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetail(request.getDetail());
        address.setIsDefault(0);
        
        // 如果设为默认，先取消其他默认
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearDefault(userId);
            address.setIsDefault(1);
        }
        
        addressMapper.insert(address);
        log.info("用户添加地址: userId={}, addressId={}", userId, address.getId());
    }
    
    @Override
    @Transactional
    public void update(Long id, AddressRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        UserAddress existing = getById(id);
        
        existing.setContactName(request.getContactName());
        existing.setContactPhone(request.getContactPhone());
        existing.setProvince(request.getProvince());
        existing.setCity(request.getCity());
        existing.setDistrict(request.getDistrict());
        existing.setDetail(request.getDetail());
        
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearDefault(userId);
            existing.setIsDefault(1);
        }
        
        addressMapper.updateById(existing);
        log.info("用户更新地址: userId={}, addressId={}", userId, id);
    }
    
    @Override
    public void delete(Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        getById(id); // 验证权限
        addressMapper.deleteById(id);
        log.info("用户删除地址: userId={}, addressId={}", userId, id);
    }
    
    @Override
    @Transactional
    public void setDefault(Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        getById(id); // 验证权限
        
        clearDefault(userId);
        
        UserAddress address = new UserAddress();
        address.setId(id);
        address.setIsDefault(1);
        addressMapper.updateById(address);
        
        log.info("用户设置默认地址: userId={}, addressId={}", userId, id);
    }
    
    private void clearDefault(Long userId) {
        addressMapper.update(null, 
                new LambdaUpdateWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
                        .set(UserAddress::getIsDefault, 0)
        );
    }
}
