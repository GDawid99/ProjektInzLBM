package controller;

import graphics.VisualCanvas;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import lbm.Lattice;
import lbm.model.D2Q9;
import lbm.model.Model;
import util.Velocity;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReentrantLock;

public class MainSceneController implements Initializable {

    @FXML
    protected VisualCanvas visualCanvas;
    private Lattice lattice;
    public static String visualValue = "VelocityX";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lattice = new Lattice((int)visualCanvas.getWidth(),(int)visualCanvas.getHeight());
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                lattice.executeOperations();
                visualCanvas.draw(lattice,visualValue);
            }
        };
        animationTimer.start();
    }


}
