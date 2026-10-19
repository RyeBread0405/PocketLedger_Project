package edu.okstate.pocketledger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;



@Configuration
public class SecurityConfig{

    // signing secret from .env
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    private SecretKey secretKey() {
        return new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    // creates signed tokens when someone logs in
    @Bean 
    public JwtEncoder jwtEncoder() {
        return NimbusJwtEncoder.withSecretKey(secretKey()).build();
    }

    // checks signature on tokens coming back in
    @Bean 
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(secretKey()).build();
    }

    // hashes passwords
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // decides what endpoints need a login so SpringSecurity doesn't lock all our endpoints by default
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .authorizeHttpRequests(auth -> auth .requestMatchers("/api/health", "/api/auth/**").permitAll()

        .requestMatchers("/api/accounts").permitAll() .anyRequest().authenticated())    // until login exists

        // unblocking curl and fetch POSTs
        .csrf(csrf -> csrf.disable())

        // CorsConfig so frontend is still allowed
        .cors(withDefaults())

        // accept "Authorization: Bearer <token>" on protected endpoints
        .oauth2ResourceServer(oauth2->oauth2.jwt(withDefaults()));

        return http.build();
    }
}

