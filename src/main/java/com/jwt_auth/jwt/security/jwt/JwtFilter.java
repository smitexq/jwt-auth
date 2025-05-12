package com.jwt_auth.jwt.security.jwt;

import com.jwt_auth.jwt.security.CustomUserDetails;
import com.jwt_auth.jwt.security.CustomUserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserServiceImpl customUserService;

    public JwtFilter(JwtService jwtService, CustomUserServiceImpl customUserService) {
        this.jwtService = jwtService;
        this.customUserService = customUserService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = getTokenFromRequest(request);
        if (token != null && jwtService.validateJwtToken(token)) {
            setCustomUserDetailsToSecurityContextHolder(token);
        }
        filterChain.doFilter(request, response);
    }

    private void setCustomUserDetailsToSecurityContextHolder(String token) {
        String email = jwtService.getEmailFromToken(token);
        CustomUserDetails customUserDetails = customUserService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
