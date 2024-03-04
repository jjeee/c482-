package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.util.Scanner;

public class Inventory {
    public static int partID = 0;

    public static int productID = 0;

    private static ObservableList<Part> allParts = FXCollections.observableArrayList();
    private static ObservableList<Product> allProducts = FXCollections.observableArrayList();

    public static void addPart(Part newPart) {
        allParts.add(newPart);
    }

    public static void addProduct(Product newProduct) {
        allProducts.add(newProduct);
    }

    public static Part lookupPart(int partID) {
        for (Part part : allParts)
            if (part.getId() == partID)
                return part;

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Could not find part");
        return null;
    }

    public static Product lookupProduct(int productID) {
        for (Product product : allProducts)
            if (product.getId() == productID)
                return product;

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Could not find product");
        return null;
    }

    public static ObservableList<Part> lookupPart(String partName) {
        if (!allParts.isEmpty()) {
            ObservableList<Part> foundPart = FXCollections.observableArrayList();

            if (testInt(partName))
                for (Part pt1 : allParts)
                    if (pt1.getId() == Integer.parseInt(partName))
                        foundPart.add(pt1);
            else {
                for (Part part : allParts)
                            if (part.getName().toLowerCase().contains(partName))
                                foundPart.add(part);
                    }
            return foundPart;
        } else
            return null;
    }

    public static ObservableList<Product> lookupProduct(String productName) {
        if (!allProducts.isEmpty()) {
            ObservableList<Product> foundProduct = FXCollections.observableArrayList();

            if (testInt(productName))
                for (Product pd1 : allProducts)
                    if (pd1.getId() == Integer.parseInt(productName))
                        foundProduct.add(pd1);
            else {
                for (Product product : allProducts)
                    if (product.getName().toLowerCase().contains(productName))
                        foundProduct.add(product);
                    }
            return foundProduct;
        }else return null;
    }

    public static void updatePart(int index, Part selectedPart) {allParts.set(index, selectedPart);}

    public static void updateProduct(int index, Product newProduct) {allProducts.set(index, newProduct);}

    public static boolean deletePart(Part selectedPart) {
        if (allParts.contains(selectedPart)) {
            allParts.remove(selectedPart);
            return true;
        } else return false;
    }

    public static boolean deleteProduct(Product selectedProduct) {
        if (allProducts.contains(selectedProduct)) {
            allProducts.remove(selectedProduct);
            return true;
        } else return false;
    }

    public static ObservableList<Part> getAllParts() {return allParts;}

    public static ObservableList<Product> getAllProducts() {return allProducts;}

    public static int genPartID() {return ++partID;}

    public static int genProductID() {return ++productID;}

    public static int partIndex(Part tmpPart) {return allParts.indexOf(tmpPart);}

    public static int productIndex(Product tmpProduct) {return allProducts.indexOf(tmpProduct);}

    public static boolean testInt(String string) {
        Scanner sc = new Scanner(string);
        if (!sc.hasNextInt(10))
            return false;
        sc.hasNextInt(10);
        return sc.hasNext();
    }
}
