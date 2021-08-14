package payments.duo.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import payments.duo.model.auth.Role;
import payments.duo.model.auth.User;
import payments.duo.model.request.auth.UserCommand;
import payments.duo.security.jwt.JwtUser;

import java.util.List;
import java.util.stream.Collectors;

public final class UserFactory {
    public UserFactory() {
    }

    public static JwtUser toJwtUser(User user) {
        return new JwtUser(
                user.getUsername(),
                user.getPassword(),
                getAuthoritiesFromRoles(user.getRoles())
        );
    }

    public static List<GrantedAuthority> getAuthoritiesFromRoles(List<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    public static UserCommand toUserCommand(User user) {
        return new UserCommand(
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getCreatedOn(),
                user.getUpdatedOn()
        );
    }
}
