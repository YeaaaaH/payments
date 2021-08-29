package payments.duo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import payments.duo.exception.UserNotFoundException;
import payments.duo.model.auth.Role;
import payments.duo.model.auth.User;
import payments.duo.model.request.auth.CreateUserCommand;
import payments.duo.repository.RoleRepository;
import payments.duo.repository.UserRepository;
import payments.duo.utils.UserFactory;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with name: " + username + " hasn't been found"));
        return UserFactory.toJwtUser(user);
    }

    public User registration(CreateUserCommand createUserCommand) {
        User user = new User();
        user.setUsername(createUserCommand.getUsername());
        user.setPassword(passwordEncoder.encode(createUserCommand.getPassword()));
        user.setEmail(createUserCommand.getEmail());
        user.setFirstName(createUserCommand.getFirstName());
        user.setLastName(createUserCommand.getLastName());
        Role role = roleRepository.findByName("ROLE_CLIENT");
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);
        user.setCreatedOn(new Date(System.currentTimeMillis()));
        return userRepository.save(user);
    }

    public User findUserByUsername(String userName) {
        return userRepository.findUserByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException("User with username: " + userName + " hadn't been found"));
    }
}