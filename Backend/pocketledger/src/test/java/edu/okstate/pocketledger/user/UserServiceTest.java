package edu.okstate.pocketledger.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// tests register and login rules with a fake repository (no database needed)
class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class); // test double
        userService = new UserService(userRepository, encoder);
    }

    @Test
    void registeringHashesThePassword() {
        when(userRepository.findByEmail("a@okstate.edu")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(call -> call.getArgument(0));

        User saved = userService.createUser(new CreateUserRequest("a@okstate.edu", "testpassword"));

        assertThat(saved.getPasswordHash()).isNotEqualTo("testpassword");
        assertThat(encoder.matches("testpassword", saved.getPasswordHash())).isTrue();
    }

    @Test
    void duplicateEmailsRejected() {
        when(userRepository.findByEmail("a@okstate.edu")).thenReturn(Optional.of(new User("a@okstate.edu", "hash")));

        assertThatThrownBy(() -> userService.createUser(new CreateUserRequest("a@okstate.edu", "testpassword")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void loginSucceedsWithTheCorrectPassword() {
        User user = new User("a@okstate.edu", encoder.encode("testpassword"));

        when(userRepository.findByEmail("a@okstate.edu")).thenReturn(Optional.of(user));

        User result = userService.login(new LoginRequest("a@okstate.edu", "testpassword"));

        assertThat(result.getEmail()).isEqualTo("a@okstate.edu");
    }

    @Test
    void loginFailsWithTheWrongPassword() {
        User user = new User("a@okstate.edu", encoder.encode("testpassword"));

        when(userRepository.findByEmail("a@okstate.edu")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.login(new LoginRequest("a@okstate.edu", "wrongpassword")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void loginFailsWhenTheEmailIsNotRegistered() {
        when(userRepository.findByEmail("nobody@okstate.edu")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(new LoginRequest("nobody@okstate.edu", "testpassword")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
