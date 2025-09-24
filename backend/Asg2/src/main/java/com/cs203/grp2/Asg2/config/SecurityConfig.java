package com.cs203.grp2.Asg2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // disable CSRF for testing
            .authorizeHttpRequests(auth -> auth
                // allow POST to /landedcost without auth
                .requestMatchers(HttpMethod.POST, "/landedcost").permitAll()
                // allow GET to tariffs and health checks
                .requestMatchers("/tariffs/**", "/actuator/health", "/vat").permitAll()
                // everything else requires auth
                .anyRequest().authenticated()
            )
            .httpBasic(); // keep basic auth for secured endpoints
        return http.build();
    }
}

