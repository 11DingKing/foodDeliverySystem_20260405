package com.fooddelivery.config;

import com.fooddelivery.entity.User;
import com.fooddelivery.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Check if test users exist
        User existingUser = userMapper.selectById(1L);
        if (existingUser != null) {
            // Update password for all test users
            String encodedPassword = passwordEncoder.encode("123456");
            log.info("Updating test user passwords...");
            
            for (long id = 1; id <= 8; id++) {
                User user = userMapper.selectById(id);
                if (user != null) {
                    user.setPassword(encodedPassword);
                    userMapper.updateById(user);
                    log.info("Updated password for user: {}", user.getUsername());
                }
            }
            log.info("Test user passwords updated successfully");
        }
    }
}
