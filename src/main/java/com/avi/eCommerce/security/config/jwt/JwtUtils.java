package com.avi.eCommerce.security.config.jwt;

import com.avi.eCommerce.service.cart.UserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;
    @Value("${auth.token.jwtExpirationTime}")
    private int jwtExpirationTime;

    public JwtUtils() {
        this.jwtSecret = "dGhpc19pc19hX3Zlcnlfc2VjdXJlX2tleV90aGF0X2lzXzY0X2J5dGVzX2xvbmcHello3676397924436763979244367639792443676397924436763979244=";
        this.jwtExpirationTime = 3600000;
    }

    public String generateTokenForUser(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                       .claim("id", userPrincipal.getId())
                       .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + this.jwtExpirationTime))
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    @PostConstruct
    private Key key(){
        System.out.println("======>>>jwtSecret: "+this.jwtSecret);
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.jwtSecret));
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder()
                   .setSigningKey(key())
                   .build()
                   .parseClaimsJws(token);
            return true;
        }catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e){
            throw new JwtException(e.getMessage());
        }




    }

}
