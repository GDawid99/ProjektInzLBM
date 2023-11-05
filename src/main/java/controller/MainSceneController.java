package controller;

import graphics.VisualCanvas;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import lbm.Lattice;
import lbm.model.D2Q9;
import lbm.model.Model;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReentrantLock;

public class MainSceneController implements Initializable {

    @FXML
    VisualCanvas visualCanvas;

    Lattice lattice;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lattice = new Lattice((int)visualCanvas.getWidth(),(int)visualCanvas.getHeight());
        final int[] count = {0};
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                lattice.executeOperations();
                visualCanvas.draw(lattice);
                //try {
                //    Thread.sleep(10);
                //} catch (InterruptedException e) {
                //    throw new RuntimeException(e);
                //}

            }
        };
        animationTimer.start();
    }
}
