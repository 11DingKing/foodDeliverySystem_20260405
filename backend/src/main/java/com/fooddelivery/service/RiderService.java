package com.fooddelivery.service;

import com.fooddelivery.dto.RiderApplicationRequest;
import com.fooddelivery.entity.RiderApplication;

public interface RiderService {
    void apply(RiderApplicationRequest request);
    RiderApplication getMyApplication();
}
