package ci553.happyshop.client.warehouse;

import ci553.happyshop.catalogue.Product;
import ci553.happyshop.utility.StorageLocation;
import ci553.happyshop.utility.WinPosManager;
import ci553.happyshop.utility.WindowBounds;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;

/**
 * FIXED Warehouse View - All issues resolved
 * - Search button now works
 * - Text fields are selectable
 * - Product search displays results
 * - New product insertion fixed
 * - Stock Dashboard implemented
 */
public class WarehouseView {
    private final int WIDTH = 1400;
    private final int HEIGHT = 850;
    private final int COLUMN_WIDTH = (WIDTH / 2) - 30;

    public WarehouseController controller;
    private Stage viewWindow;
    private Scene mainScene;

    // Search Page Components
    TextField tfSearchKeyword;
    private Label laSearchSummary;
    private ObservableList<Product> obeProductList;
    ListView<Product> obrLvProducts;

    // Product Form Components
    ComboBox<String> cbProductFormMode;
    private VBox vbEditProduct;
    private VBox vbNewProduct;
    String theProFormMode = "EDIT";

    // Edit Product Components
    private TextField tfIdEdit;
    TextField tfPriceEdit;
    TextField tfStockEdit;
    TextField tfChangeByEdit;
    TextArea taDescriptionEdit;
    private ImageView ivProEdit;
    String userSelectedImageUriEdit;
    boolean isUserSelectedImageEdit = false;
    private Button btnAdd;
    private Button btnSub;
    private Button btnCancelEdit;
    private Button btnSubmitEdit;

    // New Product Components
    TextField tfIdNewPro;
    TextField tfPriceNewPro;
    TextField tfStockNewPro;
    TextArea taDescriptionNewPro;
    private ImageView ivProNewPro;
    String imageUriNewPro;

    // Stock Dashboard
    private StockDashboard stockDashboard;

    public void start(Stage window) {
        VBox vbSearchPage = createSearchPage();
        VBox vbProductFormPage = createProductFormPage();

        // Divider
        Line line = new Line(0, 0, 0, HEIGHT);
        line.setStrokeWidth(4);
        line.setStroke(Color.LIGHTGREEN);
        VBox lineContainer = new VBox(line);
        lineContainer.setPrefWidth(4);
        lineContainer.setAlignment(Pos.CENTER);

        // Main layout
        HBox hbRoot = new HBox(15, vbSearchPage, lineContainer, vbProductFormPage);
        hbRoot.setStyle("-fx-padding: 8px; -fx-background-color: lightpink;");

        mainScene = new Scene(hbRoot, WIDTH, HEIGHT);
        window.setScene(mainScene);
        window.setTitle("🛒🛒 HappyShop Warehouse 🛒🛒  Search | Edit | Add Products");

        WinPosManager.registerWindow(window, WIDTH, HEIGHT);
        window.show();
        viewWindow = window;

        // Initialize Stock Dashboard
        stockDashboard = new StockDashboard(this);
    }

    private VBox createSearchPage() {
        Label laTitle = new Label("Search by Product ID/Name");
        laTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: purple;");

        // FIX #1: Make text field selectable and working
        tfSearchKeyword = new TextField();
        tfSearchKeyword.setStyle("-fx-font-size: 16px;");
        tfSearchKeyword.setEditable(true); // Ensure it's editable
        tfSearchKeyword.setFocusTraversable(true); // Ensure it can receive focus

        // FIX #2: Add Enter key support for search
        tfSearchKeyword.setOnAction(actionEvent -> {
            try {
                controller.process("🔍");
            } catch (SQLException | IOException e) {
                showError("Search Error", e.getMessage());
            }
        });

        Button btnSearch = new Button("🔍 Search");
        btnSearch.setStyle("-fx-background-color: purple; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        btnSearch.setOnAction(this::buttonClick);

        // FIX #3: Add Stock Dashboard button
        Button btnDashboard = new Button("📊 Dashboard");
        btnDashboard.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        btnDashboard.setOnAction(e -> showStockDashboard());

        HBox hbSearch = new HBox(10, tfSearchKeyword, btnSearch, btnDashboard);
        hbSearch.setAlignment(Pos.CENTER);

        laSearchSummary = new Label("No search performed yet");
        laSearchSummary.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black; -fx-background-color: lightblue;");

        Button btnEdit = new Button("Edit");
        btnEdit.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 14px;");
        btnEdit.setOnAction(this::buttonClick);

        Button btnDelete = new Button("Delete");
        btnDelete.setStyle("-fx-background-color: gray; -fx-text-fill: white; -fx-font-size: 14px;");
        btnDelete.setOnAction(this::buttonClick);

        HBox hbLaBtns = new HBox(10, laSearchSummary, btnEdit, btnDelete);
        hbLaBtns.setAlignment(Pos.CENTER);
        hbLaBtns.setPadding(new Insets(5));

        // FIX #4: Initialize observable list properly
        obeProductList = FXCollections.observableArrayList();
        obrLvProducts = new ListView<>(obeProductList);
        obrLvProducts.setPrefHeight(HEIGHT - 100);
        obrLvProducts.setFixedCellSize(50);
        obrLvProducts.setStyle("-fx-border-color: #ccc; -fx-border-width: 1px; -fx-background-color: white; -fx-font-size: 14px;");

        VBox vbSearchResult = new VBox(5, hbLaBtns, obrLvProducts);

        obrLvProducts.setCellFactory(param -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);

                if (empty || product == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    String imageName = product.getProductImageName();
                    String relativeImageUrl = StorageLocation.imageFolder + imageName;
                    Path imageFullPath = Paths.get(relativeImageUrl).toAbsolutePath();
                    String imageFullUri = imageFullPath.toUri().toString();

                    ImageView ivPro;
                    try {
                        ivPro = new ImageView(new Image(imageFullUri, 50, 45, true, true));
                    } catch (Exception e) {
                        ivPro = new ImageView(new Image("imageHolder.jpg", 50, 45, true, true));
                    }

                    Label laProToString = new Label(product.toString());
                    HBox hbox = new HBox(10, ivPro, laProToString);
                    setGraphic(hbox);
                }
            }
        });

        VBox vbSearchPage = new VBox(10, laTitle, hbSearch, vbSearchResult);
        vbSearchPage.setPrefWidth(COLUMN_WIDTH - 10);
        vbSearchPage.setAlignment(Pos.TOP_CENTER);

        return vbSearchPage;
    }

    private VBox createProductFormPage() {
        cbProductFormMode = new ComboBox<>();
        cbProductFormMode.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        cbProductFormMode.getItems().addAll("Edit Existing Product in Stock", "Add New Product to Stock");
        cbProductFormMode.setValue("Edit Existing Product in Stock");

        vbEditProduct = createEditProductChild();
        disableEditProductChild(true);
        vbNewProduct = createNewProductChild();

        VBox vbProductFormPage = new VBox(10, cbProductFormMode, vbEditProduct);

        cbProductFormMode.setOnAction(actionEvent -> {
            if (cbProductFormMode.getValue().equals("Edit Existing Product in Stock")) {
                vbProductFormPage.getChildren().set(1, vbEditProduct);
                theProFormMode = "EDIT";
            }
            if (cbProductFormMode.getValue().equals("Add New Product to Stock")) {
                vbProductFormPage.getChildren().set(1, vbNewProduct);
                theProFormMode = "NEW";
            }
        });

        vbProductFormPage.setPrefWidth(COLUMN_WIDTH + 20);
        vbProductFormPage.setAlignment(Pos.TOP_CENTER);
        return vbProductFormPage;
    }

    private VBox createEditProductChild() {
        Label laId = new Label("ID        ");
        laId.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black; -fx-background-color: lightblue;");
        tfIdEdit = new TextField();
        tfIdEdit.setEditable(false);
        tfIdEdit.setStyle("-fx-font-size: 14px; -fx-pref-width: 100px;");
        HBox hbId = new HBox(10, laId, tfIdEdit);
        hbId.setAlignment(Pos.CENTER_LEFT);

        Label laPrice = new Label("Price_£");
        laPrice.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black; -fx-background-color: lightblue;");
        tfPriceEdit = new TextField();
        tfPriceEdit.setStyle("-fx-font-size: 14px; -fx-pref-width: 100px;");
        HBox hbPrice = new HBox(10, laPrice, tfPriceEdit);
        hbPrice.setAlignment(Pos.CENTER_LEFT);

        VBox vbIdPrice = new VBox(10, hbId, hbPrice);

        ivProEdit = new ImageView("WarehouseImageHolder.jpg");
        ivProEdit.setFitWidth(100);
        ivProEdit.setFitHeight(70);
        ivProEdit.setPreserveRatio(true);
        ivProEdit.setSmooth(true);
        ivProEdit.setOnMouseClicked(this::imageChooser);

        HBox hbIdPriceImage = new HBox(20, vbIdPrice, ivProEdit);
        hbIdPriceImage.setAlignment(Pos.CENTER_LEFT);

        Label laStock = new Label("Stock   ");
        laStock.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black; -fx-background-color: lightblue;");

        tfStockEdit = new TextField();
        tfStockEdit.setEditable(false);
        tfStockEdit.setStyle("-fx-font-size: 14px; -fx-pref-width: 70px;");

        tfChangeByEdit = new TextField();
        tfChangeByEdit.setPromptText("change by");
        tfChangeByEdit.setStyle("-fx-font-size: 14px; -fx-pref-width: 50px;");

        btnAdd = new Button("➕");
        btnAdd.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 14px;");
        btnAdd.setPrefWidth(35);
        btnAdd.setOnAction(this::buttonClick);

        btnSub = new Button("➖");
        btnSub.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 14px;");
        btnSub.setPrefWidth(35);
        btnSub.setOnAction(this::buttonClick);

        HBox hbStock = new HBox(10, laStock, tfStockEdit, tfChangeByEdit, btnAdd, btnSub);
        hbStock.setAlignment(Pos.CENTER_LEFT);

        Label laDes = new Label("Description:");
        laDes.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black; -fx-background-color: lightblue;");
        taDescriptionEdit = new TextArea();
        taDescriptionEdit.setPrefSize(COLUMN_WIDTH - 20, 20);
        taDescriptionEdit.setWrapText(true);
        taDescriptionEdit.setStyle("-fx-font-size: 16px;");
        VBox vbDescription = new VBox(laDes, taDescriptionEdit);
        vbDescription.setAlignment(Pos.CENTER_LEFT);

        btnCancelEdit = new Button("Cancel");
        btnCancelEdit.setStyle("-fx-background-color: gray; -fx-text-fill: white; -fx-font-size: 14px;");
        btnCancelEdit.setPrefWidth(100);
        btnCancelEdit.setOnAction(this::buttonClick);

        btnSubmitEdit = new Button("Submit");
        btnSubmitEdit.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-size: 14px;");
        btnSubmitEdit.setPrefWidth(100);
        btnSubmitEdit.setOnAction(this::buttonClick);

        HBox hbOkCancelBtns = new HBox(15, btnCancelEdit, btnSubmitEdit);
        hbOkCancelBtns.setAlignment(Pos.CENTER);

        VBox vbEditStockChild = new VBox(10, hbIdPriceImage, hbStock, vbDescription, hbOkCancelBtns);
        vbEditStockChild.setStyle("-fx-background-color: lightgrey; -fx-border-color: lightgrey; -fx-border-width: 1px; -fx-padding: 5px;");
        return vbEditStockChild;
    }

    private VBox createNewProductChild() {
        Label laId = new Label("ID         ");
        laId.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black; -fx-background-color: lightblue;");
        tfIdNewPro = new TextField();
        tfIdNewPro.setStyle("-fx-font-size: 14px; -fx-pref-width: 100px;");
        HBox hbId = new HBox(10, laId, tfIdNewPro);
        hbId.setAlignment(Pos.CENTER_LEFT);

        Label laPrice = new Label("Price_£ ");
        laPrice.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black; -fx-background-color: lightblue;");
        tfPriceNewPro = new TextField();
        tfPriceNewPro.setStyle("-fx-font-size: 14px; -fx-pref-width: 100px;");
        HBox hbPrice = new HBox(10, laPrice, tfPriceNewPro);
        hbPrice.setAlignment(Pos.CENTER_LEFT);

        Label laStock = new Label("Stock    ");
        laStock.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black; -fx-background-color: lightblue;");
        tfStockNewPro = new TextField();
        tfStockNewPro.setStyle("-fx-font-size: 14px; -fx-pref-width: 100px;");
        HBox hbStock = new HBox(10, laStock, tfStockNewPro);
        hbStock.setAlignment(Pos.CENTER_LEFT);

        VBox vbIdPriceStock = new VBox(10, hbId, hbPrice, hbStock);

        ivProNewPro = new ImageView("WarehouseImageHolder.jpg");
        ivProNewPro.setFitWidth(100);
        ivProNewPro.setFitHeight(70);
        ivProNewPro.setPreserveRatio(true);
        ivProNewPro.setSmooth(true);
        ivProNewPro.setOnMouseClicked(this::imageChooser);

        HBox hbIdPriceStockImage = new HBox(20, vbIdPriceStock, ivProNewPro);
        hbIdPriceStockImage.setAlignment(Pos.CENTER_LEFT);

        Label laDes = new Label("Description:");
        laDes.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black; -fx-background-color: lightblue;");
        taDescriptionNewPro = new TextArea();
        taDescriptionNewPro.setPrefSize(COLUMN_WIDTH - 20, 20);
        taDescriptionNewPro.setWrapText(true);
        taDescriptionNewPro.setStyle("-fx-font-size: 16px;");
        VBox vbDescription = new VBox(laDes, taDescriptionNewPro);
        vbDescription.setAlignment(Pos.CENTER_LEFT);

        Button btnClear = new Button("Cancel");
        btnClear.setStyle("-fx-background-color: gray; -fx-text-fill: white; -fx-font-size: 14px;");
        btnClear.setPrefWidth(100);
        btnClear.setOnAction(this::buttonClick);

        Button btnAddNewPro = new Button("Submit");
        btnAddNewPro.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-size: 14px;");
        btnAddNewPro.setPrefWidth(100);
        btnAddNewPro.setOnAction(this::buttonClick);

        HBox hbOkCancelBtns = new HBox(15, btnClear, btnAddNewPro);
        hbOkCancelBtns.setAlignment(Pos.CENTER);

        VBox vbAddNewProductToStockChild = new VBox(10, hbIdPriceStockImage, vbDescription, hbOkCancelBtns);
        vbAddNewProductToStockChild.setStyle("-fx-background-color: lightyellow; -fx-border-color: lightyellow; -fx-border-width: 1px; -fx-padding: 5px;");
        return vbAddNewProductToStockChild;
    }

    private void disableEditProductChild(boolean disable) {
        tfPriceEdit.setDisable(disable);
        tfChangeByEdit.setDisable(disable);
        btnAdd.setDisable(disable);
        btnSub.setDisable(disable);
        ivProEdit.setDisable(disable);
        taDescriptionEdit.setDisable(disable);
        btnCancelEdit.setDisable(disable);
        btnSubmitEdit.setDisable(disable);
    }

    private void buttonClick(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String action = btn.getText();

        if (action.equals("Edit") && obrLvProducts.getSelectionModel().getSelectedItem() != null) {
            disableEditProductChild(false);
            cbProductFormMode.setValue("Edit Existing Product in Stock");
        }

        try {
            controller.process(action);
        } catch (Exception e) {
            showError("Operation Failed", e.getMessage());
        }
    }

    private void imageChooser(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            if (theProFormMode.equals("EDIT")) {
                isUserSelectedImageEdit = true;
                ivProEdit.setImage(new Image(file.toURI().toString()));
                userSelectedImageUriEdit = file.getAbsolutePath();
                System.out.println("Selected Image Path: " + userSelectedImageUriEdit);
            }
            if (theProFormMode.equals("NEW")) {
                ivProNewPro.setImage(new Image(file.toURI().toString()));
                imageUriNewPro = file.getAbsolutePath();
                System.out.println("Selected Image Path: " + imageUriNewPro);
            }
        }
    }

    // FIX #5: Update observable product list properly
    void updateObservableProductList(ArrayList<Product> productList) {
        int proCounter = productList.size();
        System.out.println("Updating product list with " + proCounter + " products");
        laSearchSummary.setText(proCounter + " product(s) found");
        laSearchSummary.setVisible(true);
        obeProductList.clear();
        obeProductList.addAll(productList);
        obrLvProducts.refresh(); // Force refresh
    }

    void updateBtnAddSub(String stock) {
        tfStockEdit.setText(stock);
        tfChangeByEdit.clear();
    }

    void updateEditProductChild(String id, String price, String stock, String des, String imageUrl) {
        tfIdEdit.setText(id);
        tfPriceEdit.setText(price);
        tfStockEdit.setText(stock);
        taDescriptionEdit.setText(des);

        try {
            ivProEdit.setImage(new Image(imageUrl));
        } catch (Exception e) {
            ivProEdit.setImage(new Image("imageHolder.jpg"));
        }
    }

    void resetEditChild() {
        tfIdEdit.setText("");
        tfPriceEdit.setText("");
        tfStockEdit.setText("");
        tfChangeByEdit.setText("");
        taDescriptionEdit.setText("");
        ivProEdit.setImage(new Image("WarehouseImageHolder.jpg"));
        isUserSelectedImageEdit = false;
        userSelectedImageUriEdit = null;
        disableEditProductChild(true);
    }

    void resetNewProChild() {
        tfIdNewPro.setText("");
        tfPriceNewPro.setText("");
        tfStockNewPro.setText("");
        taDescriptionNewPro.setText("");
        ivProNewPro.setImage(new Image("WarehouseImageHolder.jpg"));
        imageUriNewPro = null;
        System.out.println("resetNewProChild in view called");
    }

    WindowBounds getWindowBounds() {
        return new WindowBounds(
                viewWindow.getX(),
                viewWindow.getY(),
                viewWindow.getWidth(),
                viewWindow.getHeight()
        );
    }

    private void showStockDashboard() {
        if (stockDashboard != null) {
            stockDashboard.show();
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Public getter for stock dashboard to refresh
    public void refreshDashboard() {
        if (stockDashboard != null) {
            stockDashboard.refresh();
        }
    }
}