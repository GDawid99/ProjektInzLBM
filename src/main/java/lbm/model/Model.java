package lbm.model;

import lbm.Cell;
import util.Velocity;

import java.util.ArrayList;
import java.util.List;

public abstract class Model {
    protected List<Float> fin;
    protected List<Float> feq;
    protected List<Float> fout;
    protected List<Float> tin;
    protected List<Float> teq;
    protected List<Float> tout;
    protected List<Float> neighbourhoodFoutElements;
    protected List<Float> neighbourhoodToutElements;

    public abstract float calcDensity();
    public abstract float calcTemperature();
    public abstract Velocity calcVelocity(float density);
    public abstract void calcFinFunctions(List<Float> f);
    public abstract void calcFeqFunctions(Velocity velocity, float density);
    public abstract void calcFoutFunctions(ArrayList<Float> fin, ArrayList<Float> feq, float time, float tau);
    public abstract void calcTinFunctions(List<Float> t);
    public abstract void calcTeqFunctions(Velocity velocity, float temperature);
    public abstract void calcToutFunctions(ArrayList<Float> tin, ArrayList<Float> teq, float time, float tau);
    public abstract void calcStreamingAndBoundaryConditions(List<Cell> cells);

    public List<Float> getFin() {
        return fin;
    }

    public List<Float> getFeq() {
        return feq;
    }

    public List<Float> getFout() {
        return fout;
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
