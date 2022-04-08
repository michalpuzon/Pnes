package org.ekipa.pnes.api.configs.security.fillters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.ekipa.pnes.api.models.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JWTUtils {
    private static final String SECRET = "alpejskiemleczkoosmakuwaniliowym";

    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public static Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    public static boolean isExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
    }

    public static String generateToken(User user) {
        return createToken(new HashMap<>(), user.getUsername());
    }

    public static boolean validateToken(String token, User user) {
        return extractUsername(token).equals(user.getUsername()) && !isExpired(token);
    }

    private static String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }
}
