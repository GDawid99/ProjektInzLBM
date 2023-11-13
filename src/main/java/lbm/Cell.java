package lbm;


import javafx.scene.paint.Color;
import lbm.model.D2Q9;
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
    private CellState cellState;
    public float density;
    public Velocity velocity;



    public Cell(int x, int y, float density, Velocity velocity, CellState state, Model model) {
        this.x = x;
        this.y = y;
        this.model = model;
        this.cellState = state;
        this.density = density;
        this.velocity = velocity;
    }

    public Cell(Cell cell) {
        this.x = cell.x;
        this.y = cell.y;
        this.cellState = cell.getCellState();
        this.density = cell.density;
        this.model = new D2Q9((D2Q9) cell.model);
        this.velocity = new Velocity(cell.velocity);
    }

    public CellState getCellState() {
        return this.cellState;
    }

    public Color getColor(float min, float max, float valueVisualization) {
        switch (cellState) {
            case BOUNCE_BACK_BC, SYMMETRY_BC -> {
                return Color.color(0,0,0);
            }
            case FLUID, CONST_BC -> {
                return  calcColorByValueOtherScale(valueVisualization);
                //return calcColorByValue(min, max, valueVisualization);
            }

        }
        return null;
    }

    private Color calcColorByValue(double min, double max, double value) {
        if (min > value) return Color.color(0, 0, 0.75);
        if (max < value) return Color.color(0.75, 0, 0);
        double firstColorValue = (value - min)/(max - min), colorValue;
        if (firstColorValue < 1/6d) {
            colorValue = (0.25*firstColorValue)/(1/6d) + 0.75;
            return Color.color(0, 0, colorValue);
        }
        else if (firstColorValue >= 1/6d && firstColorValue < 5/6d) {
            colorValue = (firstColorValue - 1/6d)/(5/6d - 1/6d);
            return Color.color(colorValue, 0, 1- colorValue);
        }
        else {
            colorValue =(0.25*(1 - firstColorValue))/(1/6d) + 0.75;
            return Color.color(colorValue, 0, 0);
        }
    }

    private Color calcColorByValueOtherScale(double value) {
        if (value > 0) {
            double color = 1 - value / 0.02;
            if (color > 1) color = 1.0;
            if (color < 0) color = 0.0;
            return Color.color(1, color, color);
        }
        else {
            double color = 1 - Math.abs(value) / 0.02;
            if (color > 1) color = 1.0;
            if (color < 0) color = 0.0;
            return Color.color(color, color, 1);
        }
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
