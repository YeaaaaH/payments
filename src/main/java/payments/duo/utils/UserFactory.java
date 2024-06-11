package payments.duo.utils;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import payments.duo.model.auth.Role;
import payments.duo.model.auth.User;
import payments.duo.model.response.UserDTO;
import payments.duo.security.jwt.JwtUser;

import java.util.List;

public final class UserFactory {

    public static JwtUser toJwtUser(User user) {
        return new JwtUser(
                user.getUsername(),
                user.getPassword(),
                getAuthoritiesFromRoles(user.getRoles()),
                user.getId());
    }

    public static List<SimpleGrantedAuthority> getAuthoritiesFromRoles(List<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();
    }

    public static UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .createdOn(user.getCreatedOn())
                .updatedOn(user.getUpdatedOn())
                .build();
    }
}
