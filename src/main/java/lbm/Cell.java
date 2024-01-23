package lbm;


import lbm.boundary.BoundaryDirection;
import lbm.boundary.CellBoundaryType;
import lbm.boundary.FluidBoundaryType;
import lbm.boundary.TempBoundaryType;
import lbm.model.*;
import lbm.force.BuoyancyForceD2Q9;
import lbm.force.GravityForceD2Q9;
import util.Velocity;

import java.util.ArrayList;
import java.util.List;


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
        if (this.x == 0 && this.y == 0) isHeatSource = true;
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

    public void calcEquilibriumFunction() {
        this.model.calcEquilibriumFunctions(this.velocity, this.density);
        this.temperatureModel.calcEquilibriumFunctions(this.velocity, this.temperature);
    }

    public void calcCollisionOperation(float timeStep, float tau, float tempTau, float gravity, float beta) {
        this.model.calcOutputFunctions((ArrayList<Float>) this.model.fin,
                (ArrayList<Float>) this.model.feq,
                timeStep,
                tau,
                //new GravityForceD2Q9(density, gravity));
                new BuoyancyForceD2Q9(density, gravity, beta, temperature, 0f));
                this.temperatureModel.calcOutputFunctions((ArrayList<Float>) this.temperatureModel.fin,
                (ArrayList<Float>) this.temperatureModel.feq,
                timeStep,
                tempTau,
                new GravityForceD2Q9());
    }

    public void calcStreaming(List<Cell> neighbourhood) {
        model.calcStreaming(neighbourhood);
        temperatureModel.calcStreaming(neighbourhood);
    }

    public void calcBC() {
        model.calcBoundaryConditions(this);
        temperatureModel.calcBoundaryConditions(this);
    }

    private float calcDensity() {
        float d = 0;
        for (int i = 0; i < 9; i++) {
            d += model.fin.get(i);
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

    private Velocity calcVelocity() {
        float ux = 0f, uy = 0f;
        for (int i = 0; i < 9; i++) {
            ux += model.fin.get(i) * ModelD2Q9.c.get(i).get(0);
            uy += model.fin.get(i) * ModelD2Q9.c.get(i).get(1);
        }
        ux /= this.density;
        uy /= this.density;
        return new Velocity(ux, uy);
    }

    public void calcMacroscopicValues() {
        this.density = calcDensity();
        if (!isHeatSource) this.temperature = calcTemperature();
        else {
            for (int i =0 ; i < 9; i++) temperatureModel.feq.set(i, temperature * ModelD2Q9.w.get(i));
            temperatureModel.calcInputFunctions(temperatureModel.feq);
        }
        this.velocity = calcVelocity();
    }

    public CellBoundaryType getCellBoundaryType() {
        return cellBoundaryType;
    }


    @Override
    public String toString() {
        return "Cell{" +
                "x=" + x +
                ", y=" + y +
                ", density=" + density +
                ", velocity=" + velocity +
                ", temperature=" + temperature +
                ", cellBoundaryType=[" + cellBoundaryType.getFluidBoundaryType() +
                ", " + cellBoundaryType.getTempBoundaryType() +
                ", " + cellBoundaryType.getBoundaryDirection() +
                "]}";
    }
}
