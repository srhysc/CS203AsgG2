package com.cs203.grp2.Asg2.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

//for spring Authority and ROLES
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.Collections;
import com.cs203.grp2.Asg2.service.UserService;    //User services
import com.cs203.grp2.Asg2.models.User;
import java.util.List;
import java.util.concurrent.ExecutionException;
import com.cs203.grp2.Asg2.exceptions.UserAuthorizationException;


//aa
//filter only runs once per API request to save unnecessary decoding
public class ClerkJwtFilter extends OncePerRequestFilter {
    //to decode clerk jwt
    private final JwtDecoder jwtDecoder;
    private final UserService userService;

    public ClerkJwtFilter(JwtDecoder jwtDecoder, UserService userService ) {
        this.jwtDecoder = jwtDecoder;
        this.userService = userService;
    }

    @Override
    //run for every API request made as part of SecurityFilterChain
    
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {


        //get authorization header from API request which contains Clerk JWT
        //containts the JWT issued by clerk after a successful login
        //in the form of [Authorization: Bearer xxxxxx]
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

System.out.println("🔐 ClerkJwtFilter triggered for request: " + request.getRequestURI() + "header: " + header);

        if (header != null && header.startsWith("Bearer ")) {
            //remove the "Bearer" to get actual JWT
            String token = header.substring(7);

    System.out.println("🔐🔐🔐🔐 checking token " + token);

            //try decoding JWT 
            try {
                
                //decode JWT using spring security OAuth2 JWT library
                Jwt jwt = jwtDecoder.decode(token);

    System.out.println("🔍🔍🔍🔍 Decoded JWT claims: " + jwt.getClaims());

                //extract user's id, email and username based on custom template
                String userId = jwt.getClaimAsString("id");
                String email = jwt.getClaimAsString("email"); 
                String username = jwt.getClaimAsString("username"); 
                if (username == null){
                    username = email.split("@")[0]; // fallback
                } 


                //call userService's getOrCreateUser to get/create signed in user
                User user = userService.getOrCreateUser(userId, email, username);
                //Get list of user's ROLES
                List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().toString())
                );

    System.out.println("User created: " + authorities);

                //Create a new authentication object with userID, email, and permission list for other controllers
                var auth = new UsernamePasswordAuthenticationToken(
                        user, //user object - taken from firebase
                        null,
                        authorities //list of permissions - taken from firebase
                );

                //tell rest of controllers that JWT is valid and user is authenticated!
                SecurityContextHolder.getContext().setAuthentication(auth);
            
            } catch (JwtException e) {
                //IF invalid or expired
                throw new UserAuthorizationException("You are unauthorized, or JWT is invalid or expired!");
            } catch (ExecutionException e){
                throw new RuntimeException("Firebase error", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // restore interrupted status
                throw new RuntimeException("Firebase operation interrupted", e);
            }
        }
        //pass request down chain
        chain.doFilter(request, response);
    }
}
