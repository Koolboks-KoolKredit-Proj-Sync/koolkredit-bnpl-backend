package com.koolboks.creditProject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global configuration for Cross-Origin Resource Sharing (CORS).
 * This configuration explicitly defines allowed origins to enable
 * credentials (like cookies or session IDs) securely.
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Apply this CORS configuration to all paths ("/**")
                registry.addMapping("/**")

                        // 1. Specify Allowed Origins
                        // Since allowCredentials(true) is set, we MUST list explicit origins
                        // and cannot use the wildcard "*".
                        .allowedOriginPatterns("http://localhost:*", "https://koolkredit-ui-avdf.vercel.app/")
//                        .allowedOrigins(
////                            "http://localhost:5173", // Common for Vite/Frontend development
////                            "http://localhost:8000"  // Alternative development port
//                            // TODO: When deploying to production, add the production domain(s) here, e.g.:
//                            // "https://your-production-app.com"
//                        )

                        // 2. Allowed Methods (e.g., GET, POST, PUT, DELETE)
                        .allowedMethods("*")

                        // 3. Allowed Headers (Includes Content-Type, Authorization, etc.)
                        .allowedHeaders("*")

                        // 4. Allow Credentials (Crucial for sending cookies/session IDs)
                        .allowCredentials(true);
            }
        };
    }
}