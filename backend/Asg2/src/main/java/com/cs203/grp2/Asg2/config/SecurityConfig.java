package com.cs203.grp2.Asg2.config;

import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import com.cs203.grp2.Asg2.config.ClerkJwtFilter; //Clerk authentication
import com.cs203.grp2.Asg2.service.UserService; //User services

@Configuration
@EnableWebSecurity

public class SecurityConfig {

  // create a new jwtDecoder to decode Clerk JWT
  @Bean
  public JwtDecoder jwtDecoder() {
    // Nimbus decoder is library for handling tokens
    return NimbusJwtDecoder.withJwkSetUri("https://many-hawk-2.clerk.accounts.dev/.well-known/jwks.json")
        .build();
  }

  @Bean
  public ClerkJwtFilter clerkJwtFilter(JwtDecoder jwtDecoder, UserService userService) {
    return new ClerkJwtFilter(jwtDecoder, userService);
  }

  // SecurityFilterChain defines which instances invoked for current request
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, ClerkJwtFilter clerkJwtFilter) throws Exception {
    // List all controller base paths here
    String[] DOMAIN_PATHS = new String[] {
        "/countries/**", // CountryController
        "/landed-cost/**", // LandedCostController
        "/petroleum/**", // PetroleumController
        "/route-optimization/**", // RouteOptimizationController
        "/shipping-fees/**", // ShippingFeesController
        "/tariffs/**", // TariffController
        "/trade-agreements/**", // TradeAgreementController
        "/refineries/**",
        "/convertables/**"
    };

    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> {
        }) 
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(clerkJwtFilter, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(auth -> auth
  
            //ONLY ADMINS ABLE TO ACCESS USERS
            .requestMatchers("/api/users").hasRole("ADMIN")
            // READ for USER or ADMIN on your domain controllers
            .requestMatchers(HttpMethod.GET, DOMAIN_PATHS).hasAnyRole("USER", "ADMIN")

            // WRITE only for ADMIN on your domain controllers
            .requestMatchers(HttpMethod.POST, DOMAIN_PATHS).hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, DOMAIN_PATHS).hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, DOMAIN_PATHS).hasRole("ADMIN")

            // Everything else: must be authenticated (for now, can change whenevr)
            .anyRequest().authenticated());

    return http.build();
  }

  // webconfig to declare CORS settings to allow communication
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    // allowed domains to send cross-origin request
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:5173", // dev
        "https://myfrontend.com" // production
    ));
    // allow all methods and request headers and cookies
    configuration.setAllowedMethods(Arrays.asList("*"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);

    // register configuration for spring
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration); // all paths
    return source;
  }
}
