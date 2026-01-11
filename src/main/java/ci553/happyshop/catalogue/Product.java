package ci553.happyshop.catalogue;

import ci553.happyshop.catalogue.StockLevelIndicator.StockLevel;

/**
 * Enhanced Product class with stock level tracking and low stock warning support.
 *
 * Key Enhancements:
 * - Tracks maximum stock capacity for percentage calculations
 * - Integrates with StockLevelIndicator for intelligent stock status
 * - Provides formatted stock status information
 *
 * Design Principles Applied:
 * - Single Responsibility: Product manages product data
 * - Open/Closed: Extended functionality without modifying existing behavior
 * - Dependency Inversion: Depends on StockLevelIndicator abstraction
 *
 * @author University of Brighton Student
 * @version 2.0
 */
public class Product implements Comparable<Product> {
    private String proId;
    private String productName;          // NEW: Added product name field
    private String proDescription;
    private String proImageName;
    private double unitPrice;
    private int orderedQuantity = 1;
    private int stockQuantity;
    private int maxStockCapacity; // Track maximum stock for percentage calculation

    /**
     * Primary constructor used by DatabaseRW.
     * Automatically sets maxStockCapacity to initial stock quantity.
     *
     * @param id Product ID
     * @param name Product name
     * @param des Description of product
     * @param image Image name of product
     * @param aPrice The price of the product
     * @param stockQuantity The quantity of the product in stock
     */
    public Product(String id, String name, String des, String image, double aPrice, int stockQuantity) {
        this.proId = id;
        this.productName = name;
        this.proDescription = des;
        this.proImageName = image;
        this.unitPrice = aPrice;
        this.stockQuantity = stockQuantity;
        this.maxStockCapacity = stockQuantity; // Initialize max capacity
    }

    /**
     * Backward compatibility constructor (for existing code)
     * Uses description as product name
     */
    public Product(String id, String des, String image, double aPrice, int stockQuantity) {
        this(id, des, des, image, aPrice, stockQuantity); // Use description as both name and description
    }

    /**
     * Extended constructor with explicit max stock capacity.
     * Useful when restocking products with known maximum capacity.
     *
     * @param id Product ID
     * @param name Product name
     * @param des Description
     * @param image Image name
     * @param aPrice Price
     * @param stockQuantity Current stock
     * @param maxStockCapacity Maximum stock capacity
     */
    public Product(String id, String name, String des, String image, double aPrice,
                   int stockQuantity, int maxStockCapacity) {
        this.proId = id;
        this.productName = name;
        this.proDescription = des;
        this.proImageName = image;
        this.unitPrice = aPrice;
        this.stockQuantity = stockQuantity;
        this.maxStockCapacity = maxStockCapacity;
    }

    // ========== GETTERS ==========
    public String getProductId() { return proId; }
    public String getProductName() { return productName; }  // NEW: Added getter
    public String getProductDescription() { return proDescription; }
    public String getProductImageName() { return proImageName; }
    public double getUnitPrice() { return unitPrice; }
    public int getOrderedQuantity() { return orderedQuantity; }
    public int getStockQuantity() { return stockQuantity; }
    public int getMaxStockCapacity() { return maxStockCapacity; }

    // ========== SETTERS ==========
    public void setOrderedQuantity(int orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setMaxStockCapacity(int maxStockCapacity) {
        this.maxStockCapacity = maxStockCapacity;
    }

    public void setProductName(String productName) {  // NEW: Added setter
        this.productName = productName;
    }

    public void setProductDescription(String description) {
        this.proDescription = description;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setProImageName(String imageName) {
        this.proImageName = imageName;
    }

    // ========== STOCK LEVEL METHODS ==========

    /**
     * Gets the current stock level status.
     * Delegates to StockLevelIndicator for calculation.
     *
     * @return StockLevel enum (CRITICAL, LOW, MEDIUM, HIGH)
     */
    public StockLevel getStockLevel() {
        return StockLevelIndicator.determineStockLevel(stockQuantity, maxStockCapacity);
    }

    /**
     * Gets the stock percentage (0.0 to 1.0).
     *
     * @return Stock percentage as decimal
     */
    public double getStockPercentage() {
        return StockLevelIndicator.calculateStockPercentage(stockQuantity, maxStockCapacity);
    }

    /**
     * Gets formatted stock status text for display.
     *
     * @return Human-readable stock status
     */
    public String getStockStatusText() {
        StockLevel level = getStockLevel();
        return level.getDisplayText();
    }

    /**
     * Checks if product is low in stock (<=10% of capacity).
     *
     * @return true if stock is low, false otherwise
     */
    public boolean isLowStock() {
        return getStockLevel() == StockLevel.LOW ||
                getStockLevel() == StockLevel.CRITICAL;
    }

    /**
     * Gets JavaFX CSS style for stock level indicator.
     *
     * @return CSS style string
     */
    public String getStockLevelStyle() {
        return StockLevelIndicator.getStyleForLevel(getStockLevel());
    }

    /**
     * Gets console-formatted stock status message with color coding.
     *
     * @return Colored console output string
     */
    public String getColoredStockStatus() {
        StockLevel level = getStockLevel();
        String colorCode = StockLevelIndicator.getConsoleColorCode(level);
        String resetCode = StockLevelIndicator.resetConsoleColor();

        return String.format("%s%s%s (%d/%d units, %.1f%%)",
                colorCode,
                level.getDisplayText(),
                resetCode,
                stockQuantity,
                maxStockCapacity,
                getStockPercentage() * 100
        );
    }

    // ========== UTILITY METHODS ==========

    /**
     * Gets a short display string for lists
     * @return Short product display
     */
    public String getShortDisplay() {
        return String.format("%s - %s", proId, productName);
    }

    /**
     * Gets product details for summary display
     * @return Formatted product summary
     */
    public String getProductSummary() {
        return String.format("%s: %s (Stock: %d, Price: £%.2f)",
                proId, productName, stockQuantity, unitPrice);
    }

    // ========== EXISTING METHODS ==========

    @Override
    public int compareTo(Product otherProduct) {
        return this.proId.compareTo(otherProduct.proId);
    }

    @Override
    public String toString() {
        // Updated to include product name
        return String.format("ID: %s - %s, £%.2f/unit, stock: %d (%s)\nDescription: %s",
                proId,
                productName,
                unitPrice,
                stockQuantity,
                getStockStatusText(),
                proDescription
        );
    }

    /**
     * Extended toString with detailed stock information.
     *
     * @return Detailed product information including stock percentage
     */
    public String toDetailedString() {
        return String.format(
                "Product ID: %s\n" +
                        "Name: %s\n" +
                        "Description: %s\n" +
                        "Price: £%.2f per unit\n" +
                        "Stock: %d/%d units (%.1f%%)\n" +
                        "Status: %s\n" +
                        "Image: %s",
                proId,
                productName,
                proDescription,
                unitPrice,
                stockQuantity,
                maxStockCapacity,
                getStockPercentage() * 100,
                getStockStatusText(),
                proImageName
        );
    }

    /**
     * Clone method for creating copies of products
     * @return A new Product instance with the same values
     */
    public Product clone() {
        return new Product(proId, productName, proDescription, proImageName,
                unitPrice, stockQuantity, maxStockCapacity);
    }

    /**
     * Checks if two products have the same ID
     * @param other The other product to compare
     * @return true if IDs match
     */
    public boolean equalsById(Product other) {
        return this.proId.equals(other.proId);
    }
}