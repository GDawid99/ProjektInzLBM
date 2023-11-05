import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        stage.setMinWidth(450);
        stage.setMinHeight(300);
        stage.setTitle("ProjektInżynierski");
        stage.setScene(scene);
        stage.show();
    }
}
