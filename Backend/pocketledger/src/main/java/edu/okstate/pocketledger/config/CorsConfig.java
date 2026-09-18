package edu.okstate.pocketledger.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


// allows user to call this api from frontend
// adds origin permission for differing ports
@Configuration 
public class CorsConfig implements WebMvcConfigurer {

    // defaults to http://localhost:3000
    // set FRONTEND_ORIGIN environment to point to he deployed frontend
    @Value("${app.frontend-origin}")
    private String frontendOrigin;

    // called once at startup by Spring to collect CORS rules
    @Override 
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
        // every url under /api
                .allowedOrigins(frontendOrigin)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE");
                // only GET, HEAD, and POST are allowed by default
    }
}