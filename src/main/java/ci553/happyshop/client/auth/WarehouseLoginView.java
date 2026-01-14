package ci553.happyshop.client.auth;

import ci553.happyshop.auth.AuthenticationManager;
import ci553.happyshop.auth.User;
import ci553.happyshop.auth.UserRole;
import ci553.happyshop.client.customer.CustomerClient;
import ci553.happyshop.client.warehouse.WarehouseClient;
import ci553.happyshop.client.emergency.EmergencyExit;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Warehouse login view with staff/admin role selection.
 *
 * Features:
 * - Staff login (warehouse access with restrictions)
 * - Admin login (full access + emergency controls)
 * - Role-based access validation
 *
 * Default Accounts:
 * - Admin: username=admin, password=admin123
 * - Staff: Create via admin panel
 *
 * @author HappyShop Development Team
 * @version 1.0
 */
public class WarehouseLoginView {

    public LoginView loginView;
    public LoginController loginController;

    private Stage stage;
    private TextField tfUsername;
    private PasswordField pfPassword;
    private TextField tfPasswordVisible;
    private CheckBox cbShowPassword;
    private Label lblMessage;
    private Button btnLogin;
    private ToggleGroup roleGroup;

    private final int WIDTH = 500;
    private final int HEIGHT = 700;
    private final String PRIMARY_COLOR = "#7C3AED";
    private final String ERROR_COLOR = "#FF3B30";
    private final String SUCCESS_COLOR = "#34C759";
    private final String BACKGROUND_COLOR = "#F8F9FA";

    public void start(Stage primaryStage) {
        this.stage = primaryStage;

        VBox root = createWarehouseLoginView();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setScene(scene);
    }

    private VBox createWarehouseLoginView() {
        VBox container = new VBox(25);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(40));
        container.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        // Back button
        Button btnBack = new Button("← Back");
        btnBack.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-size: 14px; -fx-cursor: hand;");
        btnBack.setOnAction(e -> {
            if (loginView != null) {
                loginView.start(stage);
            }
        });
        HBox backContainer = new HBox(btnBack);
        backContainer.setAlignment(Pos.TOP_LEFT);

        // Login card
        VBox loginCard = new VBox(20);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setPadding(new Insets(40));
        loginCard.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 15px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        loginCard.setMaxWidth(400);

        // Header
        Label title = new Label("Warehouse Login");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_COLOR + ";");

        Label subtitle = new Label("Select your role and sign in");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        // Role selection
        VBox roleBox = createRoleSelection();

        // Username field
        VBox usernameBox = createInputField("Username");
        tfUsername = (TextField) usernameBox.getChildren().get(1);

        // Password field
        VBox passwordBox = createPasswordField();

        // Message label
        lblMessage = new Label("");
        lblMessage.setStyle("-fx-text-fill: " + ERROR_COLOR + "; -fx-font-size: 13px; -fx-wrap-text: true;");
        lblMessage.setMaxWidth(350);
        lblMessage.setWrapText(true);
        lblMessage.setVisible(false);

        // Login button
        btnLogin = new Button("Login to Warehouse");
        btnLogin.setStyle(getButtonStyle(PRIMARY_COLOR));
        btnLogin.setPrefWidth(350);
        btnLogin.setOnAction(e -> handleWarehouseLogin());
        setupButtonHover(btnLogin, PRIMARY_COLOR);

        // Info box
        VBox infoBox = createInfoBox();

        loginCard.getChildren().addAll(
                title, subtitle, roleBox, usernameBox, passwordBox, lblMessage, btnLogin, infoBox
        );

        container.getChildren().addAll(backContainer, loginCard);
        return container;
    }

    private VBox createRoleSelection() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);

        Label label = new Label("Select Role");
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");

        roleGroup = new ToggleGroup();

        HBox rolesBox = new HBox(20);
        rolesBox.setAlignment(Pos.CENTER);

        // Staff card
        VBox staffCard = createRoleCard(
                "🧑‍💼 Staff",
                "Manage inventory\nand stock levels",
                UserRole.STAFF,
                "#3B82F6"
        );

        // Admin card
        VBox adminCard = createRoleCard(
                "👑 Admin",
                "Full system access\nand controls",
                UserRole.ADMIN,
                "#EF4444"
        );

        rolesBox.getChildren().addAll(staffCard, adminCard);

        box.getChildren().addAll(label, rolesBox);
        return box;
    }

    private VBox createRoleCard(String title, String description, UserRole role, String color) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #DDD; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-cursor: hand;"
        );
        card.setPrefWidth(160);
        card.setPrefHeight(110);

        RadioButton radioButton = new RadioButton();
        radioButton.setToggleGroup(roleGroup);
        radioButton.setUserData(role);
        radioButton.setVisible(false);

        // Select staff by default
        if (role == UserRole.STAFF) {
            radioButton.setSelected(true);
        }

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666; -fx-text-alignment: center;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(140);

        card.getChildren().addAll(radioButton, titleLabel, descLabel);

        // Click to select
        card.setOnMouseClicked(e -> {
            radioButton.setSelected(true);
            updateRoleCardStyles();
        });

        // Store card reference in radio button
        radioButton.setUserData(role);
        radioButton.getProperties().put("card", card);

        // Initial style update
        updateRoleCardStyle(card, radioButton.isSelected(), color);

        return card;
    }

    private void updateRoleCardStyles() {
        for (Toggle toggle : roleGroup.getToggles()) {
            RadioButton rb = (RadioButton) toggle;
            VBox card = (VBox) rb.getProperties().get("card");
            UserRole role = (UserRole) rb.getUserData();
            String color = role == UserRole.ADMIN ? "#EF4444" : "#3B82F6";
            updateRoleCardStyle(card, rb.isSelected(), color);
        }
    }

    private void updateRoleCardStyle(VBox card, boolean selected, String color) {
        if (selected) {
            card.setStyle(
                    "-fx-background-color: " + color + "15; " +
                            "-fx-border-color: " + color + "; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: dropshadow(gaussian, " + color + "66, 8, 0, 0, 2);"
            );
        } else {
            card.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-border-color: #DDD; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: 10px; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-cursor: hand;"
            );
        }
    }

    private VBox createInfoBox() {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setStyle(
                "-fx-background-color: #FFF3CD; " +
                        "-fx-border-color: #FFE69C; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-background-radius: 8px;"
        );
        box.setMaxWidth(350);

        Label titleLabel = new Label("ℹ️ Default Admin Credentials");
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #856404;");

        Label infoLabel = new Label(
                "Username: admin\n" +
                        "Password: admin123\n\n" +
                        "⚠️ Please change after first login"
        );
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #856404; -fx-text-alignment: center;");
        infoLabel.setWrapText(true);

        box.getChildren().addAll(titleLabel, infoLabel);
        return box;
    }

    private void handleWarehouseLogin() {
        String username = tfUsername.getText().trim();
        String password = cbShowPassword.isSelected() ? tfPasswordVisible.getText() : pfPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please enter username and password", true);
            return;
        }

        // Get selected role
        Toggle selectedToggle = roleGroup.getSelectedToggle();
        if (selectedToggle == null) {
            showMessage("Please select a role", true);
            return;
        }

        UserRole expectedRole = (UserRole) selectedToggle.getUserData();

        btnLogin.setDisable(true);
        btnLogin.setText("Logging in...");

        // Perform login in background
        new Thread(() -> {
            try {
                loginController.doLogin(username, password);

                // Validate role after successful login
                Platform.runLater(() -> {
                    User loggedInUser = AuthenticationManager.getInstance().getCurrentUser();

                    if (loggedInUser != null) {
                        // Check if role matches
                        if (loggedInUser.getRole() != expectedRole) {
                            showMessage(
                                    "Access denied. This account does not have " + expectedRole.getDisplayName() + " privileges.",
                                    true
                            );
                            AuthenticationManager.getInstance().logout();
                        } else if (!loggedInUser.canAccessWarehouse()) {
                            showMessage("This account cannot access warehouse management.", true);
                            AuthenticationManager.getInstance().logout();
                        }
                    }
                });
            } finally {
                Platform.runLater(() -> {
                    btnLogin.setDisable(false);
                    btnLogin.setText("Login to Warehouse");
                });
            }
        }).start();
    }

    private VBox createInputField(String label) {
        VBox box = new VBox(5);

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");

        TextField field = new TextField();
        field.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 12px; " +
                        "-fx-background-color: " + BACKGROUND_COLOR + "; " +
                        "-fx-border-color: #DDD; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-background-radius: 8px;"
        );
        field.setPrefWidth(350);

        box.getChildren().addAll(lbl, field);
        return box;
    }

    private VBox createPasswordField() {
        VBox box = new VBox(5);

        Label lbl = new Label("Password");
        lbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");

        pfPassword = new PasswordField();
        pfPassword.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 12px; " +
                        "-fx-background-color: " + BACKGROUND_COLOR + "; " +
                        "-fx-border-color: #DDD; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-background-radius: 8px;"
        );
        pfPassword.setPrefWidth(350);

        tfPasswordVisible = new TextField();
        tfPasswordVisible.setStyle(pfPassword.getStyle());
        tfPasswordVisible.setPrefWidth(350);
        tfPasswordVisible.setVisible(false);
        tfPasswordVisible.setManaged(false);
        pfPassword.textProperty().bindBidirectional(tfPasswordVisible.textProperty());

        cbShowPassword = new CheckBox("Show password");
        cbShowPassword.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        cbShowPassword.setOnAction(e -> togglePasswordVisibility());

        StackPane passwordStack = new StackPane(pfPassword, tfPasswordVisible);
        passwordStack.setAlignment(Pos.CENTER_LEFT);

        box.getChildren().addAll(lbl, passwordStack, cbShowPassword);
        return box;
    }

    private void togglePasswordVisibility() {
        boolean show = cbShowPassword.isSelected();
        pfPassword.setVisible(!show);
        pfPassword.setManaged(!show);
        tfPasswordVisible.setVisible(show);
        tfPasswordVisible.setManaged(show);
    }

    private void showMessage(String message, boolean isError) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: " + (isError ? ERROR_COLOR : SUCCESS_COLOR) + "; -fx-font-size: 13px; -fx-wrap-text: true;");
        lblMessage.setVisible(true);
    }

    private String getButtonStyle(String color) {
        return String.format(
                "-fx-background-color: %s; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 15px 30px; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);",
                color
        );
    }

    private void setupButtonHover(Button button, String baseColor) {
        String originalStyle = button.getStyle();
        String hoverColor = "#6D28D9"; // Darker purple

        button.setOnMouseEntered(e ->
                button.setStyle(originalStyle.replace(baseColor, hoverColor))
        );

        button.setOnMouseExited(e -> button.setStyle(originalStyle));
    }
}