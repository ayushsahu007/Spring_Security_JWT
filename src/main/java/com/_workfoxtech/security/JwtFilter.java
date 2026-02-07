package com._workfoxtech.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1️⃣ Authorization header read
        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // 2️⃣ Check Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            username = jwtUtil.extractUsername(jwtToken);
        }

        // 3️⃣ Agar user already authenticated nahi hai
        if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            // 4️⃣ Load user from DB
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            // 5️⃣ Validate token
            if (jwtUtil.validateToken(jwtToken, userDetails.getUsername())) {

                // 6️⃣ Create authentication object
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // 7️⃣ Set authentication in SecurityContext
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);
            }
        }

        // 8️⃣ Continue filter chain
        filterChain.doFilter(request, response);
    }
}

