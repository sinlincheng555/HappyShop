package ci553.happyshop.catalogue;

/**
 * StockAllocation represents the breakdown of product stock into different states.
 *
 * Stock States:
 * - Total Stock: All units in warehouse (physical inventory)
 * - Reserved Stock: Units allocated to active orders (Ordered + Progressing)
 * - Available Stock: Units available for new purchases (Total - Reserved)
 *
 * Business Rules:
 * - Available Stock = Total Stock - Reserved Stock
 * - Customers can only purchase from Available Stock
 * - Reserved Stock is released when orders are Collected
 *
 * Design Pattern: Value Object
 * - Immutable representation of stock allocation state
 * - Ensures data consistency
 *
 * @author University of Brighton Student
 * @version 1.0
 */
public class StockAllocation {
    private final int totalStock;       // Physical inventory in warehouse
    private final int reservedStock;    // Stock in active orders (Ordered + Progressing)
    private final int availableStock;   // Stock available for purchase

    /**
     * Constructor calculates available stock automatically.
     *
     * @param totalStock Total physical inventory
     * @param reservedStock Stock allocated to active orders
     */
    public StockAllocation(int totalStock, int reservedStock) {
        this.totalStock = totalStock;
        this.reservedStock = reservedStock;
        this.availableStock = totalStock - reservedStock;
    }

    // ========== GETTERS ==========

    /**
     * Gets total physical stock in warehouse.
     * @return Total stock quantity
     */
    public int getTotalStock() {
        return totalStock;
    }

    /**
     * Gets stock reserved for active orders.
     * @return Reserved stock quantity
     */
    public int getReservedStock() {
        return reservedStock;
    }

    /**
     * Gets stock available for new purchases.
     * @return Available stock quantity
     */
    public int getAvailableStock() {
        return availableStock;
    }

    // ========== BUSINESS LOGIC ==========

    /**
     * Checks if requested quantity can be fulfilled.
     *
     * @param requestedQuantity Quantity customer wants to purchase
     * @return true if available stock >= requested quantity
     */
    public boolean canFulfillOrder(int requestedQuantity) {
        return availableStock >= requestedQuantity;
    }

    /**
     * Calculates percentage of stock that is available.
     *
     * @return Percentage (0.0 to 1.0)
     */
    public double getAvailablePercentage() {
        if (totalStock <= 0) return 0.0;
        return (double) availableStock / totalStock;
    }

    /**
     * Calculates percentage of stock that is reserved.
     *
     * @return Percentage (0.0 to 1.0)
     */
    public double getReservedPercentage() {
        if (totalStock <= 0) return 0.0;
        return (double) reservedStock / totalStock;
    }

    /**
     * Gets status message for warehouse staff.
     *
     * @return Human-readable status message
     */
    public String getStatusMessage() {
        if (availableStock <= 0) {
            return "❌ No stock available (All reserved)";
        } else if (getAvailablePercentage() <= 0.10) {
            return "🚨 Critical: Only " + availableStock + " units available";
        } else if (getAvailablePercentage() <= 0.30) {
            return "⚠️  Low availability: " + availableStock + " units available";
        } else {
            return "✅ " + availableStock + " units available for sale";
        }
    }

    /**
     * Creates a formatted breakdown string.
     *
     * @return Multi-line stock breakdown
     */
    public String getDetailedBreakdown() {
        return String.format(
                "📦 Stock Allocation:\n" +
                        "   Total Stock: %d units\n" +
                        "   Reserved:    %d units (%.1f%%)\n" +
                        "   Available:   %d units (%.1f%%)\n" +
                        "   Status:      %s",
                totalStock,
                reservedStock,
                getReservedPercentage() * 100,
                availableStock,
                getAvailablePercentage() * 100,
                getStatusMessage()
        );
    }

    @Override
    public String toString() {
        return String.format("Total: %d | Reserved: %d | Available: %d",
                totalStock, reservedStock, availableStock);
    }

    /**
     * Creates a compact single-line display.
     *
     * @return Compact format for UI display
     */
    public String toCompactString() {
        return String.format("%d in stock (%d reserved, %d available)",
                totalStock, reservedStock, availableStock);
    }
}