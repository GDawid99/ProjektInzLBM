package controller;

import graphics.GradientBarCanvas;
import graphics.VisualCanvas;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import lbm.Simulation;
import util.Velocity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainSceneController implements Initializable {

    @FXML
    public ToggleButton show_flowlines_button;
    @FXML
    public GradientBarCanvas gradient_bar;
    @FXML
    public TextField gradientbar_min_textfield;
    @FXML
    public TextField gradientbar_max_textfield;
    @FXML
    public Label val1_label;
    @FXML
    public Label val2_label;
    @FXML
    public Label val3_label;
    @FXML
    public Label val4_label;
    @FXML
    public Label val5_label;
    @FXML
    public Slider lineSlider;
    public Label cellXLabel;
    public Label cellYLabel;
    public Label cellDensityLabel;
    public Label cellTemperatureLabel;
    public Label cellVelocityLabel;
    public Label iterationLabel;
    public Label runSimulationLabel;
    public Button runButton;
    @FXML
    protected VisualCanvas visualCanvas;
    @FXML
    protected HBox latticeBox;
    @FXML
    protected Menu menu_view;
    private Simulation simulation;
    private String currentVisualValue = "Velocity [Vx]";

    private Velocity Vmin = new Velocity(-0.02f,-0.02f);
    private Velocity Vmax = new Velocity(0.02f, 0.02f);
    private float minDensity = 0.995f;
    private float maxDensity = 1.005f;
    private float minTemperature = 0f;
    private float maxTemperature = minTemperature+1f;
    private float gradientMin = Vmin.ux;
    private float gradientMax = Vmax.ux;
    private AnimationTimer animationTimer;
    private String path = "data5.txt";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> visualCanvas.scaleLattice(latticeBox.getWidth(),latticeBox.getHeight()));
        simulation = new Simulation();
        setLatticeSize(path);
        simulation.initLBM((int)visualCanvas.getWidth(),(int)visualCanvas.getHeight(), path);
        menu_view.getItems().get(0).setDisable(true);
        setGradientBarValues(Vmin.ux,Vmax.ux);
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                int it = simulation.loopLBM();
                iterationLabel.setText("Iteration: " + it);
                visualCanvas.draw(simulation.getLattice(),currentVisualValue, gradientMin, gradientMax);
                if (show_flowlines_button.isSelected()) visualCanvas.drawLines(simulation.getLattice(),lineSlider.getValue());
                gradient_bar.draw(currentVisualValue, gradientMin, gradientMax);
            }
        };
        animationTimer.start();

    }

    private void setLatticeSize(String path) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("#")) {
                    if (line.charAt(0) == '#') continue;
                    else line = line.substring(0, line.indexOf('#'));
                }
                if (line.contains("width")) {
                    visualCanvas.setWidth(Double.parseDouble(line.substring(line.indexOf("=")+1,line.indexOf(";"))));
                }
                if (line.contains("height")) {
                    visualCanvas.setHeight(Double.parseDouble(line.substring(line.indexOf("=")+1,line.indexOf(";"))));
                }
            }
        }
        catch (IOException e) {
            System.err.println("I/O: Blad z plikiem.");
            System.exit(-1);
        }
    }

    public void setGradientBarValues(float min, float max) {
        gradientbar_min_textfield.setText(String.valueOf(min));
        gradientbar_max_textfield.setText(String.valueOf(max));
        setLabelValues(min,max);
    }

    public void setLabelValues(float min, float max) {
        float v2 = (min + max)/2;
        float v1 = (min + v2)/2;
        float v3 = (v2 + max)/2;
        val1_label.setText(String.valueOf(min));
        val2_label.setText(String.valueOf(v1));
        val3_label.setText(String.valueOf(v2));
        val4_label.setText(String.valueOf(v3));
        val5_label.setText(String.valueOf(max));
    }

    //Events
    public void setOnActionChangeCanvasView(ActionEvent actionEvent) {
        currentVisualValue = ((MenuItem)actionEvent.getSource()).getText();
        menu_view.getItems().forEach(e -> e.setDisable(false));
        ((MenuItem)actionEvent.getSource()).setDisable(true);
        switch (currentVisualValue) {
            case "Velocity [Vx]" -> {
                gradientbar_min_textfield.setText(String.valueOf(Vmin.ux));
                gradientbar_max_textfield.setText(String.valueOf(Vmax.ux));
                gradientMin = Vmin.ux;
                gradientMax = Vmax.ux;

            }
            case "Velocity [Vy]" -> {
                gradientbar_min_textfield.setText(String.valueOf(Vmin.uy));
                gradientbar_max_textfield.setText(String.valueOf(Vmax.uy));
                gradientMin = Vmin.uy;
                gradientMax = Vmax.uy;
            }
            case "Density" -> {
                gradientbar_min_textfield.setText(String.valueOf(minDensity));
                gradientbar_max_textfield.setText(String.valueOf(maxDensity));
                gradientMin = minDensity;
                gradientMax = maxDensity;
            }
            case "Temperature" -> {
                gradientbar_min_textfield.setText(String.valueOf(minTemperature));
                gradientbar_max_textfield.setText(String.valueOf(maxTemperature));
                gradientMin = minTemperature;
                gradientMax = maxTemperature;
            }
        }
        setLabelValues(gradientMin,gradientMax);
    }


    public void onMouseClickedChangeVisibleOfLines(MouseEvent mouseEvent) {
        if (show_flowlines_button.isSelected()) {
            show_flowlines_button.setText("Hide");
            show_flowlines_button.setSelected(true);
        }
        else {
            show_flowlines_button.setText("Show");
            show_flowlines_button.setSelected(false);
        }

    }

    public void onMouseClickedSetNewValuesForGradientBar(MouseEvent mouseEvent) {
        float copyMin, copyMax;
        switch (currentVisualValue) {
            case "Velocity [Vx]" -> {
                copyMin = Vmin.ux;
                copyMax = Vmax.ux;
                try {
                    Vmin.ux = Float.parseFloat(gradientbar_min_textfield.getText());
                    Vmax.ux = Float.parseFloat(gradientbar_max_textfield.getText());
                    gradientMin = Vmin.ux;
                    gradientMax = Vmax.ux;
                } catch (NumberFormatException e) {
                    Vmin.ux = copyMin;
                    Vmax.ux = copyMax;
                    setGradientBarValues(copyMin,copyMax);
                }
            }
            case "Velocity [Vy]" -> {
                copyMin = Vmin.uy;
                copyMax = Vmax.uy;
                try {
                    Vmin.uy = Float.parseFloat(gradientbar_min_textfield.getText());
                    Vmax.uy = Float.parseFloat(gradientbar_max_textfield.getText());
                    gradientMin = Vmin.uy;
                    gradientMax = Vmax.uy;
                } catch (NumberFormatException e) {
                    Vmin.uy = copyMin;
                    Vmax.uy = copyMax;
                    setGradientBarValues(copyMin,copyMax);
                }
            }
            case "Density" -> {
                copyMin = minDensity;
                copyMax = maxDensity;
                try {
                    minDensity = Float.parseFloat(gradientbar_min_textfield.getText());
                    maxDensity = Float.parseFloat(gradientbar_max_textfield.getText());
                    gradientMin = minDensity;
                    gradientMax = maxDensity;
                } catch (NumberFormatException e) {
                    minDensity = copyMin;
                    maxDensity = copyMax;
                    setGradientBarValues(copyMin,copyMax);
                }
            }
            case "Temperature" -> {
                copyMin = minTemperature;
                copyMax = maxTemperature;
                try {
                    minTemperature = Float.parseFloat(gradientbar_min_textfield.getText());
                    maxTemperature = Float.parseFloat(gradientbar_max_textfield.getText());
                    gradientMin = minTemperature;
                    gradientMax = maxTemperature;
                } catch (NumberFormatException e) {
                    minTemperature = copyMin;
                    maxTemperature = copyMax;
                    setGradientBarValues(copyMin,copyMax);
                }
            }
        }
        setLabelValues(gradientMin,gradientMax);
    }


    public void getCellData(MouseEvent mouseEvent) {
        int x = (int)Math.floor(mouseEvent.getX());
        int y = (int)Math.floor(mouseEvent.getY());
        cellXLabel.setText("x = " + simulation.getLattice().cells[y][x].x);
        cellYLabel.setText("y = " + simulation.getLattice().cells[y][x].y);
        cellDensityLabel.setText("Density = " + simulation.getLattice().cells[y][x].density);
        cellTemperatureLabel.setText("Temperature = " + simulation.getLattice().cells[y][x].temperature);
        cellVelocityLabel.setText("Velocity = \n[" + simulation.getLattice().cells[y][x].velocity.ux + ", " + simulation.getLattice().cells[y][x].velocity.uy + "]");
    }

    public void onMouseClickedStopOrRunSimulation(MouseEvent mouseEvent) {
        if (runSimulationLabel.getText().contains("Stop")) {
            animationTimer.stop();
            runSimulationLabel.setText("Run simulation: ");
            runButton.setText("Run");
        }
        else {
            animationTimer.start();
            runSimulationLabel.setText("Stop simulation: ");
            runButton.setText("Stop");
        }
    }
}
