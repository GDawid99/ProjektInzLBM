package lbm.model;

import lbm.Cell;
import util.Velocity;

import java.util.ArrayList;
import java.util.List;

public abstract class Model {
    protected List<Float> fin;
    protected List<Float> feq;
    protected List<Float> fout;
    protected List<Float> neighbourhoodOutFunctionElements;

    protected float scalar_prod(ArrayList<? extends Number> a, Velocity u) {
        return a.get(0).floatValue()*u.ux + a.get(1).floatValue()*u.uy;
    }

    protected float calc(ArrayList<Integer> c_i, Velocity u, float w_i , float variable) {
        return w_i * variable * (1f + (3f * scalar_prod(c_i, u)) + (4.5f * scalar_prod(c_i,u) * scalar_prod(c_i,u)) - (1.5f * (u.ux*u.ux + u.uy*u.uy)));
    }

    public abstract void calcInputFunctions(List<Float> f);
    public abstract void calcEquilibriumFunctions(Velocity velocity, float variable);
    public abstract void calcOutputFunctions(ArrayList<Float> fin, ArrayList<Float> feq, float time, float tau);
    public abstract void calcStreaming(List<Cell> cells);
    public abstract void calcBoundaryConditions(Cell cell, String direction);

    public List<Float> getFin() {
        return fin;
    }

    public List<Float> getFeq() {
        return feq;
    }

    public List<Float> getFout() {
        return fout;
    }
}
