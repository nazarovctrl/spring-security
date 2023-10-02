package com.example.springsecurity.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String ACCESS_TOKEN_SECRET_KEY = "4125442A472D4B6150645367566B59703373357638792F423F4528482B4D6251";
    private static final String REFRESH_TOKEN_SECRET_KEY = "432A462D4A614E645267556B586E3272357538782F413F4428472B4B62506553";

    private static final int ACCESS_TOKEN_LIVE = 1000 * 60; // 20 minut
    private static final int REFRESH_TOKEN_LIVE = 1000 * 60 * 40; // 40 minut

    public String extractAccessTokenUsername(String accessToken) {
        return extractClaim(accessToken, Claims::getSubject, getAccessTokenSignInKey());
    }

    public String extractRefreshTokenUsername(String refreshToken) {
        return extractClaim(refreshToken, Claims::getSubject, getRefreshTokenSignInKey());
    }

    public String generateAccessToken(String username) {
        return generateToken(new HashMap<>(), username, ACCESS_TOKEN_LIVE, getAccessTokenSignInKey());
    }

    public String generateRefreshToken(String username) {
        return generateToken(new HashMap<>(), username, REFRESH_TOKEN_LIVE, getRefreshTokenSignInKey());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver, Key key) {
        final Claims claims = extractAllClaims(token, key);
        return claimsResolver.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, String username, int expirationTime, Key key) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenExpired(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        Date expiresAt = decodedJWT.getExpiresAt();
        return expiresAt.before(new Date());
    }

    public String getTokenExpiredMessage(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        Date expiresAt = decodedJWT.getExpiresAt();
        return "JWT expired at" + expiresAt + ". Current time" + new Date();
    }

    private Claims extractAllClaims(String token, Key key) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getAccessTokenSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(ACCESS_TOKEN_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getRefreshTokenSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(REFRESH_TOKEN_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
