package ci553.happyshop.client.warehouse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * WarehouseController - Fixed to handle all button actions
 * Routes user actions from WarehouseView to WarehouseModel
 *
 * Handles:
 * - Search functionality (🔍 button and Enter key)
 * - Edit product actions
 * - Delete product actions
 * - Stock adjustment (➕ ➖ buttons)
 * - Submit/Cancel operations
 * - Dashboard refresh
 */
public class WarehouseController {
    public WarehouseModel model;

    /**
     * Process user actions from the view
     *
     * @param action The action string from button text or keyboard event
     * @throws SQLException If database operation fails
     * @throws IOException If file operation fails
     */
    void process(String action) throws SQLException, IOException {
        // Normalize action string (handle emoji and text variations)
        String normalizedAction = normalizeAction(action);

        System.out.println("Controller processing action: " + normalizedAction);

        switch (normalizedAction) {
            case "SEARCH":
                model.doSearch();
                break;

            case "EDIT":
                model.doEdit();
                break;

            case "DELETE":
                model.doDelete();
                break;

            case "ADD":
                model.doChangeStockBy("add");
                break;

            case "SUB":
                model.doChangeStockBy("sub");
                break;

            case "SUBMIT":
                model.doSummit();
                // Refresh dashboard if it exists
                if (model.view != null) {
                    model.view.refreshDashboard();
                }
                break;

            case "CANCEL":
                model.doCancel();
                break;

            case "DASHBOARD":
                // Dashboard is handled directly in view
                // But we could add analytics here if needed
                System.out.println("Dashboard opened");
                break;

            default:
                System.out.println("Unknown action: " + action);
                break;
        }
    }

    /**
     * Normalize action strings to handle emoji buttons and variations
     *
     * @param action Raw action string from button
     * @return Normalized action string
     */
    private String normalizeAction(String action) {
        if (action == null || action.trim().isEmpty()) {
            return "UNKNOWN";
        }

        // Remove emojis and extra spaces, convert to uppercase
        String normalized = action.trim().toUpperCase();

        // Handle emoji buttons
        if (normalized.contains("🔍") || normalized.contains("SEARCH")) {
            return "SEARCH";
        }
        if (normalized.contains("➕") || normalized.equals("+")) {
            return "ADD";
        }
        if (normalized.contains("➖") || normalized.equals("-") || normalized.equals("−")) {
            return "SUB";
        }
        if (normalized.contains("📊") || normalized.contains("DASHBOARD")) {
            return "DASHBOARD";
        }

        // Handle text buttons
        if (normalized.equals("EDIT")) {
            return "EDIT";
        }
        if (normalized.equals("DELETE")) {
            return "DELETE";
        }
        if (normalized.equals("SUBMIT")) {
            return "SUBMIT";
        }
        if (normalized.equals("CANCEL")) {
            return "CANCEL";
        }

        // Return original if no match
        return normalized;
    }

    /**
     * Handle errors gracefully
     *
     * @param action The action that caused the error
     * @param e The exception that occurred
     */
    public void handleError(String action, Exception e) {
        System.err.println("Error processing action '" + action + "': " + e.getMessage());
        e.printStackTrace();

        // You could show error dialog here if needed
        // Alert alert = new Alert(Alert.AlertType.ERROR);
        // alert.setTitle("Operation Failed");
        // alert.setContentText("Failed to " + action + ": " + e.getMessage());
        // alert.showAndWait();
    }

    /**
     * Validate that model is properly initialized
     *
     * @return true if model is ready, false otherwise
     */
    public boolean isModelReady() {
        if (model == null) {
            System.err.println("ERROR: WarehouseModel not initialized in controller!");
            return false;
        }
        return true;
    }

    /**
     * Safe process method that checks model initialization
     *
     * @param action The action to process
     */
    public void safeProcess(String action) {
        try {
            if (!isModelReady()) {
                System.err.println("Cannot process action - model not ready");
                return;
            }
            process(action);
        } catch (SQLException e) {
            handleError(action, e);
            System.err.println("Database error: " + e.getSQLState());
        } catch (IOException e) {
            handleError(action, e);
            System.err.println("File operation error: " + e.getMessage());
        } catch (Exception e) {
            handleError(action, e);
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}