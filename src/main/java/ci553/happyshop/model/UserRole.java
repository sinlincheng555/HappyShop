package ci553.happyshop.model;

/**
 * Enum representing different user roles in the HappyShop system
 * Each role has specific permissions and access levels
 *
 * Location: src/main/java/ci553/happyshop/model/UserRole.java
 */
public enum UserRole {
    CUSTOMER("Customer", 1),
    STAFF("Staff", 2),
    ADMIN("Administrator", 3);

    private final String displayName;
    private final int accessLevel;

    UserRole(String displayName, int accessLevel) {
        this.displayName = displayName;
        this.accessLevel = accessLevel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    /**
     * Check if this role has permission to access warehouse management
     */
    public boolean canAccessWarehouse() {
        return this == STAFF || this == ADMIN;
    }

    /**
     * Check if this role has full admin privileges
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Check if this role can modify product prices
     */
    public boolean canModifyPrices() {
        return this == ADMIN;
    }

    /**
     * Check if this role can delete products
     */
    public boolean canDeleteProducts() {
        return this == ADMIN;
    }

    /**
     * Check if this role can add new products
     */
    public boolean canAddProducts() {
        return this == STAFF || this == ADMIN;
    }

    /**
     * Check if this role can update stock levels
     */
    public boolean canUpdateStock() {
        return this == STAFF || this == ADMIN;
    }

    /**
     * Check if this role can view all orders
     */
    public boolean canViewAllOrders() {
        return this == STAFF || this == ADMIN;
    }

    /**
     * Check if this role can manage users
     */
    public boolean canManageUsers() {
        return this == ADMIN;
    }
}