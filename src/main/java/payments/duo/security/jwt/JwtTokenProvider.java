package payments.duo.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import payments.duo.exception.JwtTokenException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static payments.duo.utils.Constants.TOKEN_DECLARATION_IS_WRONG;
import static payments.duo.utils.Constants.TOKEN_NOT_FOUND;
import static payments.duo.utils.Constants.TOKEN_PREFIX;

@Component
public class JwtTokenProvider {
    @Value("${jwt.token.secret}")
    private String secret;
    @Value("${jwt.token.expired}")
    private long expireTime;

    public String createToken(Authentication authenticate) {
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        return JWT.create()
                .withSubject(authenticate.getName())
                .withExpiresAt(new Date(System.currentTimeMillis() + expireTime))
                .withClaim("roles", getRoleNamesFromAuthorities(authenticate))
                .sign(algorithm);
    }

    public DecodedJWT resolveToken(String authHeader) {
        authHeaderCheck(authHeader);
        String token = authHeader.replace(TOKEN_PREFIX, "");
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    public List<String> getRoleNamesFromAuthorities(Authentication authenticate) {
        return authenticate.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

    public List<SimpleGrantedAuthority> getAuthoritiesFromToken(DecodedJWT decodedJWT) {
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
        return Arrays.stream(roles)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private void authHeaderCheck(String authHeader) {
        if (Objects.isNull(authHeader)) {
            throw new JwtTokenException(TOKEN_NOT_FOUND);
        } else if (!authHeader.startsWith(TOKEN_PREFIX)) {
            throw new JwtTokenException(TOKEN_DECLARATION_IS_WRONG);
        }
    }
}
