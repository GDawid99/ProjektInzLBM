import controller.MainSceneController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


import java.util.Objects;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main_scene.fxml")));

        Scene scene = new Scene(root,900, 600);
        EventHandler<MouseEvent> eventHandler = e -> {
            if (MainSceneController.visualValue.equals("VelocityX")) MainSceneController.visualValue = "VelocityY";
            else if (MainSceneController.visualValue.equals("VelocityY")) MainSceneController.visualValue = "Density";
            else if (MainSceneController.visualValue.equals("Density")) MainSceneController.visualValue = "Temperature";
            else MainSceneController.visualValue = "VelocityX";
        };
        //Adding event Filter
        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);

        stage.setMinWidth(450);
        stage.setMinHeight(300);
        stage.setTitle("ProjektInżynierski");
        stage.setScene(scene);
        stage.show();
    }
}
