package Controllers;

/*
Jae Jee
C842
 */

import Model.InHouse;
import Model.Inventory;
import Model.Outsourced;
import Model.Part;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * Controller to add a part
 */
public class AddPartController implements Initializable {
    Inventory inventory;

    private int id;
    private String name;
    private double price;
    private int stock;
    private int min;
    private int max;
    private String compName;
    private int machineID;

    /**
     * The radio button to select if the part is generated In House.
     */
    @FXML private RadioButton inHouseRadioButton;

    /**
     * The radio button to select if the part generated is Outsourced.
     */
    @FXML private RadioButton outsourcedRadioButton;

    /**
     * This text box will display the name and will be editable.
     */
    @FXML private TextField nameTextBox;

    /**
     * This text box will display the stock and will be editable.
     */
    @FXML private TextField invTextBox;

    /**
     * This text box will display the price and will be editable.
     */
    @FXML private TextField priceTextBox;

    /**
     * This text box will display the maximum stock of the part and will be editable.
     */
    @FXML private TextField maxTextBox;

    /**
     * This text box will display the minimum stock of the part and will be editable.
     */
    @FXML private TextField minTextBox;

    /**
     * This label will change depending on the type of part it is.
     */
    @FXML private Label addPartSWLabel;

    /**
     * This text box will display either the machine ID or the company name depending on the part.
     */
    @FXML private TextField addPartSWTextBox;

    /**
     * This method sets the label to display machine ID if the In House radio button is selected.
     * @param event The event of the user clicking the radio button.
     */
    @FXML void inHouseRadioSelect (ActionEvent event){addPartSWLabel.setText("Machine ID"); }

    /**
     * This method sets the label to display company name if the Outsourced radio button is selected.
     * @param event
     */
    @FXML void outsourcedRadioSelect (ActionEvent event){ addPartSWLabel.setText("Company Name"); }

    /**
     * This method allows for the part to be saved and will show a dialog box with a message that the part has been saved.
     * Will return to the main form after the save is successful.
     * @param event The event of the user clicking the button.
     * @throws IOException The exception that can occur when creating a new scene.
     */
    @FXML private void addPartSave(ActionEvent event) throws IOException {
        if (validityTest()) {
            if (outsourcedRadioButton.isSelected())
                inventory.addPart(new Outsourced(id, name, price, stock, min, max, compName));

            else if (inHouseRadioButton.isSelected())
                inventory.addPart(new InHouse(id, name, price, stock, min, max, machineID));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saved Part");
            alert.setHeaderText(null);
            alert.setContentText("Part has been saved");
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
     * This method will cancel the adding process and will not save any of the entries made to the part.
     * Will show a confirmation message to confirm cancellation
     * @param event The event of the user clicking the button.
     * @throws IOException The exception that can occur when creating a new scene.
     */
    @FXML private void addPartCancel(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
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
     * Initializes the controller with the In House radio button selected.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inHouseRadioButton.setSelected(true);
    }

    /**
     * This method sets the variables with the given entries of the part information to allow for easier saving.
     */
    private void setVar() {
        id = inventory.genPartID();
        name = nameTextBox.getText().trim();
        stock = Integer.parseInt(invTextBox.getText().trim());
        price = Double.parseDouble(priceTextBox.getText().trim());
        min = Integer.parseInt(minTextBox.getText().trim());
        max = Integer.parseInt(maxTextBox.getText().trim());

        if (inHouseRadioButton.isSelected())
            machineID = Integer.parseInt(addPartSWTextBox.getText().trim());
        else if (outsourcedRadioButton.isSelected())
            compName = addPartSWTextBox.getText().trim();
    }

    /**
     * This method tests each text box to ensure that they are valid entries.
     * If a text box fails, it calls on a method to show the relevant error.
     * Initially a series of nested if statements changed into if else statements.
     * @return Returns true if all text boxes are valid. False if there is any errors.
     */
    private boolean validityTest() {
        if (nameTextBox.getText().isEmpty())
            errorBox(2, nameTextBox);
        else if (!testInt(invTextBox.getText()))
            errorBox(1, invTextBox);
        else if (!testDouble(priceTextBox.getText()))
            errorBox(3, priceTextBox);
        else if (!testInt(maxTextBox.getText()))
            errorBox(1, maxTextBox);
        else if (!testInt(minTextBox.getText()))
            errorBox(1,minTextBox);
        else if (!testInvMax())
            errorBox(5, invTextBox);
        else if (!testMinMax((testInt(minTextBox.getText())),testInt(maxTextBox.getText())))
            errorBox(4,maxTextBox);
        else if (addPartSWTextBox.getText().isEmpty())
            errorBox(2, addPartSWTextBox);
        else {
            if (inHouseRadioButton.isSelected())
                if (testInt(addPartSWTextBox.getText())) {
                    setVar();
                    return true;
                } else errorBox(1,addPartSWTextBox);
            else if (outsourcedRadioButton.isSelected()) {
                setVar();
                return true;
            }
        }
        return false;
    }

    /**
     * This method tests if the string is an integer.
     * @param string The string to be tested.
     * @return Either true or false based on the string tested.
     */
    private boolean testInt(String string) {
        Scanner sc = new Scanner(string.trim());
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
        Scanner sc = new Scanner(string.trim());
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
            return Integer.parseInt(maxTextBox.getText().trim()) > Integer.parseInt(minTextBox.getText().trim());
        else return false;
    }

    /**
     * This method tests to ensure the stock does not exceed the max.
     * @return Returns true if the stock is not greater than or is equal to the max. Returns false if the stock is greater than the max.
     */
    private boolean testInvMax() {return Integer.parseInt(invTextBox.getText()) <= Integer.parseInt(maxTextBox.getText().trim());}

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
                alert.setContentText("Max of " + maxTextBox.getText().trim() + " is lower than the min of " + minTextBox.getText().trim());
            }
            case 5 -> {
                alert.setTitle("Inventory/Max Error");
                alert.setContentText("Inventory is greater than the max");
            }
        } alert.showAndWait();
    }
}