package lbm;


import controller.MainSceneController;
import javafx.scene.paint.Color;
import lbm.model.D2Q9;
import lbm.model.D2Q9Temperature;
import lbm.model.Model;
import util.Velocity;

/**
 * Klasa {@code Cell} reprezentująca komórkę w przestrzeni<br>
 * 2D,
 */
public class Cell {
    public int x;
    public int y;
    public Model model;
    public Model temperatureModel;
    private CellState cellState;
    public float density;
    public Velocity velocity;
    public float temperature;



    public Cell(int x, int y, float density, Velocity velocity, CellState state, Model model, Model temperatureModel) {
        this.x = x;
        this.y = y;
        this.model = model;
        this.temperatureModel = temperatureModel;
        this.cellState = state;
        this.density = density;
        this.temperature = 0f;
        this.velocity = velocity;
    }

    public Cell(int x, int y, float density, float temperature, Velocity velocity, CellState state, Model model, Model temperatureModel) {
        this.x = x;
        this.y = y;
        this.model = model;
        this.temperatureModel = temperatureModel;
        this.cellState = state;
        this.density = density;
        this.temperature = temperature;
        this.velocity = velocity;
    }

    public Cell(Cell cell) {
        this.x = cell.x;
        this.y = cell.y;
        this.cellState = cell.getCellState();
        this.density = cell.density;
        this.model = new D2Q9((D2Q9) cell.model);
        this.temperatureModel = new D2Q9Temperature((D2Q9Temperature) cell.temperatureModel);
        this.velocity = new Velocity(cell.velocity);
    }


    private float calcDensity() {
        float d = 0;
        for (int i =0 ; i < 9; i++) {
            d += model.getFin().get(i);
        }
        return d;
    }

    private float calcTemperature() {
        float t = 0f;
        for (int i =0 ; i < 9; i++) {
            //t += temperatureModel.getFin().get(i);
        }
        return t;
    }

    private Velocity calcVelocity() {
        float ux = 0f, uy = 0f;
        for (int i = 0; i < 9; i++) {
            ux += model.getFin().get(i) * D2Q9.c.get(i).get(0);
            uy += model.getFin().get(i) * D2Q9.c.get(i).get(1);
        }
        ux /= this.density;
        uy /= this.density;
        return new Velocity(ux, uy);
    }

    public void calcMacroscopicValues() {
        this.density = calcDensity();
        this.velocity = calcVelocity();
        this.temperature = calcTemperature();
    }


    public CellState getCellState() {
        return this.cellState;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "x=" + x +
                ", y=" + y +
                ", cellState=" + cellState +
                ", density=" + density +
                ", velocity=" + velocity +
                '}';
    }
}
