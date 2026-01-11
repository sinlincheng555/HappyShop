package ci553.happyshop.utility;

import ci553.happyshop.catalogue.Product;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Enhanced StockAllocationHelper for warehouse management.
 * Creates UI components for displaying stock allocation and availability.
 *
 * @author University of Brighton Student
 * @version 2.1
 */
public class StockAllocationHelper {

    // Configuration constants
    private static final double CRITICAL_THRESHOLD = 0.1;
    private static final double LOW_THRESHOLD = 0.3;
    private static final double OPTIMAL_MAX_THRESHOLD = 0.7;
    private static final double HIGH_THRESHOLD = 0.9;

    private static final String COLOR_GREEN = "#22c55e";
    private static final String COLOR_YELLOW = "#eab308";
    private static final String COLOR_ORANGE = "#f97316";
    private static final String COLOR_RED = "#ef4444";
    private static final String COLOR_BLUE = "#3b82f6";
    private static final String COLOR_PURPLE = "#8b5cf6";
    private static final String COLOR_TEAL = "#10b981";

    /**
     * Creates a detailed stock allocation panel for warehouse view.
     * Shows total, in-stock, and available stock with visual indicators.
     *
     * @param product Product to display allocation for
     * @return VBox with stock allocation details
     * @throws IllegalArgumentException if product is null
     */
    public static VBox createAllocationPanel(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        VBox panel = new VBox(10);
        panel.setStyle("-fx-padding: 15px; " +
                "-fx-background-color: linear-gradient(to bottom, #ffffff, #f8fafc); " +
                "-fx-background-radius: 10px; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 10px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");

        // Title
        Label title = new Label("📊 Stock Allocation Analysis");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1e293b;");

        // Separator
        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setStyle("-fx-background-color: #e2e8f0; -fx-padding: 5px 0;");

        // Stock metrics
        HBox stockMetrics = createStockMetrics(product);

        // Progress bar visualization
        VBox progressSection = createProgressVisualization(product);

        // Status message
        Label status = createStatusLabel(product);

        panel.getChildren().addAll(title, separator, stockMetrics, progressSection, status);
        return panel;
    }

    /**
     * Creates a compact single-line stock allocation display.
     * Ideal for list views or table cells.
     *
     * @param product Product to display
     * @return HBox with compact stock info
     */
    public static HBox createCompactAllocation(Product product) {
        if (product == null) {
            return createErrorHBox("No product data");
        }

        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setStyle("-fx-padding: 8px 12px; " +
                "-fx-background-color: #f1f5f9; " +
                "-fx-background-radius: 8px;");

        int totalStock = product.getStockQuantity();
        int maxCapacity = getMaxStockCapacity(product);
        double utilization = calculateUtilization(totalStock, maxCapacity);

        Label stockLabel = new Label(String.format("Stock: %d/%d", totalStock, maxCapacity));
        stockLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #475569;");

        // Utilization indicator
        String utilizationColor = getUtilizationColor(utilization);

        Label utilizationLabel = new Label(String.format("%.0f%%", utilization * 100));
        utilizationLabel.setStyle(String.format(
                "-fx-font-size: 12px; -fx-font-weight: bold; " +
                        "-fx-text-fill: white; -fx-padding: 2px 8px; " +
                        "-fx-background-color: %s; -fx-background-radius: 10px;",
                utilizationColor
        ));

        container.getChildren().addAll(stockLabel, utilizationLabel);
        return container;
    }

    /**
     * Creates a visual progress bar showing stock utilization.
     *
     * @param product Product to display
     * @return VBox with visual progress bar and labels
     */
    public static VBox createAllocationBar(Product product) {
        if (product == null) {
            return createErrorVBox("No product data");
        }

        VBox container = new VBox(5);

        int totalStock = product.getStockQuantity();
        int maxCapacity = getMaxStockCapacity(product);
        double utilization = calculateUtilization(totalStock, maxCapacity);

        // Progress bar
        ProgressBar progressBar = new ProgressBar(utilization);
        progressBar.setPrefWidth(250);
        progressBar.setPrefHeight(12);
        progressBar.setStyle(String.format(
                "-fx-accent: %s; -fx-background-color: #e2e8f0; -fx-background-radius: 6px;",
                getUtilizationColor(utilization)
        ));

        // Labels
        HBox labelContainer = new HBox();
        labelContainer.setAlignment(Pos.CENTER_LEFT);

        Label stockLabel = new Label(String.format("%d units", totalStock));
        stockLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        Label capacityLabel = new Label(String.format("of %d capacity", maxCapacity));
        capacityLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label percentageLabel = new Label(String.format("%.0f%%", utilization * 100));
        percentageLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #475569;");

        labelContainer.getChildren().addAll(stockLabel, capacityLabel, spacer, percentageLabel);

        container.getChildren().addAll(progressBar, labelContainer);
        return container;
    }

    // ========== PRIVATE HELPER METHODS ==========

    private static HBox createStockMetrics(Product product) {
        HBox metrics = new HBox(20);
        metrics.setAlignment(Pos.CENTER_LEFT);

        int totalStock = product.getStockQuantity();
        int maxCapacity = getMaxStockCapacity(product);
        int availableSpace = Math.max(0, maxCapacity - totalStock);
        double utilization = calculateUtilization(totalStock, maxCapacity);

        // Current Stock Metric
        VBox currentStockBox = createMetricBox(
                "Current Stock",
                String.valueOf(totalStock),
                COLOR_BLUE,
                "📦"
        );

        // Capacity Metric
        VBox capacityBox = createMetricBox(
                "Max Capacity",
                String.valueOf(maxCapacity),
                COLOR_PURPLE,
                "🏭"
        );

        // Available Space Metric
        VBox spaceBox = createMetricBox(
                "Available Space",
                String.valueOf(availableSpace),
                COLOR_TEAL,
                "📈"
        );

        // Utilization Metric
        VBox utilizationBox = createMetricBox(
                "Utilization",
                String.format("%.1f%%", utilization * 100),
                getUtilizationColor(utilization),
                "📊"
        );

        metrics.getChildren().addAll(currentStockBox, capacityBox, spaceBox, utilizationBox);
        return metrics;
    }

    private static VBox createProgressVisualization(Product product) {
        VBox progressContainer = new VBox(8);

        int totalStock = product.getStockQuantity();
        int maxCapacity = getMaxStockCapacity(product);
        double utilization = calculateUtilization(totalStock, maxCapacity);

        // Progress bar
        ProgressBar progressBar = new ProgressBar(utilization);
        progressBar.setPrefWidth(300);
        progressBar.setPrefHeight(10);
        progressBar.setStyle(String.format(
                "-fx-accent: %s; -fx-background-color: #e2e8f0; -fx-background-radius: 5px;",
                getUtilizationColor(utilization)
        ));

        // Progress labels
        HBox progressLabels = new HBox();
        HBox.setHgrow(progressLabels, Priority.ALWAYS);

        Label lowLabel = new Label("0%");
        lowLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label highLabel = new Label("100%");
        highLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

        progressLabels.getChildren().addAll(lowLabel, spacer, highLabel);

        // Current value label
        Label currentLabel = new Label(String.format("Current: %.1f%%", utilization * 100));
        currentLabel.setStyle(String.format(
                "-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: %s;",
                getUtilizationColor(utilization)
        ));

        progressContainer.getChildren().addAll(progressBar, progressLabels, currentLabel);
        return progressContainer;
    }

    private static Label createStatusLabel(Product product) {
        int totalStock = product.getStockQuantity();
        int maxCapacity = getMaxStockCapacity(product);
        double utilization = calculateUtilization(totalStock, maxCapacity);

        String statusText;
        String statusStyle;

        if (totalStock == 0) {
            statusText = "🚨 OUT OF STOCK - Immediate restocking required!";
            statusStyle = "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #ef4444; -fx-padding: 8px; -fx-background-color: #fee2e2; -fx-background-radius: 6px;";
        } else if (utilization <= CRITICAL_THRESHOLD) {
            statusText = "⚠️ CRITICAL STOCK - Below 10% capacity. Consider restocking.";
            statusStyle = "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #f97316; -fx-padding: 8px; -fx-background-color: #ffedd5; -fx-background-radius: 6px;";
        } else if (utilization <= LOW_THRESHOLD) {
            statusText = "📉 LOW STOCK - Below 30% capacity. Monitor closely.";
            statusStyle = "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #eab308; -fx-padding: 8px; -fx-background-color: #fef3c7; -fx-background-radius: 6px;";
        } else if (utilization <= OPTIMAL_MAX_THRESHOLD) {
            statusText = "✅ OPTIMAL STOCK - Within 30-70% capacity. Good balance.";
            statusStyle = "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #22c55e; -fx-padding: 8px; -fx-background-color: #dcfce7; -fx-background-radius: 6px;";
        } else if (utilization <= HIGH_THRESHOLD) {
            statusText = "📈 HIGH STOCK - Above 70% capacity. Reduce ordering.";
            statusStyle = "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #3b82f6; -fx-padding: 8px; -fx-background-color: #dbeafe; -fx-background-radius: 6px;";
        } else {
            statusText = "🚫 OVERSTOCKED - Above 90% capacity. Consider redistribution.";
            statusStyle = "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #8b5cf6; -fx-padding: 8px; -fx-background-color: #ede9fe; -fx-background-radius: 6px;";
        }

        Label statusLabel = new Label(statusText);
        statusLabel.setStyle(statusStyle);
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(350);

        return statusLabel;
    }

    private static VBox createMetricBox(String label, String value, String color, String icon) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 18px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle(String.format(
                "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: %s;",
                color
        ));

        Label nameLabel = new Label(label);
        nameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        box.getChildren().addAll(iconLabel, valueLabel, nameLabel);
        return box;
    }

    private static String getStockLevelText(Product product) {
        int totalStock = product.getStockQuantity();
        int maxCapacity = getMaxStockCapacity(product);
        double utilization = calculateUtilization(totalStock, maxCapacity);

        if (totalStock == 0) return "Out of Stock";
        if (utilization <= CRITICAL_THRESHOLD) return "Critical";
        if (utilization <= LOW_THRESHOLD) return "Low";
        if (utilization <= OPTIMAL_MAX_THRESHOLD) return "Optimal";
        if (utilization <= HIGH_THRESHOLD) return "High";
        return "Overstocked";
    }

    private static String getStockRecommendation(Product product) {
        int totalStock = product.getStockQuantity();
        int maxCapacity = getMaxStockCapacity(product);
        double utilization = calculateUtilization(totalStock, maxCapacity);

        if (totalStock == 0) return "Recommendation: IMMEDIATE RESTOCKING REQUIRED!";
        if (utilization <= CRITICAL_THRESHOLD) return "Recommendation: Restock immediately to avoid stockouts.";
        if (utilization <= LOW_THRESHOLD) return "Recommendation: Schedule restocking within 1-2 weeks.";
        if (utilization <= OPTIMAL_MAX_THRESHOLD) return "Recommendation: Maintain current stock levels.";
        if (utilization <= HIGH_THRESHOLD) return "Recommendation: Reduce ordering frequency.";
        return "Recommendation: Consider redistributing excess stock.";
    }

    private static String getUtilizationColor(double utilization) {
        if (utilization >= HIGH_THRESHOLD) return COLOR_GREEN; // Green
        if (utilization >= 0.5) return COLOR_YELLOW; // Yellow
        if (utilization >= 0.2) return COLOR_ORANGE; // Orange
        return COLOR_RED; // Red
    }

    // ========== UTILITY METHODS ==========

    private static double calculateUtilization(int totalStock, int maxCapacity) {
        return maxCapacity > 0 ? (double) totalStock / maxCapacity : 0;
    }

    private static int getMaxStockCapacity(Product product) {
        // Try to access max stock capacity - handle if method doesn't exist
        try {
            // Assuming Product has getMaxStockCapacity() method
            return product.getMaxStockCapacity();
        } catch (NoSuchMethodError e) {
            // Fallback: use a default or calculate based on stock quantity
            return Math.max(product.getStockQuantity() * 2, 100);
        }
    }

    private static HBox createErrorHBox(String message) {
        HBox errorBox = new HBox();
        errorBox.setAlignment(Pos.CENTER);
        Label errorLabel = new Label("⚠️ " + message);
        errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
        errorBox.getChildren().add(errorLabel);
        return errorBox;
    }

    private static VBox createErrorVBox(String message) {
        VBox errorBox = new VBox();
        errorBox.setAlignment(Pos.CENTER);
        Label errorLabel = new Label("⚠️ " + message);
        errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
        errorBox.getChildren().add(errorLabel);
        return errorBox;
    }
}