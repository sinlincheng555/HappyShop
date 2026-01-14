package ci553.happyshop.auth;

import ci553.happyshop.storageAccess.UserDatabaseRW;
import java.sql.SQLException;

/**
 * AuthenticationManager - Central authentication system
 * Singleton pattern for managing user authentication
 *
 * @author HappyShop Development Team
 * @version 2.0
 */
public class AuthenticationManager {

    private static AuthenticationManager instance;
    private UserDatabaseRW userDatabase;
    private User currentUser;

    private AuthenticationManager() {
        this.userDatabase = new UserDatabaseRW();
    }

    public static AuthenticationManager getInstance() {
        if (instance == null) {
            instance = new AuthenticationManager();
        }
        return instance;
    }

    /**
     * Authenticate a user with username and password
     */
    public User login(String username, String password) {
        try {
            User user = userDatabase.authenticateUser(username, password);
            if (user != null) {
                this.currentUser = user;
                System.out.println("✅ Login successful: " + username);
            } else {
                System.out.println("❌ Login failed: " + username);
            }
            return user;
        } catch (SQLException e) {
            System.err.println("❌ Login error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Register a new customer
     */
    public RegistrationResult registerCustomer(String username, String password, String email, String fullName) {
        try {
            // Validate input
            if (username == null || username.trim().isEmpty()) {
                return new RegistrationResult(false, "Username cannot be empty");
            }
            if (username.length() < 3) {
                return new RegistrationResult(false, "Username must be at least 3 characters");
            }
            if (password == null || password.length() < 6) {
                return new RegistrationResult(false, "Password must be at least 6 characters");
            }
            if (email == null || !email.contains("@")) {
                return new RegistrationResult(false, "Invalid email address");
            }

            // Check if username exists
            if (userDatabase.usernameExists(username)) {
                return new RegistrationResult(false, "Username already exists");
            }

            // Register user
            boolean success = userDatabase.registerUser(username, password, email, fullName, UserRole.CUSTOMER);

            if (success) {
                return new RegistrationResult(true, "Account created successfully! Please login.");
            } else {
                return new RegistrationResult(false, "Registration failed. Please try again.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Registration error: " + e.getMessage());
            return new RegistrationResult(false, "Database error: " + e.getMessage());
        }
    }

    /**
     * Get currently logged in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Logout current user
     */
    public void logout() {
        if (currentUser != null) {
            System.out.println("👋 Logout: " + currentUser.getUsername());
            currentUser = null;
        }
    }

    /**
     * Check if a user is currently logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Change password for current user
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser == null) {
            return false;
        }

        try {
            // Verify old password
            if (!PasswordHasher.verifyPassword(oldPassword, currentUser.getPasswordHash())) {
                System.out.println("❌ Old password incorrect");
                return false;
            }

            // Update password
            boolean success = userDatabase.updatePassword(currentUser.getUsername(), newPassword);

            if (success) {
                // Update current user's password hash
                currentUser.setPasswordHash(PasswordHasher.hashPassword(newPassword));
                System.out.println("✅ Password changed successfully");
            }

            return success;

        } catch (SQLException e) {
            System.err.println("❌ Password change error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Registration result inner class
     */
    public static class RegistrationResult {
        private final boolean success;
        private final String message;

        public RegistrationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}