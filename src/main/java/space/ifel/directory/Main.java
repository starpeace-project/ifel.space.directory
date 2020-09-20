package space.ifel.directory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Starting to run application");
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/directory.fxml"));
        primaryStage.setTitle("Directory Server");
        primaryStage.setScene(new Scene(root, 486, 321));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
