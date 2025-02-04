package com.qvl.gethomeweb.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (uri.equals("/public/login")){
            String userAgent = request.getHeader("User-Agent");
            Date now = new Date();
            System.out.println("使用者於" + now + "使用了" + userAgent + "瀏覽器");
        }
        filterChain.doFilter(request, response);
    }
}
