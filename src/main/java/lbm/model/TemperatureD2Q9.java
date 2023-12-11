package lbm.model;

import lbm.Cell;
import lbm.GlobalValues;
import lbm.boundary.BoundaryDirection;
import lbm.boundary.FluidBoundaryType;
import lbm.boundary.TempBoundaryType;
import util.Velocity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TemperatureD2Q9 extends Model{

    public List<Float> tin;
    public List<Float> teq;
    public List<Float> tout;
    
    public TemperatureD2Q9() {
        this.tin = new ArrayList<>(9);
        this.teq = new ArrayList<>(9);
        this.tout = new ArrayList<>(9);
        this.neighbourhoodOutFunctionElements = new LinkedList<>();
    }

    public TemperatureD2Q9(TemperatureD2Q9 temperatureModel) {
        this.tin = new ArrayList<>(temperatureModel.getTin());
        this.teq = new ArrayList<>(temperatureModel.getTeq());
        this.tout = new ArrayList<>(temperatureModel.getTout());
        this.neighbourhoodOutFunctionElements = new LinkedList<>(temperatureModel.neighbourhoodOutFunctionElements);
    }

    @Override
    public float calc(ArrayList<Integer> c_i, Velocity u, float w_i , float variable) {
        return w_i * variable * (1f + (3f * scalar_prod(c_i, u)));
    }

    @Override
    public void calcInputFunctions(List<Float> f) {
        tin.clear();
        for (int i = 0; i < 9; i++) {
            this.tin.add(f.get(i));
        }
    }

    @Override
    public void calcEquilibriumFunctions(Velocity velocity, float temperature) {
        teq.clear();
        for (int i = 0; i < 9; i++) {
            teq.add(calc(FluidFlowD2Q9.c.get(i),velocity, FluidFlowD2Q9.w.get(i),temperature));
        }
    }

    @Override
    public void calcOutputFunctions(ArrayList<Float> inFunction, ArrayList<Float> eqFunction, float time, float tau) {
        tout.clear();
        for (int i = 0; i < 9; i++) {
            tout.add(inFunction.get(i) + time*((eqFunction.get(i) - inFunction.get(i))/tau));
        }
    }

    @Override
    public void calcStreaming(List<Cell> cells) {
        neighbourhoodOutFunctionElements.clear();
        neighbourhoodOutFunctionElements.add(cells.get(0).temperatureModel.getTout().get(0));
        for (int i = 1; i < 9; i++) {
            if (cells.get(i) == null || cells.get(i).getTempBoundaryType() == TempBoundaryType.WALL) {
                neighbourhoodOutFunctionElements.add(null);
            }
            else {
                neighbourhoodOutFunctionElements.add(cells.get(i).temperatureModel.getTout().get(i));
            }
        }
        cells.get(0).temperatureModel.calcInputFunctions(neighbourhoodOutFunctionElements);
    }

    @Override
    public void calcBoundaryConditions(FluidBoundaryType fluidBC, TempBoundaryType tempBC, BoundaryDirection direction, Velocity v, float temperature) {
        float alphaT = 1;
        switch (tempBC) {
            case CONST_BC -> calcInputFunctions(tout);
            case BOUNCE_BACK_BC -> {
                switch (direction) {
                    case NORTH -> {
                        tin.set(4, tout.get(8));
                        tin.set(5, tout.get(1));
                        tin.set(6, tout.get(2));
                    }
                    case SOUTH -> {
                        tin.set(8, tout.get(4));
                        tin.set(1, tout.get(5));
                        tin.set(2, tout.get(6));
                    }
                    case WEST -> {
                        tin.set(2, tout.get(6));
                        tin.set(3, tout.get(7));
                        tin.set(4, tout.get(8));
                    }
                    case EAST -> {
                        tin.set(6, tout.get(2));
                        tin.set(7, tout.get(3));
                        tin.set(8, tout.get(4));
                    }
                    case NORTHWEST -> {
                        tin.set(2, tout.get(8));
                        tin.set(3, tout.get(7));
                        tin.set(4, tout.get(8));
                        tin.set(5, tout.get(1));
                        tin.set(6, tout.get(8));
                    }
                    case NORTHEAST -> {
                        tin.set(4, tout.get(2));
                        tin.set(5, tout.get(1));
                        tin.set(6, tout.get(2));
                        tin.set(7, tout.get(3));
                        tin.set(8, tout.get(2));
                    }
                    case SOUTHWEST -> {
                        tin.set(8, tout.get(6));
                        tin.set(1, tout.get(5));
                        tin.set(2, tout.get(6));
                        tin.set(3, tout.get(7));
                        tin.set(4, tout.get(6));
                    }
                    case SOUTHEAST -> {
                        tin.set(6, tout.get(4));
                        tin.set(7, tout.get(3));
                        tin.set(8, tout.get(4));
                        tin.set(1, tout.get(5));
                        tin.set(2, tout.get(4));
                    }
                    case NORTHWEST_TYPE2 -> {
                        tin.set(8, tout.get(4));
                    }
                    case NORTHEAST_TYPE2 -> {
                        tin.set(2, tout.get(6));
                    }
                }
            }
            case SYMMETRY_BC -> {
                switch (direction) {
                    case NORTH -> {
                        tin.set(4, tout.get(2));
                        tin.set(5, tout.get(1));
                        tin.set(6, tout.get(8));
                    }
                    case SOUTH -> {
                        tin.set(8, tout.get(6));
                        tin.set(1, tout.get(5));
                        tin.set(2, tout.get(4));
                    }
                    case WEST -> {
                        tin.set(2, tout.get(8));
                        tin.set(3, tout.get(7));
                        tin.set(4, tout.get(6));
                    }
                    case EAST -> {
                        tin.set(6, tout.get(4));
                        tin.set(7, tout.get(3));
                        tin.set(8, tout.get(2));
                    }
                    case NORTHWEST -> {
                        tin.set(2, tout.get(8));
                        tin.set(3, tout.get(7));
                        tin.set(4, tout.get(8));
                        tin.set(5, tout.get(1));
                        tin.set(6, tout.get(8));
                    }
                    case NORTHEAST -> {
                        tin.set(4, tout.get(2));
                        tin.set(5, tout.get(1));
                        tin.set(6, tout.get(2));
                        tin.set(7, tout.get(3));
                        tin.set(8, tout.get(2));
                    }
                    case SOUTHWEST -> {
                        tin.set(8, tout.get(6));
                        tin.set(1, tout.get(5));
                        tin.set(2, tout.get(6));
                        tin.set(3, tout.get(7));
                        tin.set(4, tout.get(6));
                    }
                    case SOUTHEAST -> {
                        tin.set(6, tout.get(4));
                        tin.set(7, tout.get(3));
                        tin.set(8, tout.get(4));
                        tin.set(1, tout.get(5));
                        tin.set(2, tout.get(4));
                    }
                }
            }
            case OPEN_TEMPERATURE_BC -> {
                switch (direction) {
                    case NORTH -> {
                        alphaT = 1f;
                        tin.set(4, (1 - alphaT) * tin.get(8) + alphaT * (temperature -  tin.get(8)));
                        tin.set(5, (1 - alphaT) * tin.get(1) + alphaT * (4f * temperature -  tin.get(1)));
                        tin.set(6, (1 - alphaT) * tin.get(2) + alphaT * (temperature -  tin.get(2)));
                    }
                    case SOUTH -> {
                        alphaT = 0f;
                        tin.set(8, (1 - alphaT) * tin.get(4) + alphaT * (temperature -  tin.get(4)));
                        tin.set(1, (1 - alphaT) * tin.get(5) + alphaT * (4f * temperature -  tin.get(5)));
                        tin.set(2, (1 - alphaT) * tin.get(6) + alphaT * (temperature -  tin.get(6)));
                    }
                    case WEST -> {
                        alphaT = 1f;
                        tin.set(2, (1 - alphaT) * tin.get(6) + alphaT * (temperature -  tin.get(6)));
                        tin.set(3, (1 - alphaT) * tin.get(7) + alphaT * (4f * temperature -  tin.get(7)));
                        tin.set(4, (1 - alphaT) * tin.get(8) + alphaT * (temperature -  tin.get(8)));
                    }
                    case EAST -> {
                        alphaT = 1f;
                        tin.set(6, (1 - alphaT) * tin.get(2) + alphaT * (temperature -  tin.get(2)));
                        tin.set(7, (1 - alphaT) * tin.get(3) + alphaT * (4f * temperature -  tin.get(3)));
                        tin.set(8, (1 - alphaT) * tin.get(4) + alphaT * (temperature -  tin.get(4)));
                    }
                }
            }
        }
        if (fluidBC == FluidBoundaryType.OPEN_DENSITY_BC) {
            switch (direction) {
                case NORTH -> {
                    temperature = ( tin.get(0) +  tin.get(3) +  tin.get(7)
                            + 2 * tin.get(8) + 2 * tin.get(1) + 2 * tin.get(2))/(1 + v.uy);
                    tin.set(4, tin.get(8) - temperature * v.uy/6);
                    tin.set(5, tin.get(1) - 2 * temperature * v.uy/3);
                    tin.set(6, tin.get(2) - temperature * v.uy/6);
                }
                case SOUTH -> {
                    temperature = ( tin.get(0) +  tin.get(3) +  tin.get(7)
                            + 2 * tin.get(4) + 2 * tin.get(5) + 2 * tin.get(6))/(1 - v.uy);
                    tin.set(8, tin.get(4) + temperature * v.uy/6);
                    tin.set(1, tin.get(5) + 2 * temperature * v.uy/3);
                    tin.set(2, tin.get(6) + temperature * v.uy/6);
                }
                case WEST -> {
                    temperature = ( tin.get(0) +  tin.get(1) +  tin.get(5)
                            + 2 * tin.get(6) + 2 * tin.get(7) + 2 * tin.get(8))/(1 - v.ux);
                    tin.set(2, tin.get(6) + temperature * v.ux/6);
                    tin.set(3, tin.get(7) + 2 * temperature * v.ux/3);
                    tin.set(4, tin.get(8) + temperature * v.ux/6);}
                case EAST -> {
                    temperature = ( tin.get(0) +  tin.get(1) +  tin.get(5)
                            + 2 * tin.get(2) + 2 * tin.get(3) + 2 * tin.get(4))/(1 + v.ux);
                    tin.set(6, tin.get(2) - temperature * v.ux/6);
                    tin.set(7, tin.get(3) - 2 * temperature * v.ux/3);
                    tin.set(8, tin.get(4) - temperature * v.ux/6);
                }
            }
        }
    }
    public List<Float> getTin() {
        return tin;
    }

    public List<Float> getTeq() {
        return teq;
    }

    public List<Float> getTout() {
        return tout;
    }
}
