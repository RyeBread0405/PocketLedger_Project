package edu.okstate.pocketledger;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.okstate.pocketledger.config.SecurityConfig;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityConfigTest {

    @Test
    public void testPasswordEncoder() {

        SecurityConfig config = new SecurityConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        String password = "this_is_a_test";

        String hashedPassword = encoder.encode(password);

        System.out.println("Original password: " + password);
        System.out.println("Hashed password: " + hashedPassword);

        assertNotEquals(password, hashedPassword);

        assertTrue(encoder.matches(password, hashedPassword));
    }
}