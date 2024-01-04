package lbm;

import lbm.boundary.BoundaryDirection;
import lbm.boundary.CellBoundaryType;
import lbm.boundary.FluidBoundaryType;
import lbm.boundary.TempBoundaryType;
import lbm.model.FluidFlowD2Q9;
import lbm.model.ModelD2Q9;
import lbm.model.TemperatureD2Q9;
import util.Velocity;

public class CellBuilder {
    private int x;
    private int y;
    private ModelD2Q9 model;
    private ModelD2Q9 temperatureModel;
    private CellBoundaryType cellBoundaryType;
    private float density;
    private Velocity velocity;
    private float temperature;
    private boolean isHeatSource;

    private CellBuilder() {
        this.model = new FluidFlowD2Q9();
        this.temperatureModel = new TemperatureD2Q9();
        this.cellBoundaryType = new CellBoundaryType(FluidBoundaryType.FLUID, TempBoundaryType.FLUID, BoundaryDirection.NONE);
        this.density = 1f;
        this.temperature = 0f;
        this.velocity = new Velocity(0f,0f);
        this.isHeatSource = false;
    }

    public static CellBuilder cell() {
        return new CellBuilder();
    }

    public CellBuilder withPosition(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }





}
