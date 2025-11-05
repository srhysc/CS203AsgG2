import com.cs203.grp2.Asg2.exceptions.UserAuthorizationException;
import com.cs203.grp2.Asg2.models.User;
import com.cs203.grp2.Asg2.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

package com.cs203.grp2.Asg2.config;




@ExtendWith(MockitoExtension.class)
class ClerkJwtFilterTest {

    @Mock
    JwtDecoder jwtDecoder;

    @Mock
    UserService userService;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    FilterChain chain;

    @Captor
    ArgumentCaptor<String> headerCaptor;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_noAuthorizationHeader_doesNotAuthenticateAndContinues() throws Exception {
        ClerkJwtFilter filter = new ClerkJwtFilter(jwtDecoder, userService);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth, "Security context should remain empty when no Authorization header is present");
        verifyNoInteractions(jwtDecoder);
        verifyNoInteractions(userService);
    }

    @Test
    void doFilterInternal_withValidBearer_decodesJwtAndSetsAuthentication() throws Exception {
        ClerkJwtFilter filter = new ClerkJwtFilter(jwtDecoder, userService);

        String token = "valid-token";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("id")).thenReturn("user-123");
        when(jwt.getClaimAsString("email")).thenReturn("jdoe@example.com");
        when(jwt.getClaimAsString("username")).thenReturn("jdoe");
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        User user = mock(User.class);
        // ensure getOrCreateUser returns our mock user
        when(userService.getOrCreateUser("user-123", "jdoe@example.com", "jdoe")).thenReturn(user);

        filter.doFilterInternal(request, response, chain);

        verify(jwtDecoder).decode(token);
        verify(userService).getOrCreateUser("user-123", "jdoe@example.com", "jdoe");
        verify(chain).doFilter(request, response);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth, "Authentication should be set in the security context");
        assertEquals(user, auth.getPrincipal(), "Principal should be the user returned by userService");
        assertNull(auth.getCredentials(), "Credentials should be null");
        assertFalse(auth.getAuthorities().isEmpty(), "Authorities should be populated based on user role");
    }

    @Test
    void doFilterInternal_invalidJwt_throwsUserAuthorizationException() throws Exception {
        ClerkJwtFilter filter = new ClerkJwtFilter(jwtDecoder, userService);

        String token = "bad-token";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtDecoder.decode(token)).thenThrow(new JwtException("invalid"));

        UserAuthorizationException ex = assertThrows(UserAuthorizationException.class, () ->
                filter.doFilterInternal(request, response, chain)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("unauthor"), "Exception message should indicate unauthorized");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_userServiceExecutionException_wrappedInRuntime() throws Exception {
        ClerkJwtFilter filter = new ClerkJwtFilter(jwtDecoder, userService);

        String token = "token-exec";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("id")).thenReturn("u1");
        when(jwt.getClaimAsString("email")).thenReturn("a@b.com");
        when(jwt.getClaimAsString("username")).thenReturn("a");
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        when(userService.getOrCreateUser("u1", "a@b.com", "a")).thenThrow(new ExecutionException(new Exception("svc")));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                filter.doFilterInternal(request, response, chain)
        );
        assertNotNull(ex.getCause(), "RuntimeException should wrap the ExecutionException");
        assertTrue(ex.getCause() instanceof ExecutionException);
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_userServiceInterruptedException_restoresInterruptAndWraps() throws Exception {
        ClerkJwtFilter filter = new ClerkJwtFilter(jwtDecoder, userService);

        String token = "token-int";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("id")).thenReturn("u2");
        when(jwt.getClaimAsString("email")).thenReturn("x@y.com");
        when(jwt.getClaimAsString("username")).thenReturn("x");
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        // Ensure current thread is not interrupted before the call
        Thread.interrupted(); // clear interrupted status

        when(userService.getOrCreateUser("u2", "x@y.com", "x")).thenThrow(new InterruptedException("int"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                filter.doFilterInternal(request, response, chain)
        );
        assertNotNull(ex.getCause(), "RuntimeException should wrap the InterruptedException");
        assertTrue(ex.getCause() instanceof InterruptedException);
        assertTrue(Thread.currentThread().isInterrupted(), "Thread interrupted status should be restored by the filter");
        verify(chain, never()).doFilter(request, response);
    }
}