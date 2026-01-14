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
                        System.out.println("âš ï¸  Account inactive: " + username);
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
            System.out.println("âœ… Auth successful: " + username);
            return user;
        }

        System.out.println("Auth failed: " + username);
        return null;
    }

    public boolean registerUser(String username, String password, String email, String fullName, UserRole role) throws SQLException {
        if (usernameExists(username)) {
            System.out.println("âŒ Username exists: " + username);
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
            System.out.println(rows > 0 ? "âœ… Registered: " + username : "âŒ Registration failed");
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
            System.err.println("âš ï¸  Last login update failed: " + username);
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

    /**
     * Initialize UserTable and create default accounts
     * Called during database setup
     */
    public static void initializeUserTable() throws SQLException {
        lock.lock();

        String createTableSQL = "CREATE TABLE UserTable(" +
                "userId INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                "username VARCHAR(50) UNIQUE NOT NULL," +
                "passwordHash VARCHAR(255) NOT NULL," +
                "email VARCHAR(100)," +
                "fullName VARCHAR(100)," +
                "role VARCHAR(20) NOT NULL," +
                "isActive BOOLEAN DEFAULT true," +
                "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "lastLogin TIMESTAMP" +
                ")";

        try (Connection conn = DriverManager.getConnection(dbURL)) {
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement()) {
                // Create table
                stmt.executeUpdate(createTableSQL);
                System.out.println("  ✓ UserTable created");

                // Prepare insert statement
                String insertSQL = "INSERT INTO UserTable (username, passwordHash, email, fullName, role, isActive) " +
                        "VALUES (?, ?, ?, ?, ?, true)";

                try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                    // 1. Create default admin account
                    pstmt.setString(1, "admin");
                    pstmt.setString(2, PasswordHasher.hashPassword("admin123"));
                    pstmt.setString(3, "admin@happyshop.com");
                    pstmt.setString(4, "System Administrator");
                    pstmt.setString(5, "ADMIN");
                    pstmt.executeUpdate();
                    System.out.println("  ✓ Admin account created (username: admin, password: admin123)");

                    // 2. Create warehouse staff account
                    pstmt.setString(1, "staff");
                    pstmt.setString(2, PasswordHasher.hashPassword("staff123"));
                    pstmt.setString(3, "staff@happyshop.com");
                    pstmt.setString(4, "Warehouse Staff");
                    pstmt.setString(5, "STAFF");
                    pstmt.executeUpdate();
                    System.out.println("  ✓ Staff account created (username: staff, password: staff123)");

                    // 3. Create another warehouse staff account
                    pstmt.setString(1, "warehouse1");
                    pstmt.setString(2, PasswordHasher.hashPassword("warehouse123"));
                    pstmt.setString(3, "warehouse1@happyshop.com");
                    pstmt.setString(4, "John Warehouse");
                    pstmt.setString(5, "STAFF");
                    pstmt.executeUpdate();
                    System.out.println("  ✓ Warehouse staff account created (username: warehouse1, password: warehouse123)");

                    // 4. Create picker account
                    pstmt.setString(1, "picker");
                    pstmt.setString(2, PasswordHasher.hashPassword("picker123"));
                    pstmt.setString(3, "picker@happyshop.com");
                    pstmt.setString(4, "Order Picker");
                    pstmt.setString(5, "PICKER");
                    pstmt.executeUpdate();
                    System.out.println("  ✓ Picker account created (username: picker, password: picker123)");

                    // 5. Create customer account for testing
                    pstmt.setString(1, "customer");
                    pstmt.setString(2, PasswordHasher.hashPassword("customer123"));
                    pstmt.setString(3, "customer@example.com");
                    pstmt.setString(4, "Test Customer");
                    pstmt.setString(5, "CUSTOMER");
                    pstmt.executeUpdate();
                    System.out.println("  ✓ Customer account created (username: customer, password: customer123)");
                }

                conn.commit();
                System.out.println("  ✓ All default accounts created successfully");

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("  ✗ Failed to create default accounts - rolled back");
                throw e;
            }

        } finally {
            lock.unlock();
        }
    }
}