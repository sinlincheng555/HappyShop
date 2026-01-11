package ci553.happyshop.client.warehouse;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Modern Warehouse UI Style Class
 * Contains all styling for the WarehouseView interface
 * Follows modern website design principles
 */
public class WarehouseUIStyle {

    // Color Palette - Modern color scheme
    private static final Color PRIMARY_COLOR = Color.web("#2563eb"); // Modern blue
    private static final Color SECONDARY_COLOR = Color.web("#3b82f6"); // Lighter blue
    private static final Color ACCENT_COLOR = Color.web("#10b981"); // Emerald green
    private static final Color BACKGROUND_COLOR = Color.web("#f8fafc"); // Light gray background
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = Color.web("#1e293b"); // Dark slate
    private static final Color TEXT_SECONDARY = Color.web("#64748b"); // Slate gray
    private static final Color BORDER_COLOR = Color.web("#e2e8f0"); // Light border

    // Gradients for modern effects
    private static final String PRIMARY_GRADIENT = "linear-gradient(135deg, #2563eb 0%, #3b82f6 100%)";
    private static final String SUCCESS_GRADIENT = "linear-gradient(135deg, #10b981 0%, #34d399 100%)";
    private static final String DANGER_GRADIENT = "linear-gradient(135deg, #ef4444 0%, #f87171 100%)";

    // Fonts
    private static final String FONT_FAMILY = "'Segoe UI', 'SF Pro Display', -apple-system, sans-serif";
    private static final Font TITLE_FONT = Font.font(FONT_FAMILY, FontWeight.BOLD, 24);
    private static final Font HEADING_FONT = Font.font(FONT_FAMILY, FontWeight.SEMI_BOLD, 16);
    private static final Font BODY_FONT = Font.font(FONT_FAMILY, FontWeight.NORMAL, 14);
    private static final Font SMALL_FONT = Font.font(FONT_FAMILY, FontWeight.NORMAL, 12);

    // Root container styles
    public static String getRootStyle() {
        return String.format(
                "-fx-background-color: %s; " +
                        "-fx-padding: 20px;",
                toHex(BACKGROUND_COLOR)
        );
    }

    // Search Page Container
    public static String getSearchPageStyle() {
        return String.format(
                "-fx-background-color: %s; " +
                        "-fx-background-radius: 12px; " +
                        "-fx-padding: 25px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 4); " +
                        "-fx-border-color: %s; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 12px;",
                toHex(CARD_COLOR), toHex(BORDER_COLOR)
        );
    }

    // Product Form Page Container
    public static String getProductFormPageStyle() {
        return String.format(
                "-fx-background-color: %s; " +
                        "-fx-background-radius: 12px; " +
                        "-fx-padding: 25px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 4); " +
                        "-fx-border-color: %s; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 12px;",
                toHex(CARD_COLOR), toHex(BORDER_COLOR)
        );
    }

    // Title Label
    public static String getTitleLabelStyle() {
        return String.format(
                "-fx-font-size: 24px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: %s; " +
                        "-fx-padding: 0 0 20px 0;",
                toHex(TEXT_PRIMARY)
        );
    }

    // Section Title (used in form sections)
    public static String getSectionTitleStyle() {
        return String.format(
                "-fx-font-size: 18px; " +
                        "-fx-font-weight: 600; " +
                        "-fx-text-fill: %s; " +
                        "-fx-padding: 15px 0 10px 0; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-width: 0 0 2px 0;",
                toHex(TEXT_PRIMARY), toHex(PRIMARY_COLOR)
        );
    }

    // Regular Label
    public static String getLabelStyle() {
        return String.format(
                "-fx-font-size: 14px; " +
                        "-fx-font-weight: 500; " +
                        "-fx-text-fill: %s; " +
                        "-fx-min-width: 80px;",
                toHex(TEXT_PRIMARY)
        );
    }

    // Secondary Label (for less important text)
    public static String getSecondaryLabelStyle() {
        return String.format(
                "-fx-font-size: 13px; " +
                        "-fx-text-fill: %s;",
                toHex(TEXT_SECONDARY)
        );
    }

    // Text Field
    public static String getTextFieldStyle() {
        return String.format(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 15px; " +
                        "-fx-background-color: white; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-border-width: 1.5px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.02), 3, 0, 0, 1); " +
                        "-fx-pref-width: 200px; " +
                        "-fx-prompt-text-fill: %s; " +
                        "} " +
                        ".text-field:focused { " +
                        "-fx-border-color: %s; " +
                        "-fx-border-width: 2px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(37, 99, 235, 0.1), 5, 0, 0, 2);",
                toHex(BORDER_COLOR), toHex(TEXT_SECONDARY), toHex(PRIMARY_COLOR)
        );
    }

    // Text Area
    public static String getTextAreaStyle() {
        return String.format(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 12px 15px; " +
                        "-fx-background-color: white; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-border-width: 1.5px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.02), 3, 0, 0, 1); " +
                        "} " +
                        ".text-area:focused { " +
                        "-fx-border-color: %s; " +
                        "-fx-border-width: 2px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(37, 99, 235, 0.1), 5, 0, 0, 2);",
                toHex(BORDER_COLOR), toHex(PRIMARY_COLOR)
        );
    }

    // Primary Button (Search, Submit, etc.)
    public static String getPrimaryButtonStyle() {
        return String.format(
                "-fx-font-size: 14px; " +
                        "-fx-font-weight: 600; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 12px 24px; " +
                        "-fx-background-color: %s; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(37, 99, 235, 0.2), 5, 0, 0, 2); " +
                        "} " +
                        ".button:hover { " +
                        "-fx-background-color: %s; " +
                        "-fx-effect: dropshadow(gaussian, rgba(37, 99, 235, 0.3), 8, 0, 0, 3); " +
                        "} " +
                        ".button:pressed { " +
                        "-fx-background-color: %s; " +
                        "-fx-effect: dropshadow(gaussian, rgba(37, 99, 235, 0.1), 3, 0, 0, 1); " +
                        "-fx-translate-y: 1px;",
                PRIMARY_GRADIENT, toHex(Color.web("#1d4ed8")), toHex(Color.web("#1e40af"))
        );
    }

    // Secondary Button (Edit, Cancel)
    public static String getSecondaryButtonStyle() {
        return String.format(
                "-fx-font-size: 14px; " +
                        "-fx-font-weight: 500; " +
                        "-fx-text-fill: %s; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-color: transparent; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-width: 1.5px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-cursor: hand; " +
                        "} " +
                        ".button:hover { " +
                        "-fx-background-color: %s; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: transparent; " +
                        "} " +
                        ".button:pressed { " +
                        "-fx-background-color: %s; " +
                        "-fx-translate-y: 1px;",
                toHex(PRIMARY_COLOR), toHex(PRIMARY_COLOR), toHex(SECONDARY_COLOR), toHex(PRIMARY_COLOR)
        );
    }

    // Success Button (Add, Save)
    public static String getSuccessButtonStyle() {
        return String.format(
                "-fx-font-size: 14px; " +
                        "-fx-font-weight: 600; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 12px 24px; " +
                        "-fx-background-color: %s; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(16, 185, 129, 0.2), 5, 0, 0, 2); " +
                        "} " +
                        ".button:hover { " +
                        "-fx-background-color: %s; " +
                        "-fx-effect: dropshadow(gaussian, rgba(16, 185, 129, 0.3), 8, 0, 0, 3); " +
                        "} " +
                        ".button:pressed { " +
                        "-fx-background-color: %s; " +
                        "-fx-effect: dropshadow(gaussian, rgba(16, 185, 129, 0.1), 3, 0, 0, 1); " +
                        "-fx-translate-y: 1px;",
                SUCCESS_GRADIENT, toHex(Color.web("#0da271")), toHex(Color.web("#059669"))
        );
    }

    // Danger Button (Delete, Remove)
    public static String getDangerButtonStyle() {
        return String.format(
                "-fx-font-size: 14px; " +
                        "-fx-font-weight: 600; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 12px 24px; " +
                        "-fx-background-color: %s; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(239, 68, 68, 0.2), 5, 0, 0, 2); " +
                        "} " +
                        ".button:hover { " +
                        "-fx-background-color: %s; " +
                        "-fx-effect: dropshadow(gaussian, rgba(239, 68, 68, 0.3), 8, 0, 0, 3); " +
                        "} " +
                        ".button:pressed { " +
                        "-fx-background-color: %s; " +
                        "-fx-effect: dropshadow(gaussian, rgba(239, 68, 68, 0.1), 3, 0, 0, 1); " +
                        "-fx-translate-y: 1px;",
                DANGER_GRADIENT, toHex(Color.web("#dc2626")), toHex(Color.web("#b91c1c"))
        );
    }

    // ComboBox
    public static String getComboBoxStyle() {
        return String.format(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 10px 15px; " +
                        "-fx-background-color: white; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-border-width: 1.5px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.02), 3, 0, 0, 1); " +
                        "-fx-pref-width: 300px; " +
                        "} " +
                        ".combo-box:hover { " +
                        "-fx-border-color: %s; " +
                        "} " +
                        ".combo-box:focused { " +
                        "-fx-border-color: %s; " +
                        "-fx-border-width: 2px;",
                toHex(BORDER_COLOR), toHex(SECONDARY_COLOR), toHex(PRIMARY_COLOR)
        );
    }

    // ListView
    public static String getListViewStyle() {
        return String.format(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-border-width: 1.5px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.02), 3, 0, 0, 1); " +
                        "} " +
                        ".list-cell { " +
                        "-fx-padding: 12px 15px; " +
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: transparent; " +
                        "} " +
                        ".list-cell:filled:selected { " +
                        "-fx-background-color: %s; " +
                        "-fx-background-radius: 6px; " +
                        "} " +
                        ".list-cell:filled:hover { " +
                        "-fx-background-color: %s; " +
                        "-fx-background-radius: 6px; " +
                        "-fx-cursor: hand;",
                toHex(BORDER_COLOR), toHex(Color.web("#dbeafe")), toHex(Color.web("#f0f9ff"))
        );
    }

    // Image View Container (for product images)
    public static String getImageContainerStyle() {
        return String.format(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-border-width: 1.5px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 2); " +
                        "-fx-padding: 8px; " +
                        "-fx-cursor: hand; " +
                        "} " +
                        ":hover { " +
                        "-fx-border-color: %s; " +
                        "-fx-effect: dropshadow(gaussian, rgba(37, 99, 235, 0.1), 6, 0, 0, 3);",
                toHex(BORDER_COLOR), toHex(PRIMARY_COLOR)
        );
    }

    // Divider Line Style
    public static String getDividerLineStyle() {
        return String.format(
                "-fx-stroke: %s; " +
                        "-fx-stroke-width: 1.5px; " +
                        "-fx-stroke-dash-array: 5 5;",
                toHex(BORDER_COLOR)
        );
    }

    // Search Summary Label
    public static String getSearchSummaryStyle() {
        return String.format(
                "-fx-font-size: 14px; " +
                        "-fx-font-weight: 500; " +
                        "-fx-text-fill: %s; " +
                        "-fx-padding: 8px 16px; " +
                        "-fx-background-color: %s; " +
                        "-fx-background-radius: 20px; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 20px;",
                toHex(PRIMARY_COLOR), toHex(Color.web("#eff6ff")), toHex(Color.web("#dbeafe"))
        );
    }

    // Form Section Container (for grouping related fields)
    public static String getFormSectionStyle() {
        return String.format(
                "-fx-background-color: %s; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-padding: 20px; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.02), 3, 0, 0, 1);",
                toHex(Color.web("#f8fafc")), toHex(BORDER_COLOR)
        );
    }

    // Helper method to convert Color to hex string
    private static String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    // Card shadow effect for containers
    public static CornerRadii getCardCornerRadii() {
        return new CornerRadii(12);
    }

    public static Background getCardBackground() {
        return new Background(new BackgroundFill(
                CARD_COLOR,
                new CornerRadii(12),
                Insets.EMPTY
        ));
    }

    public static Border getCardBorder() {
        return new Border(new BorderStroke(
                BORDER_COLOR,
                BorderStrokeStyle.SOLID,
                new CornerRadii(12),
                new BorderWidths(1)
        ));
    }

    // Shadow effect for cards
    public static String getCardShadow() {
        return "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 4);";
    }

    // Animation transition for hover effects
    public static String getTransitionStyle() {
        return "-fx-transition: all 0.2s ease-in-out;";
    }
}