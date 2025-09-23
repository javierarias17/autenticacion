package co.com.pragma.api.security;

import co.com.pragma.api.dto.JwtSecretDTO;
import co.com.pragma.model.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Stream;

@Component
public class JwtProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtProvider.class);

    private final String expiration;
    private final String secret;

    public JwtProvider(JwtSecretDTO jwtSecretDTO) {
        this.expiration = jwtSecretDTO.expiration();
        this.secret = jwtSecretDTO.secret();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String roles) {
        return Stream.of(roles.split(", ")).map(SimpleGrantedAuthority::new)
                .toList();
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getFirstName() + " " + user.getLastName())
                .claim("roles", getAuthorities(user.getRoleId().toString()))
                .claim("identityDocument", user.getIdentityDocument())
                .claim("email", user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + Long.parseLong(expiration)))
                .signWith(getKey(secret))
                .compact();
    }

    public Claims getPayload(String token) {
        return Jwts.parser()
                .verifyWith(getKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validate(String token){
        try {
            Jwts.parser()
                    .verifyWith(getKey(secret))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return true;
        } catch (ExpiredJwtException e) {
            LOGGER.error("token expired");
        } catch (UnsupportedJwtException e) {
            LOGGER.error("token unsupported");
        } catch (MalformedJwtException e) {
            LOGGER.error("token malformed");
        } catch (IllegalArgumentException e) {
            LOGGER.error("illegal args");
        }catch (Exception e){
            LOGGER.error("Invalid token");
        }
        return false;
    }

    private SecretKey getKey(String secret) {
        byte[] secretBytes = Decoders.BASE64URL.decode(secret);
        return Keys.hmacShaKeyFor(secretBytes);
    }
}

