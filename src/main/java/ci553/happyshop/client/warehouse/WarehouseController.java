package ci553.happyshop.client.warehouse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * WarehouseController - FIXED to handle all actions correctly
 * Routes user actions from WarehouseView to WarehouseModel
 */
public class WarehouseController {
    public WarehouseModel model;

    /**
     * Process user actions from the view
     */
    void process(String action) throws SQLException, IOException {
        if (model == null) {
            System.err.println("ERROR: Model is not initialized!");
            return;
        }

        // Normalize action string
        String normalizedAction = normalizeAction(action);
        System.out.println("Controller processing action: " + normalizedAction + " (original: " + action + ")");

        switch (normalizedAction) {
            case "LOAD_ALL":
                // Load all products on startup
                model.doLoadAll();
                break;

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
                // Refresh dashboard after submit
                if (model.view != null) {
                    model.view.refreshDashboard();
                }
                break;

            case "CANCEL":
                model.doCancel();
                break;

            case "DASHBOARD":
                System.out.println("Dashboard opened");
                break;

            default:
                System.out.println("Unknown action: " + action + " (normalized: " + normalizedAction + ")");
                break;
        }
    }

    /**
     * Normalize action strings to handle emoji buttons and variations
     */
    private String normalizeAction(String action) {
        if (action == null || action.trim().isEmpty()) {
            return "UNKNOWN";
        }

        // Remove emojis and extra spaces, convert to uppercase
        String normalized = action.trim().toUpperCase();

        // Handle emoji buttons and text variations
        if (normalized.contains("🔎") || normalized.contains("🔍") || normalized.contains("SEARCH")) {
            return "SEARCH";
        }
        if (normalized.equals("➕") || normalized.equals("+")) {
            return "ADD";
        }
        if (normalized.equals("➖") || normalized.equals("-") || normalized.equals("−")) {
            return "SUB";
        }
        if (normalized.contains("📊") || normalized.contains("DASHBOARD")) {
            return "DASHBOARD";
        }
        if (normalized.contains("✏️") || normalized.contains("EDIT")) {
            return "EDIT";
        }
        if (normalized.contains("🗑️") || normalized.contains("DELETE")) {
            return "DELETE";
        }
        if (normalized.contains("💾") || normalized.contains("SUBMIT")) {
            return "SUBMIT";
        }
        if (normalized.contains("CANCEL")) {
            return "CANCEL";
        }
        if (normalized.equals("LOAD_ALL")) {
            return "LOAD_ALL";
        }

        // Return original if no match
        return normalized;
    }

    /**
     * Handle errors gracefully
     */
    public void handleError(String action, Exception e) {
        System.err.println("Error processing action '" + action + "': " + e.getMessage());
        e.printStackTrace();
    }

    /**
     * Validate that model is properly initialized
     */
    public boolean isModelReady() {
        if (model == null) {
            System.err.println("ERROR: WarehouseModel not initialized in controller!");
            return false;
        }
        return true;
    }
}