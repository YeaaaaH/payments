package payments.duo.security.filters;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import payments.duo.security.jwt.JwtTokenProvider;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    JwtTokenProvider tokenProvider;
    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION);
        if (Objects.nonNull(authHeader) && authHeader.startsWith("Bearer ")) {
            DecodedJWT decodedJWT = tokenProvider.resolveToken(authHeader);
            String username = decodedJWT.getSubject();
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    username, null, tokenProvider.getAuthoritiesFromToken(decodedJWT));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }
}
