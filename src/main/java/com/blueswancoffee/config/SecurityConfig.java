package com.blueswancoffee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for simplicity in this phase
                .csrf(csrf -> csrf.disable())
                // Allow all requests (we handle auth manually in Controllers)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                // Disable default login page to use our own
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());
        
        return http.build();
    }
}
