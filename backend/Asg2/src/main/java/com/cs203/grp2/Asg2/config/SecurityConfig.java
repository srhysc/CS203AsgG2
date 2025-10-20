package com.cs203.grp2.Asg2.config;

import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import com.cs203.grp2.Asg2.config.ClerkJwtFilter;  //Clerk authentication
import com.cs203.grp2.Asg2.user.UserService;    //User services




@Configuration
@EnableWebSecurity

public class SecurityConfig {
 
  //create a new jwtDecoder to decode Clerk JWT
  @Bean
  public JwtDecoder jwtDecoder() {
    //Nimbus decoder is library for handling tokens
    return NimbusJwtDecoder.withJwkSetUri("https://many-hawk-2.clerk.accounts.dev/.well-known/jwks.json")
    .build();
  }

  @Bean
    public ClerkJwtFilter clerkJwtFilter(JwtDecoder jwtDecoder, UserService userService) {
        return new ClerkJwtFilter(jwtDecoder, userService);
    }

  //SecurityFilterChain defines which instances invoked for current request
  @Bean
  SecurityFilterChain filterChain(HttpSecurity http, ClerkJwtFilter clerkJwtFilter) throws Exception {
    //Enable CORS support
    http
      .csrf(csrf -> csrf.disable())
      .cors(cors -> {})
      .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
      .httpBasic(httpBasic -> {}); // enable basic auth for secured endpoints
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

