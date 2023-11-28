package controller;

import graphics.VisualCanvas;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import lbm.Lattice;

import java.net.URL;
import java.util.ResourceBundle;

public class MainSceneController implements Initializable {
    @FXML
    protected VisualCanvas visualCanvas;
    @FXML
    protected HBox latticeBox;
    @FXML
    protected Menu menu_view;
    private Lattice lattice;
    private String currentVisualValue = "Velocity [Vx]";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> visualCanvas.scaleLattice(latticeBox.getWidth(),latticeBox.getHeight()));
        lattice = new Lattice((int)visualCanvas.getWidth(),(int)visualCanvas.getHeight());
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                lattice.executeOperations();
                visualCanvas.draw(lattice,currentVisualValue);
                visualCanvas.drawLines(lattice);
            }
        };
        animationTimer.start();
    }

    //Events
    public void setCanvasView(ActionEvent actionEvent) {
        currentVisualValue = ((MenuItem)actionEvent.getSource()).getText();
        menu_view.getItems().forEach(e -> e.setDisable(false));
        ((MenuItem)actionEvent.getSource()).setDisable(true);
    }
    

}
