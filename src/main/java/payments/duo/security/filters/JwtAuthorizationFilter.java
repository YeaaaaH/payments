package payments.duo.security.filters;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import payments.duo.exception.JwtTokenException;
import payments.duo.security.jwt.JwtTokenProvider;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static payments.duo.exception.CustomizedEntityExceptionHandler.prepareExceptions;
import static payments.duo.security.SecurityConfig.SIGNIN_ENDPOINT;
import static payments.duo.security.SecurityConfig.SIGNUP_ENDPOINT;
import static payments.duo.utils.Constants.TOKEN_DECLARATION_IS_WRONG;
import static payments.duo.utils.Constants.TOKEN_IS_EXPIRED;
import static payments.duo.utils.ResponseBuilder.buildResponse;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // TODO implement proper request filtering for swagger
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (SIGNUP_ENDPOINT.equals(request.getServletPath()) ||
                SIGNIN_ENDPOINT.equals(request.getServletPath()) ||
                request.getServletPath().contains("swagger") ||
                request.getServletPath().contains("v2/api-docs")
        ) {
            filterChain.doFilter(request, response);
        } else {
            try {
                String authHeader = request.getHeader(AUTHORIZATION);
                DecodedJWT decodedJWT = jwtTokenProvider.resolveToken(authHeader);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        decodedJWT.getSubject(),
                        null,
                        jwtTokenProvider.getAuthoritiesFromToken(decodedJWT));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                filterChain.doFilter(request, response);
            } catch (SignatureVerificationException exception) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                buildResponse(response, prepareExceptions(new JwtTokenException(TOKEN_DECLARATION_IS_WRONG)));
            } catch (TokenExpiredException expiredException) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                buildResponse(response, prepareExceptions(new TokenExpiredException(TOKEN_IS_EXPIRED)));
            } catch (Exception exception) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                buildResponse(response, prepareExceptions(exception));
            }
        }
    }
}
