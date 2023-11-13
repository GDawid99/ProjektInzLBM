package lbm.model;

import lbm.Cell;
import lbm.CellState;
import util.Velocity;

import java.util.*;

public class D2Q9 extends Model {
    public D2Q9() {
        //wartości wejściowej funkcji rozkładu dla każdego kierunku komórki (model D2Q9)
        this.fin = new ArrayList<>(9);
        //wartości równowagowej funkcji rozkładu dla każdego kierunku komórki (model D2Q9)
        this.feq = new ArrayList<>(9);
        //wartości wyjściowej funkcji rozkładu dla każdego kierunku komórki (model D2Q9)
        this.fout = new ArrayList<>(9);
        //współczynniki wag (model D2Q9)
        this.w = new ArrayList<>(Arrays.asList(4f/9f,1f/9f,1f/36f,1f/9f,1f/36f,1f/9f,1f/36f,1f/9f,1f/36f));
        //wektory kierunkowe w modelu D2Q9
        this.c = new ArrayList<>(List.of(
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
        this.neighbourhoodFoutElements = new LinkedList<>();
    }

    public D2Q9(D2Q9 model) {
        this.fin = new ArrayList<>(model.getFin());
        //wartości równowagowej funkcji rozkładu dla każdego kierunku komórki (model D2Q9)
        this.feq = new ArrayList<>(model.getFeq());
        //wartości wyjściowej funkcji rozkładu dla każdego kierunku komórki (model D2Q9)
        this.fout = new ArrayList<>(model.getFout());
        //współczynniki wag (model D2Q9)
        this.w = new ArrayList<>(Arrays.asList(4f/9f,1f/9f,1f/36f,1f/9f,1f/36f,1f/9f,1f/36f,1f/9f,1f/36f));
        //wektory kierunkowe w modelu D2Q9
        this.c = new ArrayList<>(List.of(
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
    }

    private float scalar_prod(ArrayList<? extends Number> a, Velocity u) {
        return a.get(0).floatValue()*u.ux + a.get(1).floatValue()*u.uy;
    }

    private float calc(ArrayList<Integer> c_i, Velocity u, float w_i , float density) {
        return w_i * density * (1f+ (3f * scalar_prod(c_i, u)) + (4.5f * (float)Math.pow(scalar_prod(c_i,u),2)) - (1.5f * (float)Math.pow((u.ux + u.uy),2)));
    }

    public float calcDensity() {
        float d = 0f;
        for (int i =0 ; i < 9; i++) {
            d += this.fin.get(i);
        }
        return d;
    }

    public Velocity calcVelocity(float density) {
        float ux = 0f, uy = 0f;
        for (int i = 0; i < 9; i++) {
            ux += this.fin.get(i) * this.c.get(i).get(0);
            uy += this.fin.get(i) * this.c.get(i).get(1);
        }
        ux /= density;
        uy /= density;

        return new Velocity(ux, uy);
    }

    @Override
    public void calcFinFunctions(List<Float> f) {
        fin.clear();
        for (int i = 0; i < 9; i++) {
            this.fin.add(f.get(i));
        }
    }

    @Override
    public void calcFeqFunctions(Velocity velocity, float density) {
        feq.clear();
        for (int i = 0; i < 9; i++) {
            feq.add(calc(c.get(i),velocity,w.get(i),density));
        }
    }

    @Override
    public void calcFoutFunctions(ArrayList<Float> fin, ArrayList<Float> feq, float time, float tau) {
        fout.clear();
        for (int i = 0; i < 9; i++) {
            fout.add(fin.get(i) + time*((feq.get(i) - fin.get(i))/tau));
        }
    }

    @Override
    public void calcStreamingAndBoundaryConditions(List<Cell> cells) {
        if (cells.get(0).getCellState() != CellState.FLUID) {
            cells.get(0).model.calcFinFunctions(cells.get(0).model.getFout());
        }
        else {
            neighbourhoodFoutElements.clear();
            neighbourhoodFoutElements.add(cells.get(0).model.getFout().get(0));
            for (int i = 1; i < 9; i++) {
                if (cells.get(i) == null) continue;
                switch (cells.get(i).getCellState()) {
                    case FLUID, CONST_BC:
                        neighbourhoodFoutElements.add(cells.get(i).model.getFout().get(i));
                        break;
                    case SYMMETRY_BC:
                    case BOUNCE_BACK_BC:
                        if (i < 5) neighbourhoodFoutElements.add(cells.get(0).model.getFout().get(i + 4));
                        else neighbourhoodFoutElements.add(cells.get(0).model.getFout().get(i - 4));
                        break;
                }
            }
            cells.get(0).model.calcFinFunctions(neighbourhoodFoutElements);
        }
    }

}