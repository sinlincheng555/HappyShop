package ci553.happyshop.storageAccess;

import ci553.happyshop.auth.User;
import ci553.happyshop.auth.UserRole;
import ci553.happyshop.auth.PasswordHasher;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * UserDatabaseRW - Complete user database management
 * Handles authentication, registration, and user CRUD operations
 *
 * @author HappyShop Development Team
 * @version 2.0
 */
public class UserDatabaseRW {

    private static final String dbURL = DatabaseRWFactory.dbURL;
    private static final Lock lock = new ReentrantLock();

    public User findUserByUsername(String username) throws SQLException {
        String query = "SELECT * FROM UserTable WHERE username = ?";

        lock.lock();
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String passwordHash = rs.getString("passwordHash");
                    String email = rs.getString("email");
                    String fullName = rs.getString("fullName");
                    String roleStr = rs.getString("role");
                    boolean isActive = rs.getBoolean("isActive");

                    if (!isActive) {
                        System.out.println("⚠️  Account inactive: " + username);
                        return null;
                    }

                    UserRole role = UserRole.valueOf(roleStr.toUpperCase());
                    return new User(username, passwordHash, email, fullName, role);
                }
            }
        } finally {
            lock.unlock();
        }
        return null;
    }

    public User authenticateUser(String username, String password) throws SQLException {
        User user = findUserByUsername(username);

        if (user != null && PasswordHasher.verifyPassword(password, user.getPasswordHash())) {
            updateLastLogin(username);
            System.out.println("✅ Auth successful: " + username);
            return user;
        }

        System.out.println("❌ Auth failed: " + username);
        return null;
    }

    public boolean registerUser(String username, String password, String email, String fullName, UserRole role) throws SQLException {
        if (usernameExists(username)) {
            System.out.println("❌ Username exists: " + username);
            return false;
        }

        String passwordHash = PasswordHasher.hashPassword(password);
        String insertSQL = "INSERT INTO UserTable (username, passwordHash, email, fullName, role, isActive) VALUES (?, ?, ?, ?, ?, ?)";

        lock.lock();
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement stmt = conn.prepareStatement(insertSQL)) {

            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            stmt.setString(3, email);
            stmt.setString(4, fullName);
            stmt.setString(5, role.name());
            stmt.setBoolean(6, true);

            int rows = stmt.executeUpdate();
            System.out.println(rows > 0 ? "✅ Registered: " + username : "❌ Registration failed");
            return rows > 0;
        } finally {
            lock.unlock();
        }
    }

    public boolean usernameExists(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM UserTable WHERE username = ?";

        lock.lock();
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } finally {
            lock.unlock();
        }
    }

    private void updateLastLogin(String username) {
        try {
            lock.lock();
            String sql = "UPDATE UserTable SET lastLogin = CURRENT_TIMESTAMP WHERE username = ?";
            try (Connection conn = DriverManager.getConnection(dbURL);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("⚠️  Last login update failed: " + username);
        } finally {
            lock.unlock();
        }
    }

    public boolean updatePassword(String username, String newPassword) throws SQLException {
        String hash = PasswordHasher.hashPassword(newPassword);
        String sql = "UPDATE UserTable SET passwordHash = ? WHERE username = ?";

        lock.lock();
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hash);
            stmt.setString(2, username);
            return stmt.executeUpdate() > 0;
        } finally {
            lock.unlock();
        }
    }
}