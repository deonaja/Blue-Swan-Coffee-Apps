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
                // Enable CSRF
                .csrf(org.springframework.security.config.Customizer.withDefaults())
                // Role-based Access Control
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/barista/**").hasRole("BARISTA")
                        .requestMatchers("/cart/**", "/payment/**", "/profile/**").authenticated()
                        .anyRequest().permitAll()
                )

                // Disable default login page interactions as we use custom controller
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                // Handle unauthenticated access by redirecting to login
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> response.sendRedirect("/login"))
                );
        
        return http.build();
    }

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}
