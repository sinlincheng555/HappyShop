package ci553.happyshop.client;

import ci553.happyshop.client.auth.LoginController;
import ci553.happyshop.client.auth.LoginModel;
import ci553.happyshop.client.auth.LoginView;
import ci553.happyshop.orderManagement.OrderHub;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main Application Entry Point - Authentication First Priority
 *
 * SECURITY FLOW:
 * 1. Initialize OrderHub (backend system)
 * 2.  Show LOGIN SCREEN ONLY
 * 3.  NO customer interface loads
 * 4.  NO warehouse interface loads
 * 5.  NO order tracker loads
 * 6.  NO picker interface loads
 * 7.  NO emergency exit loads
 *
 * After successful login, LoginView launches the appropriate client based on user role.
 *
 * @author HappyShop Development Team
 * @version 2.0
 */
public class Main extends Application {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   🛍️ HappyShop System Starting...");
        System.out.println("========================================");
        System.out.println();
        System.out.println("🔐 Authentication Required");
        System.out.println("   All features locked until login");
        System.out.println();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("⚙️  Initializing system...");

            // Initialize ONLY the backend OrderHub system (no UI)
            initializeOrderHub();
            System.out.println("✅ Backend initialized");

            System.out.println();
            System.out.println("🔒 Starting authentication system...");
            System.out.println("   No clients will load until login");
            System.out.println();

            // Start ONLY the login screen
            startLoginScreen(primaryStage);

            System.out.println("✅ Login screen ready");
            System.out.println();
            System.out.println("📌 Default Admin Credentials:");
            System.out.println("   Username: admin");
            System.out.println("   Password: admin123");
            System.out.println();
            System.out.println("⚠️  IMPORTANT: Change admin password after first login!");
            System.out.println("========================================");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start application", e);
            System.err.println("❌ FATAL ERROR: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }

    /**
     * Initialize OrderHub backend system
     * This runs in the background and doesn't show any UI
     */
    private void initializeOrderHub() {
        try {
            OrderHub orderHub = OrderHub.getOrderHub();
            orderHub.initializeOrderMap();
            System.out.println("   ✓ OrderHub initialized");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to initialize OrderHub", e);
            System.err.println("   ⚠️  OrderHub initialization failed (non-critical)");
        }
    }

    /**
     * Start the login screen ONLY
     * No other windows or clients are loaded at this point
     */
    private void startLoginScreen(Stage primaryStage) {
        try {
            // Create login MVC components
            LoginModel loginModel = new LoginModel();
            LoginView loginView = new LoginView();
            LoginController loginController = new LoginController();

            // Link MVC components
            loginView.loginController = loginController;
            loginController.loginModel = loginModel;
            loginModel.loginView = loginView;

            // Start login screen
            loginView.start(primaryStage);

            System.out.println("   ✓ Login screen launched");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start login screen", e);
            throw new RuntimeException("Cannot start without authentication system", e);
        }
    }

    /**
     * Note: The following methods are NO LONGER called on startup:
     * - startCustomerClient() - Launched by LoginView after customer login
     * - startWarehouseClient() - Launched by LoginView after warehouse login
     * - startPickerClient() - Can be launched manually if needed
     * - startOrderTracker() - Can be launched manually if needed
     * - startEmergencyExit() - Only launched for admin users by LoginView
     *
     * All client launching is now handled by LoginView.launchClientForUser()
     * after successful authentication.
     */
}