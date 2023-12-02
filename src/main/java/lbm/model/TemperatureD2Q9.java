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
            if (cells.get(i) == null) {
                neighbourhoodOutFunctionElements.add(null);
            }
            else {
                neighbourhoodOutFunctionElements.add(cells.get(i).temperatureModel.getTout().get(i));
            }
        }
        cells.get(0).temperatureModel.calcInputFunctions(neighbourhoodOutFunctionElements);
    }

    @Override
    public void calcBoundaryConditions(Cell cell) {
        Velocity v = new Velocity(0.0f,0.0f);
        float temperature = 0f;
        float alphaT = 1;
        switch (cell.getTempBoundaryType()) {
            case CONST_BC -> {
                cell.temperatureModel.calcInputFunctions(cell.temperatureModel.getTout());
            }
            case BOUNCE_BACK_BC -> {
//                for (int i = 0; i < 9; i++) {
//                    if (cell.temperatureModel.getFin().get(i) == null) {
                if (cell.getBoundaryDirection() == BoundaryDirection.NORTH) {
                    cell.temperatureModel.tin.set(4,cell.temperatureModel.getTout().get(8));
                    cell.temperatureModel.tin.set(5,cell.temperatureModel.getTout().get(1));
                    cell.temperatureModel.tin.set(6,cell.temperatureModel.getTout().get(2));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.SOUTH) {
                    cell.temperatureModel.tin.set(8,cell.temperatureModel.getTout().get(4));
                    cell.temperatureModel.tin.set(1,cell.temperatureModel.getTout().get(5));
                    cell.temperatureModel.tin.set(2,cell.temperatureModel.getTout().get(6));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.WEST) {
                    cell.temperatureModel.tin.set(2,cell.temperatureModel.getTout().get(6));
                    cell.temperatureModel.tin.set(3,cell.temperatureModel.getTout().get(7));
                    cell.temperatureModel.tin.set(4,cell.temperatureModel.getTout().get(8));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.EAST) {
                    cell.temperatureModel.tin.set(6,cell.temperatureModel.getTout().get(2));
                    cell.temperatureModel.tin.set(7,cell.temperatureModel.getTout().get(3));
                    cell.temperatureModel.tin.set(8,cell.temperatureModel.getTout().get(4));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.NORTHWEST) {
                    cell.temperatureModel.tin.set(2,cell.temperatureModel.getTout().get(8));
                    cell.temperatureModel.tin.set(3,cell.temperatureModel.getTout().get(7));
                    cell.temperatureModel.tin.set(4,cell.temperatureModel.getTout().get(8));
                    cell.temperatureModel.tin.set(5,cell.temperatureModel.getTout().get(1));
                    cell.temperatureModel.tin.set(6,cell.temperatureModel.getTout().get(8));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.NORTHEAST) {
                    cell.temperatureModel.tin.set(4,cell.temperatureModel.getTout().get(2));
                    cell.temperatureModel.tin.set(5,cell.temperatureModel.getTout().get(1));
                    cell.temperatureModel.tin.set(6,cell.temperatureModel.getTout().get(2));
                    cell.temperatureModel.tin.set(7,cell.temperatureModel.getTout().get(3));
                    cell.temperatureModel.tin.set(8,cell.temperatureModel.getTout().get(2));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.SOUTHWEST) {
                    cell.temperatureModel.tin.set(8,cell.temperatureModel.getTout().get(6));
                    cell.temperatureModel.tin.set(1,cell.temperatureModel.getTout().get(5));
                    cell.temperatureModel.tin.set(2,cell.temperatureModel.getTout().get(6));
                    cell.temperatureModel.tin.set(3,cell.temperatureModel.getTout().get(7));
                    cell.temperatureModel.tin.set(4,cell.temperatureModel.getTout().get(6));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.SOUTHEAST) {
                    cell.temperatureModel.tin.set(6,cell.temperatureModel.getTout().get(4));
                    cell.temperatureModel.tin.set(7,cell.temperatureModel.getTout().get(3));
                    cell.temperatureModel.tin.set(8,cell.temperatureModel.getTout().get(4));
                    cell.temperatureModel.tin.set(1,cell.temperatureModel.getTout().get(5));
                    cell.temperatureModel.tin.set(2,cell.temperatureModel.getTout().get(4));
                }
            }
            case OPEN_TEMPERATURE_BC -> {
                //if (cell.getFluidBoundaryType() == FluidBoundaryType.OPEN_DENSITY_BC) temperature = GlobalValues.TEMPERATURE;
                temperature = cell.temperature;
                if (cell.getBoundaryDirection() == BoundaryDirection.NORTH) {
                    alphaT = 1f;
                    cell.temperatureModel.tin.set(4, (1 - alphaT)*cell.temperatureModel.tin.get(8) + alphaT*(temperature - cell.temperatureModel.tin.get(8)));
                    cell.temperatureModel.tin.set(5, (1 - alphaT)*cell.temperatureModel.tin.get(1) + alphaT*(4f*temperature - cell.temperatureModel.tin.get(1)));
                    cell.temperatureModel.tin.set(6, (1 - alphaT)*cell.temperatureModel.tin.get(2) + alphaT*(temperature - cell.temperatureModel.tin.get(2)));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.SOUTH) {
                    alphaT = 0f;
                    cell.temperatureModel.tin.set(8, (1 - alphaT)*cell.temperatureModel.tin.get(4) + alphaT*(temperature - cell.temperatureModel.tin.get(4)));
                    cell.temperatureModel.tin.set(1, (1 - alphaT)*cell.temperatureModel.tin.get(5) + alphaT*(4f*temperature - cell.temperatureModel.tin.get(5)));
                    cell.temperatureModel.tin.set(2, (1 - alphaT)*cell.temperatureModel.tin.get(6) + alphaT*(temperature - cell.temperatureModel.tin.get(6)));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.WEST) {
                    alphaT = 1f;
                    cell.temperatureModel.tin.set(2, (1 - alphaT)*cell.temperatureModel.tin.get(6) + alphaT*(temperature - cell.temperatureModel.tin.get(6)));
                    cell.temperatureModel.tin.set(3, (1 - alphaT)*cell.temperatureModel.tin.get(7) + alphaT*(4f*temperature - cell.temperatureModel.tin.get(7)));
                    cell.temperatureModel.tin.set(4, (1 - alphaT)*cell.temperatureModel.tin.get(8) + alphaT*(temperature - cell.temperatureModel.tin.get(8)));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.EAST) {
                    alphaT = 1f;
                    cell.temperatureModel.tin.set(6, (1 - alphaT)*cell.temperatureModel.tin.get(2) + alphaT*(temperature - cell.temperatureModel.tin.get(2)));
                    cell.temperatureModel.tin.set(7, (1 - alphaT)*cell.temperatureModel.tin.get(3) + alphaT*(4f*temperature - cell.temperatureModel.tin.get(3)));
                    cell.temperatureModel.tin.set(8, (1 - alphaT)*cell.temperatureModel.tin.get(4) + alphaT*(temperature - cell.temperatureModel.tin.get(4)));
                }
            }

        }
        if (cell.getFluidBoundaryType() == FluidBoundaryType.OPEN_DENSITY_BC) {
            if (cell.getBoundaryDirection() == BoundaryDirection.NORTH) {
                temperature = (cell.temperatureModel.tin.get(0) + cell.temperatureModel.tin.get(3) + cell.temperatureModel.tin.get(7)
                        + 2*cell.temperatureModel.tin.get(8) + 2*cell.temperatureModel.tin.get(1) + 2*cell.temperatureModel.tin.get(2))/(1 + cell.velocity.uy);
                cell.temperatureModel.tin.set(4,cell.temperatureModel.tin.get(8) - temperature*cell.velocity.uy/6);
                cell.temperatureModel.tin.set(5,cell.temperatureModel.tin.get(1) - 2*temperature*cell.velocity.uy/3);
                cell.temperatureModel.tin.set(6,cell.temperatureModel.tin.get(2) - temperature*cell.velocity.uy/6);
            }
            else if (cell.getBoundaryDirection() == BoundaryDirection.SOUTH) {
                temperature = (cell.temperatureModel.tin.get(0) + cell.temperatureModel.tin.get(3) + cell.temperatureModel.tin.get(7)
                        + 2*cell.temperatureModel.tin.get(4) + 2*cell.temperatureModel.tin.get(5) + 2*cell.temperatureModel.tin.get(6))/(1 - cell.velocity.uy);
                cell.temperatureModel.tin.set(8,cell.temperatureModel.tin.get(4) + temperature*cell.velocity.uy/6);
                cell.temperatureModel.tin.set(1,cell.temperatureModel.tin.get(5) + 2*temperature*cell.velocity.uy/3);
                cell.temperatureModel.tin.set(2,cell.temperatureModel.tin.get(6) + temperature*cell.velocity.uy/6);
            }
            else if (cell.getBoundaryDirection() == BoundaryDirection.WEST) {
                temperature = (cell.temperatureModel.tin.get(0) + cell.temperatureModel.tin.get(1) + cell.temperatureModel.tin.get(5)
                        + 2*cell.temperatureModel.tin.get(6) + 2*cell.temperatureModel.tin.get(7) + 2*cell.temperatureModel.tin.get(8))/(1 - cell.velocity.ux);
                cell.temperatureModel.tin.set(2,cell.temperatureModel.tin.get(6) + temperature*cell.velocity.ux/6);
                cell.temperatureModel.tin.set(3,cell.temperatureModel.tin.get(7) + 2*temperature*cell.velocity.ux/3);
                cell.temperatureModel.tin.set(4,cell.temperatureModel.tin.get(8) + temperature*cell.velocity.ux/6);
            }
            else if (cell.getBoundaryDirection() == BoundaryDirection.EAST) {
                temperature = (cell.temperatureModel.tin.get(0) + cell.temperatureModel.tin.get(1) + cell.temperatureModel.tin.get(5)
                        + 2*cell.temperatureModel.tin.get(2) + 2*cell.temperatureModel.tin.get(3) + 2*cell.temperatureModel.tin.get(4))/(1 + cell.velocity.ux);
                cell.temperatureModel.tin.set(6,cell.temperatureModel.tin.get(2) - temperature*cell.velocity.ux/6);
                cell.temperatureModel.tin.set(7,cell.temperatureModel.tin.get(3) - 2*temperature*cell.velocity.ux/3);
                cell.temperatureModel.tin.set(8,cell.temperatureModel.tin.get(4) - temperature*cell.velocity.ux/6);
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
