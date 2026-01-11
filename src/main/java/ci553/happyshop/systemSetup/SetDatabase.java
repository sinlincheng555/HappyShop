package ci553.happyshop.systemSetup;

import ci553.happyshop.storageAccess.DatabaseRWFactory;
import ci553.happyshop.utility.StorageLocation;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Enhanced SetDatabase with maxStock column support.
 *
 * Key Enhancement:
 * - ProductTable now includes maxStock column for stock level tracking
 * - Enables low stock warnings when inventory falls below 10%
 *
 * WARNING: This class resets the entire database to a clean state.
 * All existing data will be lost.
 */
public class SetDatabase {

    private static final String dbURL = DatabaseRWFactory.dbURL + ";create=true";
    private static Path imageWorkingFolderPath = StorageLocation.imageFolderPath;
    private static Path imageBackupFolderPath = StorageLocation.imageResetFolderPath;

    private String[] tables = {"ProductTable"};
    private static final Lock lock = new ReentrantLock();

    public static void main(String[] args) throws SQLException, IOException {
        SetDatabase setDB = new SetDatabase();
        setDB.clearTables();
        setDB.initializeTable();
        setDB.queryTableAfterInitilization();
        deleteFilesInFolder(imageWorkingFolderPath);
        copyFolderContents(imageBackupFolderPath, imageWorkingFolderPath);
    }

    private void clearTables() throws SQLException {
        lock.lock();
        try (Connection con = DriverManager.getConnection(dbURL);
             Statement statement = con.createStatement()) {
            System.out.println("Database happyShopDB is connected successfully!");
            for (String table : tables) {
                try {
                    statement.executeUpdate("DROP TABLE " + table.toUpperCase());
                    System.out.println("Dropped table: " + table);
                } catch (SQLException e) {
                    if ("42Y55".equals(e.getSQLState())) {
                        System.out.println("Table " + table + " does not exist. Skipping...");
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Creates ProductTable with maxStock column and initializes with sample data.
     */
    private void initializeTable() throws SQLException {
        lock.lock();

        String[] iniTableSQL = {
                // Create ProductTable with maxStock column
                "CREATE TABLE ProductTable(" +
                        "productID CHAR(4) PRIMARY KEY," +
                        "description VARCHAR(100)," +
                        "unitPrice DOUBLE," +
                        "image VARCHAR(100)," +
                        "inStock INT," +
                        "maxStock INT," +  // NEW COLUMN
                        "CHECK (inStock >= 0)," +
                        "CHECK (maxStock >= 0)" +
                        ")",

                // Insert sample data (inStock, maxStock)
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
            System.out.println("Database happyShopDB is created successfully!");
            connection.setAutoCommit(false);

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(iniTableSQL[0]);

                for (int i = 1; i < iniTableSQL.length; i++) {
                    statement.addBatch(iniTableSQL[i]);
                }

                statement.executeBatch();
                connection.commit();

                System.out.println("Table and data initialized successfully.");
                System.out.println("Sample data includes products with various stock levels:");
                System.out.println("- High stock (>30%): Products 0001, 0006, 0007, 0012");
                System.out.println("- Medium stock (10-30%): Products 0002, 0003, 0008");
                System.out.println("- Low stock (≤10%): Products 0004, 0005, 0009, 0010");
                System.out.println("- Out of stock: Product 0011");

            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Transaction rolled back due to an error!");
                e.printStackTrace();
            }
        } finally {
            lock.unlock();
        }
    }

    private void queryTableAfterInitilization() throws SQLException {
        lock.lock();
        String sqlQuery = "SELECT * FROM ProductTable";

        System.out.println("-------------Product Information Below -----------------");
        String title = String.format("%-12s %-20s %-10s %-10s %-10s %s",
                "productID",
                "description",
                "unitPrice",
                "inStock",
                "maxStock",
                "image");
        System.out.println(title);

        try (Connection connection = DriverManager.getConnection(dbURL);
             Statement stat = connection.createStatement()) {
            ResultSet resultSet = stat.executeQuery(sqlQuery);
            while (resultSet.next()) {
                String productID = resultSet.getString("productID");
                String description = resultSet.getString("description");
                double unitPrice = resultSet.getDouble("unitPrice");
                String image = resultSet.getString("image");
                int inStock = resultSet.getInt("inStock");
                int maxStock = resultSet.getInt("maxStock");
                String record = String.format("%-12s %-20s %-10.2f %-10d %-10d %s",
                        productID, description, unitPrice, inStock, maxStock, image);
                System.out.println(record);
            }
        } finally {
            lock.unlock();
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
                System.out.println("Deleted files in folder: " + folder);
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println("Folder " + folder + " does not exist");
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
        System.out.println("Copied files from: " + source + " → " + destination);
    }
}