package payments.duo.security;

import java.security.Principal;

/**
 * Principal of a request authenticated by JWT, built from the token claims (no DB lookup).
 * Inject into controllers with {@code @AuthenticationPrincipal AuthenticatedUser user}.
 */
public record AuthenticatedUser(Long id, String username) implements Principal {

    @Override
    public String getName() {
        return username;
    }
}
