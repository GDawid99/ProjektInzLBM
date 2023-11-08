package lbm;


import javafx.scene.paint.Color;
import lbm.model.D2Q9;
import lbm.model.InitValues;
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

    public void setCellState(CellState cellState) {
        this.cellState = cellState;
    }


    public Color getColor() {
        switch (cellState) {
            case WALL -> {
                return Color.color(0,0,0);
            }
//            case VELOCITY_WALL -> {
//                if (velocity.ux > 0) {
//                    return Color.color(1, 0, 0);
//                }
//                else {
//                    return Color.color(0, 0, 1);
//                }
//            }
            case FLUID, VELOCITY_WALL -> {
//                if (velocity.ux > 0) {
//                    double color = 1 - velocity.ux / 0.02;
//                    if (color > 1) color = 1.0;
//                    if (color < 0) color = 0.0;
//                    return Color.color(1, color, color);
//                }
//                else {
//                    double color = 1 - Math.abs(velocity.ux) / 0.02;
//                    if (color > 1) color = 1.0;
//                    if (color < 0) color = 0.0;
//                    return Color.color(color, color, 1);
//                }
                return calcColorByValue(Lattice.MIN_VELOCITY.ux,Lattice.MAX_VELOCITY.ux, velocity.ux);
            }

        }
        return null;
    }

    private Color calcColorByValue(double min, double max, double value) {
        //Color minColor = Color.color(0, 0, 0.75);
        //Color maxColor = Color.color(0.75, 0, 0);
        double firstColorValue = (value - min)/(max - min), colorValue;
        if (firstColorValue < 1/6d) {
            colorValue = (0.25*firstColorValue)/(1/6d) + 0.75;
            return Color.color(0, 0, colorValue);
        }
        else if (firstColorValue >= 1/6d && firstColorValue < 5/6d) {
            return Color.color(firstColorValue, 0, 1- firstColorValue);
        }
        else {
            colorValue =(0.25*(1 - firstColorValue))/(1/6d) + 0.75;
            return Color.color(colorValue, 0, 0);
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
