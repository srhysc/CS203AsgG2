package com.cs203.grp2.Asg2.models;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDefaultConstructor() {
        // Act
        User user = new User();

        // Assert
        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getRole());
    }

    @Test
    void testParameterizedConstructor() {
        // Act
        User user = new User("user123", "john@example.com", "john_doe", User.Role.USER);

        // Assert
        assertEquals("user123", user.getId());
        assertEquals("john_doe", user.getUsername());
        assertEquals("john@example.com", user.getEmail());
        assertEquals(User.Role.USER, user.getRole());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        User user = new User();

        // Act
        user.setId("user456");
        user.setUsername("jane_doe");
        user.setEmail("jane@example.com");
        user.setRole(User.Role.ADMIN);

        // Assert
        assertEquals("user456", user.getId());
        assertEquals("jane_doe", user.getUsername());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals(User.Role.ADMIN, user.getRole());
    }

    @Test
    void testValidUser() {
        // Arrange
        User user = new User("user123", "john@example.com", "john_doe", User.Role.USER);

        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankId() {
        // Arrange
        User user = new User("", "john@example.com", "john_doe", User.Role.USER);

        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Assert
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("UserID is required", violation.getMessage());
    }

    @Test
    void testBlankUsername() {
        // Arrange
        User user = new User("user123", "john@example.com", "", User.Role.USER);

        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Assert
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Username is required", violation.getMessage());
    }

    @Test
    void testBlankEmail() {
        // Arrange
        User user = new User("user123", "", "john_doe", User.Role.USER);

        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Assert
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Email is required", violation.getMessage());
    }

    @Test
    void testMultipleBlankFields() {
        // Arrange
        User user = new User("", "", "", User.Role.USER);

        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Assert
        assertEquals(3, violations.size());
    }

    @Test
    void testUserRoleEnum() {
        // Arrange & Act
        User user = new User("user123", "john@example.com", "john_doe", User.Role.USER);

        // Assert
        assertEquals(User.Role.USER, user.getRole());
    }

    @Test
    void testAdminRoleEnum() {
        // Arrange & Act
        User user = new User("admin123", "admin@example.com", "admin_user", User.Role.ADMIN);

        // Assert
        assertEquals(User.Role.ADMIN, user.getRole());
    }

    @Test
    void testRoleEnumValues() {
        // Act
        User.Role[] roles = User.Role.values();

        // Assert
        assertEquals(2, roles.length);
        assertEquals(User.Role.USER, roles[0]);
        assertEquals(User.Role.ADMIN, roles[1]);
    }

    @Test
    void testRoleEnumValueOf() {
        // Act & Assert
        assertEquals(User.Role.USER, User.Role.valueOf("USER"));
        assertEquals(User.Role.ADMIN, User.Role.valueOf("ADMIN"));
    }

    @Test
    void testWhitespaceOnlyFields() {
        // Arrange
        User user = new User("   ", "   ", "   ", User.Role.USER);

        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Assert
        assertEquals(3, violations.size());
    }

    @Test
    void testNullRole() {
        // Arrange
        User user = new User("user123", "john@example.com", "john_doe", null);

        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Assert
        // Role can be null - no validation constraint on it
        assertTrue(violations.isEmpty());
        assertNull(user.getRole());
    }

    @Test
    void testEmailWithSpecialCharacters() {
        // Arrange
        User user = new User("user123", "john.doe+test@example.co.uk", "john.doe+test", User.Role.USER);

        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void testLongUsername() {
        // Arrange
        String longUsername = "this_is_a_very_long_username_that_should_still_be_valid";
        User user = new User("user123", "user@example.com", longUsername, User.Role.USER);

        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Assert
        assertTrue(violations.isEmpty());
        assertEquals(longUsername, user.getUsername());
    }

    @Test
    void testRoleChangeFromUserToAdmin() {
        // Arrange
        User user = new User("user123", "john@example.com", "john_doe", User.Role.USER);

        // Act
        user.setRole(User.Role.ADMIN);

        // Assert
        assertEquals(User.Role.ADMIN, user.getRole());
    }
}
