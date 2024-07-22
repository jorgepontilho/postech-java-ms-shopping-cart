package com.postech.shoppingcart.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        validateRequest(request);
        filterChain.doFilter(request, response);
    }

    private void validateRequest(HttpServletRequest request) {
        var token = this.recoverToken(request);
        if (token == null) {
            request.setAttribute("error_code", HttpStatus.BAD_REQUEST);
            request.setAttribute("error", "Bearer token inválido");
            return;
        }
        String s = validToken(token);

    }

    public String validToken(String token) {
        try {
            String login = tokenService.validateToken(token);
            Authentication authentication = new UsernamePasswordAuthenticationToken(login, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return login;
        } catch (Exception e) {
            return null;
        }
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}