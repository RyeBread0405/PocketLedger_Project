package edu.okstate.pocketledger.user;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateUserRequestTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    // Test 1: Valid password with letters and numbers
    @Test
    void acceptsValidPassword() {
        CreateUserRequest request =
            new CreateUserRequest("test@example.com", "Password123");

        assertTrue(validator.validate(request).isEmpty());
    }

    // Test 2: Password contains only letters
    @Test
    void rejectsPasswordWithoutNumbers() {
        CreateUserRequest request =
            new CreateUserRequest("test@example.com", "PasswordOnly");

        assertFalse(validator.validate(request).isEmpty());
    }

    // Test 3: Password contains only numbers
    @Test
    void rejectsPasswordWithoutLetters() {
        CreateUserRequest request =
            new CreateUserRequest("test@example.com", "123456789");

        assertFalse(validator.validate(request).isEmpty());
    }

    // Test 4: Password is too short
    @Test
    void rejectsShortPassword() {
        CreateUserRequest request =
            new CreateUserRequest("test@example.com", "Abc123");

        assertFalse(validator.validate(request).isEmpty());
    }

    // Test 5: Password contains special characters
    @Test
    void acceptsSpecialCharacters() {
        CreateUserRequest request =
            new CreateUserRequest("test@example.com", "Hello2026!");

        assertTrue(validator.validate(request).isEmpty());
    }

    // Test 6: Password is too long
    @Test
    void rejectsLongPassword() {
        CreateUserRequest request =
            new CreateUserRequest("test@example.com", "Password123".repeat(8));

        assertFalse(validator.validate(request).isEmpty());
    }

    // Test 7: Password is blank
    @Test
    void rejectsBlankPassword() {
        CreateUserRequest request =
            new CreateUserRequest("test@example.com", "");

        assertFalse(validator.validate(request).isEmpty());
    }
}