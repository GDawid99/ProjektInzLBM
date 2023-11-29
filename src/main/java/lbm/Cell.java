package lbm;


import lbm.boundary.FluidBoundaryType;
import lbm.boundary.TempBoundaryType;
import lbm.model.FluidFlowD2Q9;
import lbm.model.TemperatureD2Q9;
import util.Velocity;

/**
 * Klasa {@code Cell} reprezentująca komórkę w przestrzeni<br>
 * 2D,
 */
public class Cell {
    public int x;
    public int y;
    public FluidFlowD2Q9 model;
    public TemperatureD2Q9 temperatureModel;
    private FluidBoundaryType fluidBoundaryType;
    private TempBoundaryType tempBoundaryType;
    public float density;
    public Velocity velocity;
    public float temperature;



    public Cell(int x, int y, float density, Velocity velocity, FluidBoundaryType fluidBoundaryType, TempBoundaryType tempBoundaryType, FluidFlowD2Q9 model, TemperatureD2Q9 temperatureModel) {
        this.x = x;
        this.y = y;
        this.model = model;
        this.temperatureModel = temperatureModel;
        this.fluidBoundaryType = fluidBoundaryType;
        this.tempBoundaryType = tempBoundaryType;
        this.density = density;
        this.temperature = 0f;
        this.velocity = velocity;
    }

    public Cell(int x, int y, float density, float temperature, Velocity velocity, FluidBoundaryType fluidBoundaryType, TempBoundaryType tempBoundaryType, FluidFlowD2Q9 model, TemperatureD2Q9 temperatureModel) {
        this.x = x;
        this.y = y;
        this.model = model;
        this.temperatureModel = temperatureModel;
        this.fluidBoundaryType = fluidBoundaryType;
        this.tempBoundaryType = tempBoundaryType;
        this.density = density;
        this.temperature = temperature;
        this.velocity = velocity;
    }

    public Cell(Cell cell) {
        this.x = cell.x;
        this.y = cell.y;
        this.fluidBoundaryType = cell.getFluidBoundaryType();
        this.tempBoundaryType = cell.getTempBoundaryType();
        this.density = cell.density;
        this.model = new FluidFlowD2Q9((FluidFlowD2Q9) cell.model);
        this.temperatureModel = new TemperatureD2Q9((TemperatureD2Q9) cell.temperatureModel);
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
            t += temperatureModel.getTin().get(i);
        }
        return t;
    }

    private Velocity calcVelocity() {
        float ux = 0f, uy = 0f;
        for (int i = 0; i < 9; i++) {
            ux += model.getFin().get(i) * FluidFlowD2Q9.c.get(i).get(0);
            uy += model.getFin().get(i) * FluidFlowD2Q9.c.get(i).get(1);
        }
        ux /= this.density;
        uy /= this.density;
        return new Velocity(ux, uy);
    }

    public void calcMacroscopicValues() {
        this.density = calcDensity();
        //this.temperature = calcTemperature();
        this.velocity = calcVelocity();
    }

    public void calcMacroscopicTemperature() {
        this.temperature = calcTemperature();
    }


    public FluidBoundaryType getFluidBoundaryType() {
        return this.fluidBoundaryType;
    }

    public TempBoundaryType getTempBoundaryType() {
        return this.tempBoundaryType;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "x=" + x +
                ", y=" + y +
                ", fluidBoundaryType=" + fluidBoundaryType +
                ", tempBoundaryType=" + tempBoundaryType +
                ", density=" + density +
                ", velocity=" + velocity +
                ", temperature=" + temperature +
                '}';
    }
}
