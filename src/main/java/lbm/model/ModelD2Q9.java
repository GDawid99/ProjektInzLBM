package lbm.model;

import lbm.force.Force;
import util.Velocity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class ModelD2Q9 implements LBMDistributionFunctionOperation{
    public float constDensity;
    public Velocity constVelocity;
    public float constTemperature;
    public static final List<ArrayList<Integer>> c;
    public static final List<Float> w;
    public List<Float> fin;
    public List<Float> feq;
    public List<Float> fout;
    protected List<Float> neighbourhoodOutFunctionElements;

    protected ModelD2Q9() {
        this.fin = new ArrayList<>(9);
        this.feq = new ArrayList<>(9);
        this.fout = new ArrayList<>(9);
        this.neighbourhoodOutFunctionElements = new LinkedList<>();
    }

    protected ModelD2Q9(ModelD2Q9 model) {
        this.fin = new ArrayList<>(model.fin);
        this.feq = new ArrayList<>(model.feq);
        this.fout = new ArrayList<>(model.fout);
        this.neighbourhoodOutFunctionElements = new LinkedList<>(model.neighbourhoodOutFunctionElements);
    }

    protected float scalar_prod(ArrayList<? extends Number> a, Velocity u) {
        return a.get(0).floatValue()*u.ux + a.get(1).floatValue()*u.uy;
    }

    public abstract float calc(ArrayList<Integer> c_i, Velocity u, float w_i , float variable);

    @Override
    public void calcInputFunctions(List<Float> f) {
        fin.clear();
        for (int i = 0; i < 9; i++) {
            this.fin.add(f.get(i));
        }
    }

    @Override
    public void calcEquilibriumFunctions(Velocity velocity, float variable) {
        feq.clear();
        for (int i = 0; i < 9; i++) {
            feq.add(calc(c.get(i),velocity,w.get(i),variable));
        }
    }

    @Override
    public void calcOutputFunctions(ArrayList<Float> inFunction, ArrayList<Float> eqFunction, float time, float tau, Force force) {
        fout.clear();
        for (int i = 0; i < 9; i++) {
            fout.add(inFunction.get(i) + time*(eqFunction.get(i) - inFunction.get(i))/tau + time*force.calcForce(i));
        }
    }

    static {
        //wektory kierunkowe w modelu D2Q9
        c = new ArrayList<>(List.of(
                new ArrayList<>(Arrays.asList(0,0)),
                new ArrayList<>(Arrays.asList(0,1)),
                new ArrayList<>(Arrays.asList(1,1)),
                new ArrayList<>(Arrays.asList(1,0)),
                new ArrayList<>(Arrays.asList(1,-1)),
                new ArrayList<>(Arrays.asList(0,-1)),
                new ArrayList<>(Arrays.asList(-1,-1)),
                new ArrayList<>(Arrays.asList(-1,0)),
                new ArrayList<>(Arrays.asList(-1,1))
        ));
        //współczynniki wag (model D2Q9)
        w = new ArrayList<>(Arrays.asList(4f/9f,1f/9f,1f/36f,1f/9f,1f/36f,1f/9f,1f/36f,1f/9f,1f/36f));
    }
}
