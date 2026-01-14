package ci553.happyshop.systemSetup;

import ci553.happyshop.storageAccess.DatabaseRWFactory;
import ci553.happyshop.storageAccess.UserDatabaseRW;
import ci553.happyshop.utility.StorageLocation;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Enhanced SetDatabase with authentication support.
 *
 * Initializes:
 * - ProductTable (with maxStock)
 * - UserTable (for authentication)
 * - Default admin account
 * - Sample product data
 *
 * WARNING: This resets the entire database to clean state.
 * All existing data will be lost.
 *
 * @author HappyShop Development Team
 * @version 2.0
 */
public class SetDatabaseWithAuth {

    private static final String dbURL = DatabaseRWFactory.dbURL + ";create=true";
    private static Path imageWorkingFolderPath = StorageLocation.imageFolderPath;
    private static Path imageBackupFolderPath = StorageLocation.imageResetFolderPath;

    private String[] tables = {"ProductTable", "UserTable"};
    private static final Lock lock = new ReentrantLock();

    public static void main(String[] args) throws SQLException, IOException {
        System.out.println("========================================");
        System.out.println("  Database Initialization (with Auth)");
        System.out.println("========================================");
        System.out.println();

        SetDatabaseWithAuth setDB = new SetDatabaseWithAuth();
        setDB.clearTables();
        setDB.initializeTables();
        setDB.queryTablesAfterInitialization();
        deleteFilesInFolder(imageWorkingFolderPath);
        copyFolderContents(imageBackupFolderPath, imageWorkingFolderPath);

        System.out.println();
        System.out.println("========================================");
        System.out.println("✅ Database initialized successfully!");
        System.out.println("========================================");
        System.out.println();
        System.out.println("📋 Summary:");
        System.out.println("  • ProductTable created with sample data");
        System.out.println("  • UserTable created");
        System.out.println("  • Default admin account created");
        System.out.println();
        System.out.println("👤 Default Admin Credentials:");
        System.out.println("  Username: admin");
        System.out.println("  Password: admin123");
        System.out.println();
        System.out.println("⚠️  IMPORTANT: Change the admin password after first login!");
        System.out.println("========================================");
    }

    private void clearTables() throws SQLException {
        lock.lock();
        try (Connection con = DriverManager.getConnection(dbURL);
             Statement statement = con.createStatement()) {

            System.out.println("🗑️  Clearing existing tables...");
            System.out.println();

            for (String table : tables) {
                try {
                    statement.executeUpdate("DROP TABLE " + table.toUpperCase());
                    System.out.println("  ✓ Dropped table: " + table);
                } catch (SQLException e) {
                    if ("42Y55".equals(e.getSQLState())) {
                        System.out.println("  ℹ️  Table " + table + " does not exist (skipping)");
                    } else {
                        throw e;
                    }
                }
            }

            System.out.println();

        } finally {
            lock.unlock();
        }
    }

    private void initializeTables() throws SQLException {
        System.out.println("📦 Creating tables...");
        System.out.println();

        // Initialize ProductTable
        initializeProductTable();

        // Initialize UserTable
        UserDatabaseRW.initializeUserTable();

        System.out.println();
    }

    private void initializeProductTable() throws SQLException {
        lock.lock();

        String[] iniTableSQL = {
                // Create ProductTable with maxStock column
                "CREATE TABLE ProductTable(" +
                        "productID CHAR(4) PRIMARY KEY," +
                        "description VARCHAR(100)," +
                        "unitPrice DOUBLE," +
                        "image VARCHAR(100)," +
                        "inStock INT," +
                        "maxStock INT," +
                        "CHECK (inStock >= 0)," +
                        "CHECK (maxStock >= 0)" +
                        ")",

                // Insert sample data
                "INSERT INTO ProductTable VALUES('0001', '40 inch TV', 269.00, '0001.jpg', 100, 100)",
                "INSERT INTO ProductTable VALUES('0002', 'DAB Radio', 29.99, '0002.jpg', 50, 100)",
                "INSERT INTO ProductTable VALUES('0003', 'Toaster', 19.99, '0003.jpg', 25, 100)",
                "INSERT INTO ProductTable VALUES('0004', 'Watch', 29.99, '0004.jpg', 8, 100)",
                "INSERT INTO ProductTable VALUES('0005', 'Digital Camera', 89.99, '0005.jpg', 5, 100)",
                "INSERT INTO ProductTable VALUES('0006', 'MP3 player', 7.99, '0006.jpg', 100, 100)",
                "INSERT INTO ProductTable VALUES('0007', 'USB drive', 6.99, '0007.jpg', 75, 100)",
                "INSERT INTO ProductTable VALUES('0008', 'USB2 drive', 7.99, '0008.jpg', 30, 100)",
                "INSERT INTO ProductTable VALUES('0009', 'USB3 drive', 8.99, '0009.jpg', 10, 100)",
                "INSERT INTO ProductTable VALUES('0010', 'USB4 drive', 9.99, '0010.jpg', 3, 100)",
                "INSERT INTO ProductTable VALUES('0011', 'USB5 drive', 10.99, '0011.jpg', 0, 100)",
                "INSERT INTO ProductTable VALUES('0012', 'USB6 drive', 10.99, '0011.jpg', 90, 100)"
        };

        try (Connection connection = DriverManager.getConnection(dbURL)) {
            connection.setAutoCommit(false);

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(iniTableSQL[0]);
                System.out.println("  ✓ ProductTable created");

                for (int i = 1; i < iniTableSQL.length; i++) {
                    statement.addBatch(iniTableSQL[i]);
                }

                statement.executeBatch();
                connection.commit();

                System.out.println("  ✓ Sample products inserted (" + (iniTableSQL.length - 1) + " products)");

            } catch (SQLException e) {
                connection.rollback();
                System.err.println("  ❌ Transaction rolled back!");
                throw e;
            }
        } finally {
            lock.unlock();
        }
    }

    private void queryTablesAfterInitialization() throws SQLException {
        System.out.println("📊 Verifying database contents...");
        System.out.println();

        // Query ProductTable
        queryProductTable();

        // Query UserTable
        queryUserTable();
    }

    private void queryProductTable() throws SQLException {
        lock.lock();
        String sqlQuery = "SELECT * FROM ProductTable";

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📦 ProductTable Contents:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        String header = String.format("%-8s %-20s %10s %10s %10s %s",
                "ID", "Description", "Price", "InStock", "MaxStock", "Image");
        System.out.println(header);
        System.out.println("─".repeat(header.length()));

        try (Connection connection = DriverManager.getConnection(dbURL);
             Statement stat = connection.createStatement();
             ResultSet resultSet = stat.executeQuery(sqlQuery)) {

            while (resultSet.next()) {
                String productID = resultSet.getString("productID");
                String description = resultSet.getString("description");
                double unitPrice = resultSet.getDouble("unitPrice");
                String image = resultSet.getString("image");
                int inStock = resultSet.getInt("inStock");
                int maxStock = resultSet.getInt("maxStock");

                String record = String.format("%-8s %-20s %10.2f %10d %10d %s",
                        productID, description, unitPrice, inStock, maxStock, image);
                System.out.println(record);
            }

            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println();

        } finally {
            lock.unlock();
        }
    }

    private void queryUserTable() throws SQLException {
        String sqlQuery = "SELECT userId, username, email, role, isActive, fullName FROM UserTable";

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("👤 UserTable Contents:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        String header = String.format("%-8s %-15s %-25s %-12s %-8s %s",
                "UserID", "Username", "Email", "Role", "Active", "Full Name");
        System.out.println(header);
        System.out.println("─".repeat(header.length()));

        try (Connection connection = DriverManager.getConnection(dbURL);
             Statement stat = connection.createStatement();
             ResultSet resultSet = stat.executeQuery(sqlQuery)) {

            while (resultSet.next()) {
                String userId = resultSet.getString("userId");
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String role = resultSet.getString("role");
                boolean isActive = resultSet.getBoolean("isActive");
                String fullName = resultSet.getString("fullName");

                String record = String.format("%-8s %-15s %-25s %-12s %-8s %s",
                        userId, username, email, role, isActive ? "Yes" : "No", fullName);
                System.out.println(record);
            }

            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println();

        }
    }

    public static void deleteFilesInFolder(Path folder) throws IOException {
        if (Files.exists(folder)) {
            lock.lock();
            try {
                Files.walkFileTree(folder, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }
                });
                System.out.println("🗑️  Deleted files in folder: " + folder);
            } finally {
                lock.unlock();
            }
        }
    }

    public static void copyFolderContents(Path source, Path destination) throws IOException {
        lock.lock();
        if (!Files.exists(source)) {
            throw new IOException("Source folder does not exist: " + source);
        }

        if (!Files.exists(destination)) {
            Files.createDirectories(destination);
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(source)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    Path targetFile = destination.resolve(file.getFileName());
                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } finally {
            lock.unlock();
        }
        System.out.println("📋 Copied image files: " + source + " → " + destination);
    }
}