package ci553.happyshop.auth;

/**
 * User model class - Complete implementation
 */
public class User {
    private String username;
    private String passwordHash;
    private String email;
    private String fullName;
    private UserRole role;

    public User(String username, String passwordHash, String email, String fullName, UserRole role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public UserRole getRole() { return role; }

    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public boolean canAccessWarehouse() {
        return role != null && role.canAccessWarehouse();
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean canModifyPrices() {
        return role != null && role.canModifyPrices();
    }

    public boolean canDeleteProducts() {
        return role != null && role.canDeleteProducts();
    }

    public boolean canAddProducts() {
        return role != null && role.canAddProducts();
    }

    public boolean canUpdateStock() {
        return role != null && role.canUpdateStock();
    }

    public boolean canViewAllOrders() {
        return role != null && role.canViewAllOrders();
    }

    public boolean canManageUsers() {
        return role == UserRole.ADMIN;
    }

    @Override
    public String toString() {
        return String.format("User{username='%s', role=%s}", username, role);
    }
}