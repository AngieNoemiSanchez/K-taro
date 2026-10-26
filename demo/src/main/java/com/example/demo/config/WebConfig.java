package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.service.ImageStorageService;

/** Publica la carpeta de imágenes subidas en /uploads/**. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ImageStorageService images;

    public WebConfig(ImageStorageService images) {
        this.images = images;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = images.root().toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }
        registry.addResourceHandler(ImageStorageService.PUBLIC_PREFIX + "**")
                .addResourceLocations(location);
    }
}