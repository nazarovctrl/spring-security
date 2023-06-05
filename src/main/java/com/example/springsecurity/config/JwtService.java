package com.example.springsecurity.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.springsecurity.exp.JWTTokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
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

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, ACCESS_TOKEN_LIVE, SignatureAlgorithm.HS256, getAccessTokenSignInKey());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, REFRESH_TOKEN_LIVE, SignatureAlgorithm.HS256, getRefreshTokenSignInKey());
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, int expirationTime, SignatureAlgorithm algorithm, Key key) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, algorithm)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        Date expiresAt = decodedJWT.getExpiresAt();

        if (expiresAt.before(new Date())) {
            throw new JWTTokenExpiredException("JWT expired at " + expiresAt + ". Current time: " + new Date());
        }
        return false;
//        return extractExpiration(token).before(new Date());
    }

//    private Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getAccessTokenSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getAccessTokenSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(ACCESS_TOKEN_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Key getRefreshTokenSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(REFRESH_TOKEN_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
