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
import static payments.duo.exception.CustomizedEntityExceptionHandler.prepareExceptions;
import static payments.duo.security.SecurityConfig.SIGNUP_ENDPOINT;
import static payments.duo.utils.ResponseBuilder.buildResponse;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    UserDetailsService userDetailsService;

    // TODO implement proper request filtering for swagger
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (Objects.equals(request.getServletPath(), SIGNUP_ENDPOINT) ||
                request.getServletPath().contains("swagger") ||
                request.getServletPath().contains("v2/api-docs")) {
            filterChain.doFilter(request, response);
        }
        else {
            try {
                String authHeader = request.getHeader(AUTHORIZATION);
                DecodedJWT decodedJWT = jwtTokenProvider.resolveToken(authHeader);
                String username = decodedJWT.getSubject();
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, jwtTokenProvider.getAuthoritiesFromToken(decodedJWT));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                filterChain.doFilter(request, response);
            } catch (Exception exception) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                buildResponse(response, prepareExceptions(exception));
            }
        }
    }
}
