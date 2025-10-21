package prac.com_file.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import prac.com_file.model.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    //    This field is injected from the application properties using @Value.
//    It stores the secret key used for signing and verifying JWTs
    @Value("${secret.key}")
    private String SECRET_KEY;


    //     Checks if the token is valid by comparing the username from the token with the provided UserDetails.
//     It also checks if the token is expired
    public boolean isValid(String token, UserDetails user){
        String username = extractUsername(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token);
    }

    //    Determines if the token has expired by comparing its expiration date to the current date.
    private boolean isTokenExpired(String token) {
        return extractExpirationTime(token).before(new Date());
    }

    //    Extracts the expiration date from the token's claims.
    private Date extractExpirationTime(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //     Retrieves the username from the token's claims
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

//    A generic method for extracting a specific claim from the token by applying a provided function to the claims.

    public <T> T extractClaim(String token, Function<Claims, T> resolver){
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    //Creates a new JWT for the specified user:
//subject: Sets the subject (username) of the token.
//issuedAt: Specifies the token's issuance date.
//expiration: Sets the token's expiration time (24 hours from the current time).
//signWith: Signs the token with the secret key.
//compact: Builds the JWT into a compact string format
    public String generateToken(User user){
        Map<String, String> claims = new HashMap<>();
        claims.put("role", user.getRole().getName());
        String token = Jwts
                .builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 100))
                .signWith(getSigningKey())
                .compact();
        return token;
    }

// Decodes the base64-encoded secret key and generates a SecretKey instance for signing and verifying JWTs

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

//extractAllClaims: Parses the token and retrieves all claims:
//parser: Creates a parser for the JWT.
//verifyWith: Uses the signing key to verify the token's signature.
//parseSignedClaims: Parses the token into claims.
//getPayload: Retrieves the claims payload.

    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}