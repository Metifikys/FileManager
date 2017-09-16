package metifikys;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import metifikys.state.StateController;

import java.net.URL;

// TODO Spring DI
public class Start extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        String fxmlFile = "/fxml/hello.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResourceAsStream(fxmlFile));
        Scene scene = new Scene(root);

        loader.<MainController>getController()
                .setScene(scene);

        primaryStage.setOnCloseRequest(event -> StateController.saveElements());
        primaryStage.setTitle("FileManager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}