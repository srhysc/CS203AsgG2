package com.cs203.grp2.Asg2.config;

import com.cs203.grp2.Asg2.exceptions.UserAuthorizationException;
import com.cs203.grp2.Asg2.models.User;
import com.cs203.grp2.Asg2.service.UserService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClerkJwtFilterTest {

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private UserService userService;

    @Mock
    private FilterChain filterChain;

    private ClerkJwtFilter clerkJwtFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        clerkJwtFilter = new ClerkJwtFilter(jwtDecoder, userService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_WithValidJwt_ShouldAuthenticateUser() throws Exception {
        // Arrange
        String token = "valid.jwt.token";
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", "user123");
        claims.put("email", "test@example.com");
        claims.put("username", "testuser");

        Jwt jwt = new Jwt(token, Instant.now(), Instant.now().plusSeconds(3600), 
                         Map.of("alg", "RS256"), claims);

        User mockUser = new User();
        mockUser.setId("user123");
        mockUser.setEmail("test@example.com");
        mockUser.setUsername("testuser");
        mockUser.setRole(User.Role.USER);

        when(jwtDecoder.decode(token)).thenReturn(jwt);
        when(userService.getOrCreateUser("user123", "test@example.com", "testuser"))
            .thenReturn(mockUser);

        // Act
        clerkJwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(mockUser, auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithNullUsername_ShouldUseFallbackFromEmail() throws Exception {
        // Arrange
        String token = "valid.jwt.token";
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", "user123");
        claims.put("email", "testuser@example.com");
        claims.put("username", null);

        Jwt jwt = new Jwt(token, Instant.now(), Instant.now().plusSeconds(3600),
                         Map.of("alg", "RS256"), claims);

        User mockUser = new User();
        mockUser.setId("user123");
        mockUser.setEmail("testuser@example.com");
        mockUser.setUsername("testuser");
        mockUser.setRole(User.Role.USER);

        when(jwtDecoder.decode(token)).thenReturn(jwt);
        when(userService.getOrCreateUser("user123", "testuser@example.com", "testuser"))
            .thenReturn(mockUser);

        // Act
        clerkJwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(userService).getOrCreateUser("user123", "testuser@example.com", "testuser");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithAdminRole_ShouldSetAdminAuthority() throws Exception {
        // Arrange
        String token = "valid.jwt.token";
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", "admin123");
        claims.put("email", "admin@example.com");
        claims.put("username", "adminuser");

        Jwt jwt = new Jwt(token, Instant.now(), Instant.now().plusSeconds(3600),
                         Map.of("alg", "RS256"), claims);

        User mockUser = new User();
        mockUser.setId("admin123");
        mockUser.setEmail("admin@example.com");
        mockUser.setUsername("adminuser");
        mockUser.setRole(User.Role.ADMIN);

        when(jwtDecoder.decode(token)).thenReturn(jwt);
        when(userService.getOrCreateUser("admin123", "admin@example.com", "adminuser"))
            .thenReturn(mockUser);

        // Act
        clerkJwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithNoAuthorizationHeader_ShouldContinueFilterChain() throws Exception {
        // Act
        clerkJwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(jwtDecoder, never()).decode(anyString());
    }

    @Test
    void testDoFilterInternal_WithInvalidHeaderFormat_ShouldContinueFilterChain() throws Exception {
        // Arrange
        request.addHeader(HttpHeaders.AUTHORIZATION, "InvalidFormat token");

        // Act
        clerkJwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(jwtDecoder, never()).decode(anyString());
    }

    @Test
    void testDoFilterInternal_WithInvalidJwt_ShouldThrowUserAuthorizationException() throws Exception {
        // Arrange
        String token = "invalid.jwt.token";
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        when(jwtDecoder.decode(token)).thenThrow(new JwtException("Invalid JWT"));

        // Act & Assert
        UserAuthorizationException exception = assertThrows(UserAuthorizationException.class, () -> {
            clerkJwtFilter.doFilterInternal(request, response, filterChain);
        });

        assertTrue(exception.getMessage().contains("unauthorized"));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithExpiredJwt_ShouldThrowUserAuthorizationException() throws Exception {
        // Arrange
        String token = "expired.jwt.token";
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        when(jwtDecoder.decode(token)).thenThrow(new JwtException("JWT expired"));

        // Act & Assert
        UserAuthorizationException exception = assertThrows(UserAuthorizationException.class, () -> {
            clerkJwtFilter.doFilterInternal(request, response, filterChain);
        });
        
        assertTrue(exception.getMessage().contains("unauthorized"));
    }

    @Test
    void testDoFilterInternal_WithExecutionException_ShouldThrowRuntimeException() throws Exception {
        // Arrange
        String token = "valid.jwt.token";
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", "user123");
        claims.put("email", "test@example.com");
        claims.put("username", "testuser");

        Jwt jwt = new Jwt(token, Instant.now(), Instant.now().plusSeconds(3600),
                         Map.of("alg", "RS256"), claims);

        when(jwtDecoder.decode(token)).thenReturn(jwt);
        when(userService.getOrCreateUser(anyString(), anyString(), anyString()))
            .thenThrow(new ExecutionException("Firebase error", new Exception()));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clerkJwtFilter.doFilterInternal(request, response, filterChain);
        });

        assertTrue(exception.getMessage().contains("Firebase error"));
    }

    @Test
    void testDoFilterInternal_WithInterruptedException_ShouldThrowRuntimeException() throws Exception {
        // Arrange
        String token = "valid.jwt.token";
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", "user123");
        claims.put("email", "test@example.com");
        claims.put("username", "testuser");

        Jwt jwt = new Jwt(token, Instant.now(), Instant.now().plusSeconds(3600),
                         Map.of("alg", "RS256"), claims);

        when(jwtDecoder.decode(token)).thenReturn(jwt);
        when(userService.getOrCreateUser(anyString(), anyString(), anyString()))
            .thenThrow(new InterruptedException("Operation interrupted"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clerkJwtFilter.doFilterInternal(request, response, filterChain);
        });

        assertTrue(exception.getMessage().contains("Firebase operation interrupted"));
        assertTrue(Thread.interrupted()); // Verify interrupt flag was restored
    }

    @Test
    void testDoFilterInternal_WithBearerTokenOnly_ShouldDecodeEmptyToken() throws Exception {
        // Arrange
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer ");

        when(jwtDecoder.decode("")).thenThrow(new JwtException("Empty token"));

        // Act & Assert
        UserAuthorizationException exception = assertThrows(UserAuthorizationException.class, () -> {
            clerkJwtFilter.doFilterInternal(request, response, filterChain);
        });
        
        assertTrue(exception.getMessage().contains("unauthorized"));
    }
}
