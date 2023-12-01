package payments.duo.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import payments.duo.exception.UserNotFoundException;
import payments.duo.model.auth.Role;
import payments.duo.model.auth.User;
import payments.duo.model.request.auth.CreateUserCommand;
import payments.duo.model.request.auth.UserCommand;
import payments.duo.repository.RoleRepository;
import payments.duo.repository.UserRepository;
import payments.duo.service.UserService;
import payments.duo.utils.UserFactory;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@Primary
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
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
        user.setRoles(List.of(role));
        user.setCreatedOn(LocalDate.now());
        return userRepository.save(user);
    }
//TODO refactor update approach (userCommand)
    public User updateUser(UserCommand userCommand, Long id) {
        User user = findUserById(id);
        if (userCommand.getUsername() != null) {
            user.setUsername(userCommand.getUsername());
        }
        if (userCommand.getEmail() != null) {
            user.setEmail(userCommand.getEmail());
        }
        if (userCommand.getFirstName() != null) {
            user.setFirstName(userCommand.getFirstName());
        }
        if (userCommand.getLastName() != null) {
            user.setLastName(userCommand.getLastName());
        }
        user.setUpdatedOn(LocalDate.now());
        user = userRepository.save(user);
        return user;
    }

    public UserCommand getUserFromAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = findUserByUsername(authentication.getName());
        return UserFactory.toUserCommand(user);
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username: " + username + " hadn't been found"));
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " hadn't been found"));
    }

    public boolean isUserExistsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isUserExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}