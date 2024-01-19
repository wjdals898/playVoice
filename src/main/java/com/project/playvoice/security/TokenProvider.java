package com.project.playvoice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class TokenProvider {

    @Value("${jwt.secret}")
    private String secret_key;

    @Value("${jwt.access-validity-time}")
    private long access_valid_time; // 1시간

    @Value("${jwt.refresh-validity-time}")
    private long refresh_valid_time; // 30분

    private final UserDetailsService userDetailsService;
    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    protected void init() {
        secret_key = Base64.getEncoder().encodeToString(secret_key.getBytes());
    }

    public String createAccessToken(Authentication authentication) {
        Claims claims = Jwts.claims().setSubject(authentication.getName());

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + access_valid_time))
                .signWith(SignatureAlgorithm.HS512, secret_key)
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        Claims claims = Jwts.claims().setSubject(authentication.getName());

        Date now = new Date();
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refresh_valid_time))
                .signWith(SignatureAlgorithm.HS512, secret_key)
                .compact();

        redisTemplate.opsForValue().set(
                authentication.getName(),
                refreshToken,
                refresh_valid_time,
                TimeUnit.MILLISECONDS
        );

        return refreshToken;
    }

    public Authentication getAuthenticationByAccessToken(String token) {
        String userPrincipal = Jwts.parserBuilder()
                .setSigningKey(secret_key)
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPrincipal);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Authentication getAuthenticationByUsername(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(secret_key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(String bearerToken) {
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try{
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secret_key).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        }
        catch(SignatureException e){
            //서명이 옳지 않을 때
            System.out.println("잘못된 토큰 서명입니다.");
            throw new RuntimeException("invalid token signature");
        }
        catch(ExpiredJwtException e){
            //토큰이 만료됐을 때
            System.out.println("만료된 토큰입니다.");
            throw new RuntimeException("expired token");
        }
        catch(IllegalArgumentException | MalformedJwtException e){
            //토큰이 올바르게 구성되지 않았을 때 처리
            System.out.println("invalid token");
        }
        return false;
    }

    public Long getExpired(String token) {
        Date expiration = Jwts.parserBuilder().setSigningKey(secret_key).build().parseClaimsJws(token)
                .getBody().getExpiration();
        long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    // 토큰 헤더 설정
    public HttpHeaders setHeaderToken(String accessToken, String refreshToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + accessToken);
        httpHeaders.add("Refresh", "Bearer " + refreshToken);

        return httpHeaders;
    }
}
