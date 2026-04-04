package com.fooddelivery.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${file.upload-path:uploads/}")
    private String uploadPath;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 使用 File 处理路径，确保跨平台兼容
        File uploadDir = new File(uploadPath);
        String absolutePath = uploadDir.getAbsolutePath();
        // 统一使用 / 作为路径分隔符
        absolutePath = absolutePath.replace("\\", "/");
        if (!absolutePath.endsWith("/")) {
            absolutePath += "/";
        }
        
        String resourceLocation = "file:" + absolutePath;
        log.info("配置静态资源映射: /uploads/** -> {}", resourceLocation);
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation);
    }
}
