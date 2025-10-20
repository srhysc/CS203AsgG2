package com.cs203.grp2.Asg2.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

//for spring Authority and ROLES
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.Collections;
import com.cs203.grp2.Asg2.UserService;    //User services
import com.cs203.grp2.Asg2.User


//filter only runs once per API request to save unnecessary decoding
public class ClerkJwtFilter extends OncePerRequestFilter {
    //to decode clerk jwt
    private final JwtDecoder jwtDecoder;
    private final UserService userservice;

    public ClerkJwtFilter(JwtDecoder jwtDecoder, UserService userService ) {
        this.jwtDecoder = jwtDecoder;
        this.UserService = userService;
    }

    @Override
    //run for every API request made as part of SecurityFilterChain
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        //get authorization header from API request which contains Clerk JWT
        //containts the JWT issued by clerk after a successful login
        //in the form of [Authorization: Bearer xxxxxx]
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            //remove the "Bearer" to get actual JWT
            String token = header.substring(7);
            //try decoding JWT 
            try {
                //decode JWT using spring security OAuth2 JWT library
                Jwt jwt = jwtDecoder.decode(token);
                //extract subject(user's) userID and email
                String userId = jwt.getClaimAsString("sub");
                String email = jwt.getClaimAsString("email"); 

                //call userService's getOrCreateUser to get/create signed in user
                User user = userService.getOrCreateUser(userId, email);
                //Get list of user's ROLES
                List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().toString())
                );

                //Create a new Token with userID, email, and permission list for other controllers
                var auth = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        authorities
                );
                //tell rest of controllers that JWT is valid and user is authenticated!
                SecurityContextHolder.getContext().setAuthentication(auth);
            
            } catch (JwtException e) {
                //IF invalid or expired
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        //pass request down chain
        chain.doFilter(request, response);
    }
}
