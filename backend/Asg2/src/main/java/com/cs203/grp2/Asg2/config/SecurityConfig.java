// src/main/java/com/cs203/grp2/Asg2/SecurityConfig.java
package com.cs203.grp2.Asg2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  //SecurityFilterChain defines which instances invoked for current request
  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //Enable CORS support
    http
      .cors(cors -> {})
      .authorizeHttpRequests(auth -> auth
        //public access to the following URLs
        .requestMatchers("/tariffs/**", "/actuator/health","/vat").permitAll()
        //rest all need authorization
        .anyRequest().authenticated()
      )
      .httpBasic(); // enable basic auth for everything else
    return http.build();
  }

  //webconfig to declare CORS settings to allow communication
  @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //allowed domains to send cross-origin request
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",      // dev
            "https://myfrontend.com"     // production
        ));
        //allow all methods and request headers and cookies
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        //register configuration for spring
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // all paths
        return source;
    }
}
