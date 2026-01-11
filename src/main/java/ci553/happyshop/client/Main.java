package ci553.happyshop.client;

import ci553.happyshop.client.customer.CustomerController;
import ci553.happyshop.client.customer.CustomerModel;
import ci553.happyshop.client.customer.CustomerView;
import ci553.happyshop.client.emergency.EmergencyExit;
import ci553.happyshop.client.orderTracker.OrderTracker;
import ci553.happyshop.client.picker.PickerController;
import ci553.happyshop.client.picker.PickerModel;
import ci553.happyshop.client.picker.PickerView;
import ci553.happyshop.client.warehouse.AlertSimulator;
import ci553.happyshop.client.warehouse.HistoryWindow;
import ci553.happyshop.client.warehouse.WarehouseController;
import ci553.happyshop.client.warehouse.WarehouseModel;
import ci553.happyshop.client.warehouse.WarehouseView;
import ci553.happyshop.orderManagement.OrderHub;
import ci553.happyshop.storageAccess.DatabaseRW;
import ci553.happyshop.storageAccess.DatabaseRWFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage window) {
        try {
            // Start essential system components first
            startOrderTracker();
            startPickerClient();
            initializeOrderMap();

            // Start UI clients
            startCustomerClient();
            startWarehouseClient();
            startEmergencyExit();

            window.setTitle("Happy Shop System");
            window.show();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start application", e);
            Platform.exit();
        }
    }

    private void startCustomerClient() {
        try {
            CustomerView cusView = new CustomerView();
            CustomerController cusController = new CustomerController();
            CustomerModel cusModel = new CustomerModel();
            DatabaseRW databaseRW = DatabaseRWFactory.createDatabaseRW();

            cusView.cusController = cusController;
            cusController.cusModel = cusModel;
            cusModel.cusView = cusView;
            cusModel.databaseRW = databaseRW;

            Stage customerStage = new Stage();
            customerStage.setTitle("Customer Interface");
            cusView.start(customerStage);

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to start customer client", e);
        }
    }

    private void startPickerClient() {
        try {
            PickerModel pickerModel = new PickerModel();
            PickerView pickerView = new PickerView();
            PickerController pickerController = new PickerController();

            pickerView.pickerController = pickerController;
            pickerController.pickerModel = pickerModel;
            pickerModel.pickerView = pickerView;
            pickerModel.registerWithOrderHub();

            Stage pickerStage = new Stage();
            pickerStage.setTitle("Picker Interface");
            pickerView.start(pickerStage);

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to start picker client", e);
        }
    }

    private void startOrderTracker() {
        try {
            OrderTracker orderTracker = new OrderTracker();
            orderTracker.registerWithOrderHub();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to start order tracker", e);
        }
    }

    private void initializeOrderMap() {
        try {
            OrderHub orderHub = OrderHub.getOrderHub();
            orderHub.initializeOrderMap();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to initialize order map", e);
        }
    }

    private void startWarehouseClient() {
        try {
            WarehouseView view = new WarehouseView();
            WarehouseController controller = new WarehouseController();
            WarehouseModel model = new WarehouseModel();
            DatabaseRW databaseRW = DatabaseRWFactory.createDatabaseRW();

            // Link controller, model, and view and start view
            view.controller = controller;
            controller.model = model;
            model.view = view;
            model.databaseRW = databaseRW;

            Stage warehouseStage = new Stage();
            warehouseStage.setTitle("Warehouse Management");
            view.start(warehouseStage);

            // Create dependent views that need window info
            HistoryWindow historyWindow = new HistoryWindow();
            AlertSimulator alertSimulator = new AlertSimulator();

            // Link after start
            model.historyWindow = historyWindow;
            model.alertSimulator = alertSimulator;
            historyWindow.warehouseView = view;
            alertSimulator.warehouseView = view;

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to start warehouse client", e);
        }
    }

    private void startEmergencyExit() {
        try {
            EmergencyExit.getEmergencyExit();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to start emergency exit", e);
        }
    }
}