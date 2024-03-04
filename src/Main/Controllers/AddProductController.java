package Controllers;

/*
Jae Jee
C842
 */

import Model.Inventory;
import Model.Part;
import Model.Product;
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
 * Controller to add a product.
 */
public class AddProductController implements Initializable {
    Inventory inventory;

    private static ObservableList<Part> assocParts = FXCollections.observableArrayList();
    private static ObservableList<Part> partFound = FXCollections.observableArrayList();
    private static ObservableList<Part> partTmp = FXCollections.observableArrayList();

    int id;
    String name;
    int stock;
    double price;
    int min;
    int max;

    /**
     * This text box will display the name and will be editable.
     */
    @FXML private TextField addProductNameTextBox;

    /**
     * This text box will display the stock and will be editable.
     */
    @FXML private TextField addProductInvTextBox;

    /**
     * This text box will display the price and will be editable.
     */
    @FXML private TextField addProductPriceTextBox;

    /**
     * This text box will display the maximum stock of the product and will be editable.
     */
    @FXML private TextField addProductMaxTextBox;

    /**
     * This text box will display the minimum stock of the product and will be editable.
     */
    @FXML private TextField addProductMinTextBox;

    /**
     * This is the text box where the user will enter either the name or id to find a part.
     */
    @FXML private TextField addProductSearchBar;

    /**
     * This table will show all parts in the inventory.
     */
    @FXML private TableView<Part> addProductTopTable;

    /**
     * This table will show all parts associated with the product.
     */
    @FXML private TableView<Part> addProductBottomTable;

    @FXML
    private TableColumn<Part, Integer> addProductGenInv;

    @FXML
    private TableColumn<Part, Integer> addProductGenPartID;

    @FXML
    private TableColumn<Part, String> addProductGenPartName;

    @FXML
    private TableColumn<Part, Double> addProductGenPrice;

    @FXML
    private TableColumn<Part, Integer> addProductAssocInv;

    @FXML
    private TableColumn<Part, Integer> addProductAssocPartID;

    @FXML
    private TableColumn<Part, String> addProductAssocPartName;

    @FXML
    private TableColumn<Part, Double> addProductAssocPrice;

    /**
     * This method dynamically searches for a part through the text box either by partial match of the name or match of the id.
     * Will replace all entries with those that match the search criteria.
     * Will display all entries if the text box is empty.
     * @param event The event of the user typing either letter or number.
     */
    @FXML private void addProductSearch(KeyEvent event) {
        partFound.clear();
        if (addProductSearchBar.getText().trim().length() != 0 || addProductSearchBar != null) {
            if (testInt(addProductSearchBar.getText().trim())) {
                for (Part tmp1 : partTmp)
                    if (tmp1.getId() == Integer.parseInt(addProductSearchBar.getText().trim()))
                        partFound.add(tmp1);
            }
            else {
                for (Part tmp2 : partTmp)
                    if (tmp2.getName().toLowerCase().contains(addProductSearchBar.getText().trim().toLowerCase()))
                        partFound.add(tmp2);
            }

            addProductTopTable.setItems(partFound);
            addProductTopTable.refresh();
        } else {
            addProductTopTable.setItems(inventory.getAllParts());
            partFound.clear();
        }
    }

    /**
     * This method allows for the part selected to be added into the list of parts associated with the product.
     * @param event The event of the user clicking the button.
     */
    @FXML private void addProductAdd(ActionEvent event) {
        if (!assocParts.contains(addProductTopTable.getSelectionModel().getSelectedItem())){
            assocParts.add(addProductTopTable.getSelectionModel().getSelectedItem());
            addProductBottomTable.refresh();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Duplicate Part Error");
            alert.setHeaderText(null);
            alert.setContentText("Part is already on the associated list");
            alert.show();
        }
    }

    /**
     * This method deletes the part associated with the product.
     * Will display a confirmation message to confirm the part being removed.
     * @param event The event of the user clicking the button.
     */
    @FXML private void addProductRemove(ActionEvent event) {
        if (addProductBottomTable.getSelectionModel().getSelectedItem() != null) {
            if (!assocParts.isEmpty()) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Delete Product");
                confirm.setHeaderText("Are you sure you want to delete " + addProductBottomTable.getSelectionModel().getSelectedItem().getName());
                confirm.setContentText("Please confirm");

                Optional<ButtonType> result = confirm.showAndWait();
                if (result.get() == ButtonType.OK) {
                    assocParts.remove(addProductBottomTable.getSelectionModel().getSelectedItem());
                    addProductBottomTable.refresh();
                }
            }
        }
    }

    /**
     * This method allows for the product to be saved and will show a dialog box with a message that the part has been saved.
     * Will return to the main form after the save is successful.
     * @param event The event of the user clicking the button.
     * @throws IOException The exception that can occur when creating a new scene.
     */
    @FXML private void addProductSave(ActionEvent event) throws IOException{
        if (validityTest()){
            setVar();
            Product product = (new Product(id,name, price, stock, min, max));
            for (Part part : assocParts)
                product.addAssociatedPart(part);
            inventory.addProduct(product);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Product Saved");
            alert.setHeaderText(null);
            alert.setContentText("Product has been saved");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Parent parent = FXMLLoader.load(getClass().getResource("/Views/main-form.fxml"));
                Scene scene = new Scene(parent);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Main.Main Form");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
            }
        }
    }

    /**
     * This method will cancel the adding process and will not save any of the entries made to the product.
     * Will show a confirmation message to confirm cancellation
     * @param event The event of the user clicking the button.
     * @throws IOException The exception that can occur when creating a new scene.
     */
    @FXML private void addProductCancel(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            assocParts.clear();

            Parent parent = FXMLLoader.load(getClass().getResource("/Views/main-form.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Main.Main Form");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }
    }

    /**
     * Initializes the controller.
     * Populates the entire parts inventory into the top table.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        assocParts.clear();
        partTmp.clear();
        popTables();
        addProductTopTable.refresh();
        addProductBottomTable.refresh();

        for (Part part : inventory.getAllParts())
            partTmp.add(part);
    }

    /**
     * This method populates both the parts and product tables with each item in their respective inventory.
     */
    private void popTables(){
        addProductTopTable.setItems(inventory.getAllParts());

        addProductGenPartID.setCellValueFactory(new PropertyValueFactory<>("id"));
        addProductGenInv.setCellValueFactory(new PropertyValueFactory<>("stock"));
        addProductGenPartName.setCellValueFactory(new PropertyValueFactory<>("name"));
        addProductGenPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        addProductBottomTable.setItems(assocParts);

        addProductAssocPartID.setCellValueFactory(new PropertyValueFactory<>("id"));
        addProductAssocInv.setCellValueFactory(new PropertyValueFactory<>("stock"));
        addProductAssocPartName.setCellValueFactory(new PropertyValueFactory<>("name"));
        addProductAssocPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    /**
     * This method sets the variables with the given entries of the part information to allow for easier saving.
     */
    private void setVar(){
        id = inventory.genProductID();
        name = addProductNameTextBox.getText().trim();
        stock = Integer.parseInt(addProductInvTextBox.getText().trim());
        price = Double.parseDouble(addProductPriceTextBox.getText().trim());
        min = Integer.parseInt(addProductMinTextBox.getText().trim());
        max = Integer.parseInt(addProductMaxTextBox.getText().trim());
    }

    /**
     * This method tests each text box to ensure that they are valid entries.
     * If a text box fails, it calls on a method to show the relevant error.
     * Initially a series of nested if statements changed into if else statements.
     * @return Returns true if all text boxes are valid. False if there is any errors.
     */
    private boolean validityTest(){
        if (addProductNameTextBox.getText().isEmpty())
            errorBox(2, addProductNameTextBox);
        else if (!testInt(addProductInvTextBox.getText()))
            errorBox(1, addProductInvTextBox);
        else if (!testDouble(addProductPriceTextBox.getText()))
            errorBox(3, addProductPriceTextBox);
        else if (!testInt(addProductMaxTextBox.getText()))
            errorBox(1, addProductMaxTextBox);
        else if (!testInt(addProductMinTextBox.getText()))
            errorBox(1,addProductMinTextBox);
        else if (!testInvMax())
            errorBox(5, addProductInvTextBox);
        else if (!testMinMax((testInt(addProductMinTextBox.getText())),testInt(addProductMaxTextBox.getText())))
            errorBox(4,addProductMaxTextBox);
        else
            return true;
        return false;
    }

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

    /**
     * This method tests if the string is double.
     * @param string The string to be tested.
     * @return Either true or false based on the string tested.
     */
    private boolean testDouble(String string) {
        Scanner sc = new Scanner(string);
        if (!sc.hasNextDouble())
            return false;
        sc.hasNextDouble();
        return sc.hasNext();
    }

    /**
     * This method tests if the minimum is greater than the max.
     * @param min Boolean from testInt to ensure it is an integer when tested.
     * @param max Boolean from testInt to ensure it is an integer when tested.
     * @return Returns true if max is greater than the minimum. Returns false if the minimum is greater than the max.
     */
    private boolean testMinMax(boolean min, boolean max) {
        if (min && max)
            return Integer.parseInt(addProductMaxTextBox.getText().trim()) > Integer.parseInt(addProductMinTextBox.getText().trim());
        else return false;
    }

    /**
     * This method tests to ensure the stock does not exceed the max.
     * @return Returns true if the stock is not greater than or is equal to the max. Returns false if the stock is greater than the max.
     */
    private boolean testInvMax() {return Integer.parseInt(addProductInvTextBox.getText()) <= Integer.parseInt(addProductMaxTextBox.getText().trim());}

    /**
     * This method allows for the setup of different dialog boxes.
     * @param i The number that relates to each separate dialog box.
     * @param tF The text field of the related error.
     */
    private void errorBox(int i, TextField tF) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        switch (i) {
            case 1 -> {
                alert.setTitle("Integer Error");
                alert.setContentText(tF.getId() + " does not have a valid number");
            }
            case 2 -> {
                alert.setTitle("Empty Error");
                alert.setContentText(tF.getId() + " is empty");
            }
            case 3 -> {
                alert.setTitle("Double Error");
                alert.setContentText(tF.getId() + " does not have a valid number");
            }
            case 4 -> {
                alert.setHeaderText("Min/Max Error");
                alert.setContentText("Max of " + addProductMaxTextBox.getText().trim() + " is lower than the min of " + addProductMinTextBox.getText().trim());
            }
            case 5 -> {
                alert.setTitle("Inventory/Max Error");
                alert.setContentText("Inventory is greater than the max");
            }
        } alert.showAndWait();
    }
}