package com.project.playvoice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = tokenProvider.resolveToken(request.getHeader("Authorization"));
        try {
            if (token != null && tokenProvider.validateToken(token)) {
                String isLogout = redisTemplate.opsForValue().get(token);
                if (ObjectUtils.isEmpty(isLogout)) {
                    Authentication authentication = tokenProvider.getAuthenticationByAccessToken(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);   // 정상 토큰일 경우 SecurityContext에 저장
                }
            }
        } catch (RedisConnectionFailureException e) {
            SecurityContextHolder.clearContext();
            throw new RuntimeException("redis connect fail");
        } catch (Exception e) {
            throw new RuntimeException("invalid token");
        }

        filterChain.doFilter(request, response);
    }
}
