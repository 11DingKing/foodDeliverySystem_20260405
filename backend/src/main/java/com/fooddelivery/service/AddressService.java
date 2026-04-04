package com.fooddelivery.service;

import com.fooddelivery.dto.AddressRequest;
import com.fooddelivery.entity.UserAddress;
import java.util.List;

public interface AddressService {
    List<UserAddress> getMyAddresses();
    UserAddress getById(Long id);
    void create(AddressRequest request);
    void update(Long id, AddressRequest request);
    void delete(Long id);
    void setDefault(Long id);
}
