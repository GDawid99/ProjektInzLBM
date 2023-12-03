package lbm.model;

import lbm.Cell;
import lbm.boundary.BoundaryDirection;
import lbm.boundary.FluidBoundaryType;
import lbm.boundary.TempBoundaryType;
import util.Velocity;

import java.util.ArrayList;
import java.util.List;

public abstract class Model {
    protected List<Float> neighbourhoodOutFunctionElements;
    protected float scalar_prod(ArrayList<? extends Number> a, Velocity u) {
        return a.get(0).floatValue()*u.ux + a.get(1).floatValue()*u.uy;
    }

    public abstract float calc(ArrayList<Integer> c_i, Velocity u, float w_i , float variable);
    public abstract void calcInputFunctions(List<Float> inFunction);
    public abstract void calcEquilibriumFunctions(Velocity velocity, float variable);
    public abstract void calcOutputFunctions(ArrayList<Float> inFunction, ArrayList<Float> eqFunction, float time, float tau);
    public abstract void calcStreaming(List<Cell> cells);
    public abstract void calcBoundaryConditions(FluidBoundaryType fluidBC, TempBoundaryType tempBC, BoundaryDirection direction, Velocity v, float variable);
}
