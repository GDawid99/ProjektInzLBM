package lbm;


import lbm.boundary.BoundaryDirection;
import lbm.boundary.CellBoundaryType;
import lbm.boundary.FluidBoundaryType;
import lbm.boundary.TempBoundaryType;
import lbm.model.FluidFlowD2Q9;
import lbm.model.ModelD2Q9;
import lbm.model.TemperatureD2Q9;
import util.Velocity;

/**
 * Klasa {@code Cell} reprezentująca komórkę w przestrzeni<br>
 * 2D,
 */
public class Cell {
    public int x;
    public int y;
    public ModelD2Q9 model;
    public ModelD2Q9 temperatureModel;
    public CellBoundaryType cellBoundaryType;
    public float density;
    public Velocity velocity;
    public float temperature;
    public boolean isHeatSource;



    public Cell(int x, int y, float density, float temperature, Velocity velocity, FluidBoundaryType fluidBoundaryType, TempBoundaryType tempBoundaryType, BoundaryDirection direction, ModelD2Q9 model, ModelD2Q9 temperatureModel) {
        this.x = x;
        this.y = y;
        this.model = model;
        this.temperatureModel = temperatureModel;
        this.cellBoundaryType = new CellBoundaryType(fluidBoundaryType,tempBoundaryType,direction);
        this.density = density;
        this.temperature = temperature;
        this.velocity = velocity;
    }

    public Cell(Cell cell) {
        this.x = cell.x;
        this.y = cell.y;
        this.cellBoundaryType = cell.cellBoundaryType;
        this.density = cell.density;
        this.model = new FluidFlowD2Q9((FluidFlowD2Q9) cell.model);
        this.temperatureModel = new TemperatureD2Q9((TemperatureD2Q9) cell.temperatureModel);
        this.velocity = new Velocity(cell.velocity);
    }

    public void setCellBoundaryType(FluidBoundaryType fluidBoundaryType, TempBoundaryType tempBoundaryType, BoundaryDirection boundaryDirection) {
        this.cellBoundaryType = new CellBoundaryType(fluidBoundaryType,tempBoundaryType,boundaryDirection);
    }

    public void equilibriumFunction() {
        this.model.calcEquilibriumFunctions(this.velocity, this.density);
    }


    private float calcDensity() {
        float d = 0;
        try {
            for (int i = 0; i < 9; i++) {
                d += model.fin.get(i);
            }
        }catch (NullPointerException e) {
            System.out.println("[" + x + "," + y + "]");
        }
        return d;
    }

    private float calcTemperature() {
        float t = 0f;
        for (int i =0 ; i < 9; i++) {
            t += temperatureModel.fin.get(i);
        }
        return t;
    }

    private Velocity calcVelocity(float gravity) {
        float ux = 0f, uy = 0f;
        for (int i = 0; i < 9; i++) {
            ux += model.fin.get(i) * ModelD2Q9.c.get(i).get(0);
            uy += model.fin.get(i) * ModelD2Q9.c.get(i).get(1);
        }
        ux /= this.density;
        uy = (uy/this.density)-gravity*(1-2f*temperature);
        return new Velocity(ux, uy);
    }

    public void calcMacroscopicDensity() {
        this.density = calcDensity();
    }

    public void calcMacroscopicVelocity(float gravity) {
        this.velocity = calcVelocity(gravity);
    }

    public void calcMacroscopicTemperature() {
        this.temperature = calcTemperature();
    }

    public void calcMacroscopicTemperature(float temperature) {
        this.temperature = temperature;
    }

    public CellBoundaryType getCellBoundaryType() {
        return cellBoundaryType;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "x=" + x +
                ", y=" + y +
                ", model=" + model +
                ", temperatureModel=" + temperatureModel +
                ", cellBoundaryType=" + cellBoundaryType +
                ", density=" + density +
                ", velocity=" + velocity +
                ", temperature=" + temperature +
                '}';
    }
}
