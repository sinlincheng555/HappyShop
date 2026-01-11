package ci553.happyshop.utility;

import ci553.happyshop.catalogue.Product;
import ci553.happyshop.catalogue.StockLevelIndicator;
import ci553.happyshop.catalogue.StockLevelIndicator.StockLevel;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Helper class for creating stock level display components in JavaFX.
 * Demonstrates Factory Pattern for UI component creation.
 *
 * Responsibilities:
 * - Creates consistently styled stock level indicators
 * - Provides visual feedback for stock status
 * - Encapsulates UI creation logic
 *
 * Design Pattern: Factory Pattern
 * - Centralizes creation of stock level UI components
 * - Ensures consistent styling across the application
 *
 * @author University of Brighton Student
 * @version 1.0
 */
public class StockDisplayHelper {

    /**
     * Creates a styled stock level badge for JavaFX UI.
     *
     * @param product The product to display stock info for
     * @return Label configured as a stock level badge
     */
    public static Label createStockBadge(Product product) {
        StockLevel level = product.getStockLevel();

        Label badge = new Label(level.getDisplayText());
        badge.setStyle(StockLevelIndicator.getStyleForLevel(level));
        badge.setAlignment(Pos.CENTER);
        badge.setMaxWidth(Double.MAX_VALUE);

        return badge;
    }

    /**
     * Creates a detailed stock information panel.
     * Shows stock quantity, percentage, and status.
     *
     * @param product The product to display stock info for
     * @return VBox containing detailed stock information
     */
    public static VBox createDetailedStockPanel(Product product) {
        VBox panel = new VBox(5);
        panel.setAlignment(Pos.CENTER_LEFT);

        // Stock quantity label
        Label quantityLabel = new Label(String.format("Stock: %d/%d units",
                product.getStockQuantity(),
                product.getMaxStockCapacity()
        ));
        quantityLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Stock percentage bar (visual indicator)
        HBox progressBar = createProgressBar(product.getStockPercentage());

        // Stock status badge
        Label statusBadge = createStockBadge(product);

        panel.getChildren().addAll(quantityLabel, progressBar, statusBadge);
        return panel;
    }

    /**
     * Creates a visual progress bar showing stock percentage.
     *
     * @param percentage Stock percentage (0.0 to 1.0)
     * @return HBox representing a progress bar
     */
    private static HBox createProgressBar(double percentage) {
        HBox progressBar = new HBox();
        progressBar.setStyle(
                "-fx-background-color: #E0E0E0; " +
                        "-fx-background-radius: 5px; " +
                        "-fx-pref-height: 8px; " +
                        "-fx-max-width: 200px;"
        );

        // Fill indicator
        HBox fill = new HBox();
        fill.setPrefWidth(200 * percentage);

        // Color based on percentage
        String fillColor;
        if (percentage <= 0.10) {
            fillColor = "#FF3B30"; // Red
        } else if (percentage <= 0.30) {
            fillColor = "#FF9500"; // Orange
        } else {
            fillColor = "#34C759"; // Green
        }

        fill.setStyle(
                "-fx-background-color: " + fillColor + "; " +
                        "-fx-background-radius: 5px;"
        );

        progressBar.getChildren().add(fill);
        return progressBar;
    }

    /**
     * Creates a compact stock indicator (just icon and text).
     *
     * @param product The product
     * @return HBox with icon and status text
     */
    public static HBox createCompactStockIndicator(Product product) {
        HBox indicator = new HBox(5);
        indicator.setAlignment(Pos.CENTER_LEFT);

        StockLevel level = product.getStockLevel();

        // Icon based on level
        String icon;
        switch (level) {
            case CRITICAL:
                icon = "❌";
                break;
            case LOW:
                icon = "🚨";
                break;
            case MEDIUM:
                icon = "⚠️";
                break;
            case HIGH:
                icon = "✅";
                break;
            default:
                icon = "●";
        }

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 16px;");

        Label textLabel = new Label(level.getDisplayText());
        textLabel.setStyle(String.format(
                "-fx-text-fill: %s; -fx-font-weight: bold; -fx-font-size: 12px;",
                level.getBackgroundColor()
        ));

        indicator.getChildren().addAll(iconLabel, textLabel);
        return indicator;
    }

    /**
     * Creates a console-friendly stock status string with emojis.
     *
     * @param product The product
     * @return Formatted string for console output
     */
    public static String createConsoleStockStatus(Product product) {
        StockLevel level = product.getStockLevel();
        String colorCode = StockLevelIndicator.getConsoleColorCode(level);
        String resetCode = StockLevelIndicator.resetConsoleColor();

        String icon;
        switch (level) {
            case CRITICAL:
                icon = "❌";
                break;
            case LOW:
                icon = "🚨";
                break;
            case MEDIUM:
                icon = "⚠️";
                break;
            case HIGH:
                icon = "✅";
                break;
            default:
                icon = "●";
        }

        return String.format("%s %s%s%s (Stock: %d/%d units, %.1f%%)",
                icon,
                colorCode,
                level.getDisplayText(),
                resetCode,
                product.getStockQuantity(),
                product.getMaxStockCapacity(),
                product.getStockPercentage() * 100
        );
    }

    /**
     * Gets appropriate emoji for stock level.
     *
     * @param stockLevel The stock level
     * @return Emoji string
     */
    public static String getStockEmoji(StockLevel stockLevel) {
        switch (stockLevel) {
            case CRITICAL:
                return "❌";
            case LOW:
                return "🚨";
            case MEDIUM:
                return "⚠️";
            case HIGH:
                return "✅";
            default:
                return "●";
        }
    }
}