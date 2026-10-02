package edu.okstate.pocketledger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;


@Configuration
public class SecurityConfig{

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
        .cors(withDefaults());

        return http.build();
    }
}

