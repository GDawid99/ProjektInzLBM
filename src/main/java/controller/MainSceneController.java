package controller;

import graphics.VisualCanvas;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.input.MouseEvent;
import lbm.Lattice;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MainSceneController implements Initializable {

    @FXML
    protected VisualCanvas visualCanvas;
    @FXML
    protected HBox latticeBox;
    private Lattice lattice;
    public List<String> visualValues = Arrays.asList("VelocityX", "VelocityY", "Density", "Temperature");
    private String currentVisualValue;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentVisualValue = visualValues.get(0);
        Platform.runLater(() -> visualCanvas.scaleLattice(latticeBox.getWidth(),latticeBox.getHeight()));
        visualCanvas.getStyleClass().add("canvas-border");
        visualCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, changeLatticeVisualizationHandler);
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
    private EventHandler<MouseEvent> changeLatticeVisualizationHandler = mouseEvent -> {
        int i = visualValues.indexOf(currentVisualValue);
        if (i == visualValues.size()-1) currentVisualValue = visualValues.get(0);
        else currentVisualValue = visualValues.get(++i);
    };


}
