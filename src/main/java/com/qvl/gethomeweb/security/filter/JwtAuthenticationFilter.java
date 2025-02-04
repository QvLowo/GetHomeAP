package com.qvl.gethomeweb.security.filter;

import com.auth0.jwt.interfaces.Claim;
import com.qvl.gethomeweb.security.SecurityUserDetailsServiceImpl;
import com.qvl.gethomeweb.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private SecurityUserDetailsServiceImpl securityUserDetailsServiceImpl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String origin = request.getHeader("Origin");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT, DELETE,OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,Authorization,token,Origin,Content-Type,Accept,X-LINE-ChannelId,X-LINE-Authorization-Nonce,X-LINE-Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String header = request.getHeader("Authorization");
        log.info("header:{}", header);
        if (StringUtils.isEmpty(header) || !StringUtils.startsWithIgnoreCase(header, "Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = header.substring(7);
//        jwt+salt
        Map<String, Claim> tokenInfo = JwtUtil.getTokenInfo(jwt, "gethomeinasecond");
        String phone = tokenInfo.get("phone").asString();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!StringUtils.isEmpty(phone) && auth == null) {
            UserDetails userDetails = securityUserDetailsServiceImpl.loadUserByUsername(phone);
            if (phone.equals(userDetails.getUsername())) {
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(authToken);
                SecurityContextHolder.setContext(securityContext);
            }
            filterChain.doFilter(request, response);
        }
    }
}
