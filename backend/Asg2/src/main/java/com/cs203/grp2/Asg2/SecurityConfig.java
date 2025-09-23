// src/main/java/com/cs203/grp2/Asg2/SecurityConfig.java
package com.cs203.grp2.Asg2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/tariffs/**", "/actuator/health").permitAll()
        .anyRequest().authenticated()
      )
      .httpBasic(); // keep basic auth for everything else
    return http.build();
  }
}
