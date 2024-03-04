package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/*
Jae Jee
C842

Javadocs in src/javadocs
 */


public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/Views/main-form.fxml"));
        Scene scene = new Scene(parent);

        stage.setTitle("Main.Main Form");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {launch();}
}