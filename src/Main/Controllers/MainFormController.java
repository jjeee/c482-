package Controllers;
/*
Jae Jee
C842
 */

import Model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
    Controller for the main form that the user will initially land on when starting the application.

    Provides the functions of adding/modifying/deleting a part or product.
    @author Jae Jee
 */

public class MainFormController implements Initializable {
    private static Part part;

    private static Product product;

    private static ObservableList<Part> partFound = FXCollections.observableArrayList();

    private static ObservableList<Part> partTmp = FXCollections.observableArrayList();
    private static ObservableList<Product> productFound = FXCollections.observableArrayList();
    private static ObservableList<Product> productTmp = FXCollections.observableArrayList();

    Inventory inventory;

    /**
     * This is the table that will show all the parts in the inventory.
     */
    @FXML private TableView<Part> mainPartsTable;

    /**
     * This text box is where the user will enter either the name or id to find a part.
     */
    @FXML private TextField partsSearchBar;

    /**
     * This is the table that will show all the products in the inventory.
     */

    @FXML private TableView<Product> mainProductsTable;

    /**
     * This is the text box where the user will enter either the name or id to find a product.
     */
    @FXML private TextField productsSearchBar;

    @FXML
    private TableColumn<Part, Integer> partsTableID;

    @FXML
    private TableColumn<Part, Integer> partsTableInv;

    @FXML
    private TableColumn<Part, String> partsTableName;

    @FXML
    private TableColumn<Part, Double> partsTablePrice;

    @FXML
    private TableColumn<Product, Integer> productTableID;

    @FXML
    private TableColumn<Product, Integer> productTableInv;

    @FXML
    private TableColumn<Product, String> productTableName;

    @FXML
    private TableColumn<Product, Double> productTablePrice;

    /**
     * This function creates a new scene where the user can add a new part.
     * @param event The event of the user clicking the button.
     * @throws IOException The exception that can occur when creating a new scene.
     */
    @FXML private void partsAdd(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/Views/add-part-form.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Add Part Form");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * This function creates a new scene where the user can modify an existing part.
     * @param event The event of the user clicking the button.
     * @throws IOException The exception that can occur when creating a new scene.
     */
    @FXML private void partsModify(ActionEvent event) throws IOException {
        if (mainPartsTable.getSelectionModel().getSelectedItem() != null){
            part = mainPartsTable.getSelectionModel().getSelectedItem();

            Parent parent = FXMLLoader.load(getClass().getResource("/Views/modify-part-form.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Modify Part Form");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();}

        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid part to modify");
        }
    }

    /**
     * This button method allows the user to delete an existing part.
     * There will also be a confirmation required before the part is deleted.
     * @param event The event of the user clicking the button.
     */
    @FXML private void partsDelete(ActionEvent event) {
        if (mainPartsTable.getSelectionModel().getSelectedItem() != null)
            if (!inventory.getAllParts().isEmpty()) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Delete Part");
                confirm.setHeaderText("Are you sure you want to delete " + mainPartsTable.getSelectionModel().getSelectedItem().getName());
                confirm.setContentText("Please confirm");

                Optional<ButtonType> result = confirm.showAndWait();
                if (result.get() == ButtonType.OK) {
                    inventory.deletePart(mainPartsTable.getSelectionModel().getSelectedItem());
                    mainPartsTable.refresh();
                }
            }
    }

    /**
     * This method dynamically searches for a part through the text box either by partial match of the name or match of the id.
     * Will replace all entries with those that match the search criteria.
     * Will display all entries if the text box is empty.
     * @param event The event of the user typing either letter or number.
     */
    public void partsSearch(KeyEvent event) {
        partFound.clear();
        if (partsSearchBar.getText().trim().length() != 0 || partsSearchBar != null) {
           if (testInt(partsSearchBar.getText().trim())) {
                for (Part tmp1 : partTmp)
                    if (tmp1.getId() == Integer.parseInt(partsSearchBar.getText().trim()))
                        partFound.add(tmp1);
            }
            else {
                for (Part tmp2 : partTmp)
                    if (tmp2.getName().toLowerCase().contains(partsSearchBar.getText().trim().toLowerCase()))
                        partFound.add(tmp2);
            }

            mainPartsTable.setItems(partFound);
            mainPartsTable.refresh();
        } else {
            mainPartsTable.setItems(inventory.getAllParts());
            partFound.clear();
            mainPartsTable.refresh();
        }
    }

    /**
     * This function creates a new scene where the user can add a new product.
     * @param event The event of the user clicking the button.
     * @throws IOException The exception that can occur when creating a new scene.
     */
    @FXML private void productsAdd(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/Views/add-product-form.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Add Product Form");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * This function creates a new scene where the user can modify an existing product.
     * @param event The event of the user clicking the button.
     * @throws IOException The exception that can occur when creating a new scene.
     */
    @FXML private void productsModify(ActionEvent event) throws IOException {
        if (mainProductsTable.getSelectionModel().getSelectedItem() != null){
            product = mainProductsTable.getSelectionModel().getSelectedItem();

            Parent parent = FXMLLoader.load(getClass().getResource("/Views/modify-product-form.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Modify Product Form");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();}

        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid product to modify");
        }
    }

    /**
     * This button method allows the user to delete an existing product.
     * There will also be a confirmation required before the part is deleted.
     * @param event The event of the user clicking the button.
     */
    @FXML private void productsDelete(ActionEvent event) {
        if (mainProductsTable.getSelectionModel().getSelectedItem() != null)
            if (!inventory.getAllProducts().isEmpty()) {
                if (mainProductsTable.getSelectionModel().getSelectedItem().getAllAssociatedParts() != null){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Product Deletion Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Cannot delete a product with parts associated");
                    alert.show();
                } else {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Delete Product");
                    confirm.setHeaderText("Are you sure you want to delete " + mainProductsTable.getSelectionModel().getSelectedItem().getName());
                    confirm.setContentText("Please confirm");

                    Optional<ButtonType> result = confirm.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        inventory.deleteProduct(mainProductsTable.getSelectionModel().getSelectedItem());
                        mainProductsTable.refresh();
                    }
                }
            }
    }

    /**
     * This method dynamically searches for a product through the text box either by partial match of the name or match of the id.
     * @param event The event of the user typing either letter or number.
     */
    @FXML private void productsSearch(KeyEvent event) {
        productFound.clear();
        if (productsSearchBar.getText().trim().length() != 0 || productsSearchBar != null) {
           if (testInt(productsSearchBar.getText().trim())) {
                for (Product tmp1 : productTmp)
                    if (tmp1.getId() == Integer.parseInt(productsSearchBar.getText().trim()))
                        productFound.add(tmp1);
            }
            else {
                for (Product tmp2 : productTmp)
                    if (tmp2.getName().toLowerCase().contains(productsSearchBar.getText().trim().toLowerCase()))
                        productFound.add(tmp2);
            }

            mainProductsTable.setItems(productFound);
            mainProductsTable.refresh();
        } else {
            mainProductsTable.setItems(inventory.getAllProducts());
            productFound.clear();
            mainProductsTable.refresh();
        }
    }

    /**
     * This method allows the user to exit the program through the exit button.
     * Asks for confirmation before exiting the program.
     * @param event The event of the user clicking the button.
     */
    @FXML private void exitPress(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK)
            Platform.exit();
    }

    /**
     * Initializes the controller.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        popTables();
        partTmp.clear();
        productTmp.clear();

        for (Part tmp : inventory.getAllParts())
            partTmp.add(tmp);

        for (Product tmp1 : inventory.getAllProducts())
            productTmp.add(tmp1);
    }

    /**
     * This method populates both the parts and product tables with each item in their respective inventory.
     */
    private void popTables() {
        mainPartsTable.setItems(inventory.getAllParts());

        partsTableID.setCellValueFactory(new PropertyValueFactory<>("id"));
        partsTableInv.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partsTableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        partsTablePrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        mainProductsTable.setItems(inventory.getAllProducts());

        productTableID.setCellValueFactory(new PropertyValueFactory<>("id"));
        productTableInv.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productTableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        productTablePrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    /**
     * This method passes the part for the modification controller.
     * @return Returns the part selected in the table.
     */
    public static Part getPart() {return part;}

    /**
     * This method passes the product for the modification controller.
     * @return Returns the product selected in the table.
     */
    public static Product getProduct() {return product;}

    /**
     * This method tests if the string is an integer.
     * @param string The string to be tested.
     * @return Either true or false based on the string tested.
     */
    private boolean testInt(String string) {
        Scanner sc = new Scanner(string);
        if (!sc.hasNextInt(10))
            return false;
        sc.hasNextInt(10);
        return sc.hasNext();
    }
}
