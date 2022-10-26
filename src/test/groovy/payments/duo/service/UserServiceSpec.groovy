package payments.duo.service

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import payments.duo.model.auth.Role
import payments.duo.model.auth.User
import payments.duo.model.request.auth.CreateUserCommand
import payments.duo.repository.RoleRepository
import payments.duo.repository.UserRepository
import spock.lang.Specification

class UserServiceSpec extends Specification {

    UserRepository userRepository
    RoleRepository roleRepository
    BCryptPasswordEncoder passwordEncoder
    UserService service

    def setup() {
        userRepository = Mock(UserRepository)
        roleRepository = Mock(RoleRepository)
        passwordEncoder = Mock(BCryptPasswordEncoder)
        service = new UserService(userRepository, roleRepository, passwordEncoder)
    }

    def "should save valid user"() {
        given:
            CreateUserCommand command = createValidUserCommand()
        when:
            service.registration(command)
        then:
            1 * passwordEncoder.encode(_)
            1 * userRepository.save(_)
            1 * roleRepository.findByName(_) >> new Role(name: "role")
    }

    def "should load User Details by username"() {
        given:
            User user = createValidUser()
            Role role = new Role(id: 1, name: 'ROLE_CLIENT')
            user.roles = [role]
            Optional<User> optUser = Optional.of(user)
        and:
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority('ROLE_CLIENT'))
        when:
            UserDetails response = service.loadUserByUsername("username")
        then:
            1 * userRepository.findUserByUsername("username") >> optUser
            response.password == 'password'
            response.username == 'username'
            response.authorities == authorities
    }

    def "should find user by name"() {
        given:
            User user = createValidUser()
            Optional<User> optUser = Optional.of(user)
        when:
            service.findUserByUsername("username")
        then:
            1 * userRepository.findUserByUsername("username") >> optUser
    }

    def "should find user by id"() {
        given:
            User user = createValidUser()
            Optional<User> optUser = Optional.of(user)
        when:
            service.findUserById(1)
        then:
            1 * userRepository.findById(1) >> optUser
    }

    def "should check is user exists by username"() {
        given:
            userRepository.existsByEmail("username") >> a
        when:
            def response = service.isUserExistsByEmail("username")
        then:
            response == a
        where:
            a     | _
            true  | _
            false | _
    }

    def "should check is user exists by email"() {
        given:
            userRepository.existsByEmail("email@e.com") >> a
        when:
            def response = service.isUserExistsByEmail("email@e.com")
        then:
            response == a
        where:
            a     | _
            true  | _
            false | _
    }

    private CreateUserCommand createValidUserCommand() {
        CreateUserCommand command = new CreateUserCommand()
        command.username = 'username'
        command.password = 'password'
        command.email = 'email@email.com'
        command.firstName = 'some name'
        return command
    }

    private User createValidUser() {
        User user = new User()
        user.username = 'username'
        user.email = 'email@email.com'
        user.password = 'password'
        user.firstName = 'some name'
        return user
    }
}