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
 * Controller to modify a product
 */
public class ModifyProductController implements Initializable {
    Inventory inventory;
    MainFormController mainFormController;
    Product product;

    String name;
    int stock;
    double price;
    int min;
    int max;

    private static ObservableList<Part> assocParts = FXCollections.observableArrayList();
    private static ObservableList<Part> tmp = FXCollections.observableArrayList();
    private static ObservableList<Part> partFound = FXCollections.observableArrayList();
    private static ObservableList<Part> partTmp = FXCollections.observableArrayList();

    /**
     * This text box will display the ID of the product and is uneditable.
     */
    @FXML private TextField modifyProductIDTextBox;

    /**
     * This text box will display the name and will be editable.
     */
    @FXML private TextField modifyProductNameTextBox;

    /**
     * This text box will display the stock and will be editable.
     */
    @FXML private TextField modifyProductInvTextBox;

    /**
     * This text box will display the price and will be editable.
     */
    @FXML private TextField modifyProductPriceTextBox;

    /**
     * This text box will display the maximum stock of the product and will be editable.
     */
    @FXML private TextField modifyProductMaxTextBox;

    /**
     * This text box will display the minimum stock of the product and will be editable.
     */
    @FXML private TextField modifyProductMinTextBox;

    /**
     * This is the text box where the user will enter either the name or id to find a part.
     */
    @FXML private TextField modifyProductSearchBar;

    /**
     * This table will show all parts in the inventory.
     */
    @FXML private TableView<Part> modifyProductTopTable;

    /**
     * This table will show all parts associated with the product.
     */
    @FXML private TableView<Part> modifyProductBottomTable;

    @FXML
    private TableColumn<Part, Integer> modProductAssocInv;

    @FXML
    private TableColumn<Part, Integer> modProductAssocPartID;

    @FXML
    private TableColumn<Part, String> modProductAssocPartName;

    @FXML
    private TableColumn<Part, Double> modProductAssocPrice;

    @FXML
    private TableColumn<Part, Integer> modProductGenInv;

    @FXML
    private TableColumn<Part, Integer> modProductGenPartID;

    @FXML
    private TableColumn<Part, String> modProductGenPartName;

    @FXML
    private TableColumn<Part, Double> modProductGenPrice;

    /**
     * This method dynamically searches for a part through the text box either by partial match of the name or match of the id.
     * Will replace all entries with those that match the search criteria.
     * Will display all entries if the text box is empty.
     * @param event The event of the user typing either letter or number.
     */
    @FXML private void modifyProductSearch(KeyEvent event) {
        partFound.clear();
        if (modifyProductSearchBar.getText().trim().length() != 0 || modifyProductSearchBar != null) {
            if (testInt(modifyProductSearchBar.getText().trim())) {
                for (Part tmp1 : partTmp)
                    if (tmp1.getId() == Integer.parseInt(modifyProductSearchBar.getText().trim()))
                        partFound.add(tmp1);
            }
            else {
                for (Part tmp2 : partTmp)
                    if (tmp2.getName().toLowerCase().contains(modifyProductSearchBar.getText().trim().toLowerCase()))
                        partFound.add(tmp2);
            }

            modifyProductTopTable.setItems(partFound);
            modifyProductTopTable.refresh();
        } else {
            modifyProductTopTable.setItems(inventory.getAllParts());
            partFound.clear();
        }
    }

    /**
     * This method allows for the part selected to be added into the list of parts associated with the product.
     * @param event The event of the user clicking the button.
     */
    @FXML private void modifyProductAdd(ActionEvent event) {
        if (!product.getAllAssociatedParts().contains(modifyProductTopTable.getSelectionModel().getSelectedItem())) {
            assocParts.add(modifyProductTopTable.getSelectionModel().getSelectedItem());
            modifyProductBottomTable.refresh();
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
    @FXML private void modifyProductRemove(ActionEvent event) {
        if (modifyProductBottomTable.getSelectionModel().getSelectedItem() != null){
            if (!assocParts.isEmpty()){
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Delete Product");
                confirm.setHeaderText("Are you sure you want to delete " + modifyProductBottomTable.getSelectionModel().getSelectedItem().getName());
                confirm.setContentText("Please confirm");

                Optional<ButtonType> result = confirm.showAndWait();
                if (result.get() == ButtonType.OK) {
                    assocParts.remove(modifyProductBottomTable.getSelectionModel().getSelectedItem());
                    modifyProductBottomTable.refresh();
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
    @FXML private void modifyProductSave(ActionEvent event) throws IOException {
        if (validityTest()){
            setVar();

            product.setName(name);
            product.setStock(stock);
            product.setPrice(price);
            product.setMin(min);
            product.setMax(max);

            int index = inventory.productIndex(product);
            inventory.updateProduct(index, product);

            for (Part part : assocParts)
                if (!tmp.contains(part))
                    product.addAssociatedPart(part);

            for (Part part : tmp)
                if (!assocParts.contains(part)) {
                    product.deleteAssociatedPart(part);
                    System.out.println("yes");
                }

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
    @FXML private void modifyProductCancel(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setContentText("Are you sure?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            Parent parent = FXMLLoader.load(getClass().getResource("/Views/main-form.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Modify Part Form");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }
    }

    /**
     * Initializes the controller.
     * Populates the entire parts inventory into the top table and associated parts into the bottom table.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        assocParts.clear();
        partTmp.clear();
        product = mainFormController.getProduct();
        for (Part part : product.getAllAssociatedParts()) {
            assocParts.add(part);
            tmp.add(part);
        }
        for (Part tmp : inventory.getAllParts())
            partTmp.add(tmp);

        modifyProductIDTextBox.setText(String.valueOf(product.getId()));
        modifyProductNameTextBox.setText(String.valueOf(product.getName()));
        modifyProductInvTextBox.setText(String.valueOf(product.getStock()));
        modifyProductPriceTextBox.setText(String.valueOf(product.getPrice()));
        modifyProductMaxTextBox.setText(String.valueOf(product.getMax()));
        modifyProductMinTextBox.setText(String.valueOf(product.getMin()));

        popTables();
        modifyProductTopTable.refresh();
        modifyProductBottomTable.refresh();
    }

    /**
     * This method populates both the parts and product tables with each item in their respective inventory.
     */
    private void popTables() {
        modifyProductTopTable.setItems(inventory.getAllParts());

        modProductGenPartID.setCellValueFactory(new PropertyValueFactory<>("id"));
        modProductGenInv.setCellValueFactory(new PropertyValueFactory<>("stock"));
        modProductGenPartName.setCellValueFactory(new PropertyValueFactory<>("name"));
        modProductGenPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        modifyProductBottomTable.setItems(assocParts);

        modProductAssocPartID.setCellValueFactory(new PropertyValueFactory<>("id"));
        modProductAssocInv.setCellValueFactory(new PropertyValueFactory<>("stock"));
        modProductAssocPartName.setCellValueFactory(new PropertyValueFactory<>("name"));
        modProductAssocPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    /**
     * This method sets the variables with the given entries of the part information to allow for easier saving.
     */
    private void setVar() {
        name = modifyProductNameTextBox.getText().trim();
        stock = Integer.parseInt(modifyProductInvTextBox.getText().trim());
        price = Double.parseDouble(modifyProductPriceTextBox.getText().trim());
        min = Integer.parseInt(modifyProductMinTextBox.getText().trim());
        max = Integer.parseInt(modifyProductMaxTextBox.getText().trim());
    }

    /**
     * This method tests each text box to ensure that they are valid entries.
     * If a text box fails, it calls on a method to show the relevant error.
     * Initially a series of nested if statements changed into if else statements.
     * @return Returns true if all text boxes are valid. False if there is any errors.
     */
    private boolean validityTest() {
        if (modifyProductNameTextBox.getText().isEmpty())
            errorBox(2, modifyProductNameTextBox);
        else if (!testInt(modifyProductInvTextBox.getText()))
            errorBox(1, modifyProductInvTextBox);
        else if (!testDouble(modifyProductPriceTextBox.getText()))
            errorBox(3, modifyProductPriceTextBox);
        else if (!testInt(modifyProductMaxTextBox.getText()))
            errorBox(1, modifyProductMaxTextBox);
        else if (!testInt(modifyProductMinTextBox.getText()))
            errorBox(1,modifyProductMinTextBox);
        else if (!testInvMax())
            errorBox(5, modifyProductInvTextBox);
        else if (!testMinMax((testInt(modifyProductMinTextBox.getText())),testInt(modifyProductMaxTextBox.getText())))
            errorBox(4,modifyProductMaxTextBox);
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
            return Integer.parseInt(modifyProductMaxTextBox.getText().trim()) > Integer.parseInt(modifyProductMinTextBox.getText().trim());
        else return false;
    }

    /**
     * This method tests to ensure the stock does not exceed the max.
     * @return Returns true if the stock is not greater than or is equal to the max. Returns false if the stock is greater than the max.
     */
    private boolean testInvMax() {return Integer.parseInt(modifyProductInvTextBox.getText()) <= Integer.parseInt(modifyProductMaxTextBox.getText().trim());}

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
                alert.setContentText("Max of " + modifyProductMaxTextBox.getText().trim() + " is lower than the min of " + modifyProductMinTextBox.getText().trim());
            }
            case 5 -> {
                alert.setTitle("Inventory/Max Error");
                alert.setContentText("Inventory is greater than the max");
            }
        } alert.showAndWait();
    }
}
