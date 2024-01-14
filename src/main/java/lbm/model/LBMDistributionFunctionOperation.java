package lbm.model;

import lbm.Cell;
import util.Velocity;

import java.util.ArrayList;
import java.util.List;

public interface LBMDistributionFunctionOperation {
    void calcInputFunctions(List<Float> inFunction);
    void calcEquilibriumFunctions(Velocity velocity, float variable);
    void calcOutputFunctions(ArrayList<Float> inFunction, ArrayList<Float> eqFunction, float time, float tau, Force force);
    void calcStreaming(List<Cell> cells);
    void calcBoundaryConditions(Cell cell);
}
