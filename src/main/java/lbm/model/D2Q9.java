package lbm.model;

import lbm.Cell;
import lbm.CellState;
import util.Velocity;

import java.util.*;

public class D2Q9 extends Model {
    public static final List<ArrayList<Integer>> c;
    public static final List<Float> w;
    public D2Q9() {
        //wartości wejściowej funkcji rozkładu dla każdego kierunku komórki (model D2Q9)
        this.fin = new ArrayList<>(9);
        //wartości równowagowej funkcji rozkładu dla każdego kierunku komórki (model D2Q9)
        this.feq = new ArrayList<>(9);
        //wartości wyjściowej funkcji rozkładu dla każdego kierunku komórki (model D2Q9)
        this.fout = new ArrayList<>(9);
        //wartości wejściowej funkcji rozkładu temperatury dla każdego kierunku komórki (model D2Q9)
        this.tin = new ArrayList<>(9);
        //wartości równowagowej funkcji rozkładu temperatury dla każdego kierunku komórki (model D2Q9)
        this.teq = new ArrayList<>(9);
        //wartości wyjściowej funkcji rozkładu temperatury dla każdego kierunku komórki (model D2Q9)
        this.tout = new ArrayList<>(9);
        this.neighbourhoodFoutElements = new LinkedList<>();
        this.neighbourhoodToutElements = new LinkedList<>();
    }

    public D2Q9(D2Q9 model) {
        this.fin = new ArrayList<>(model.getFin());
        this.feq = new ArrayList<>(model.getFeq());
        this.fout = new ArrayList<>(model.getFout());
        this.tin = new ArrayList<>(model.getTin());
        this.teq = new ArrayList<>(model.getTeq());
        this.tout = new ArrayList<>(model.getTout());
        this.neighbourhoodFoutElements = new LinkedList<>(model.neighbourhoodFoutElements);
        this.neighbourhoodToutElements = new LinkedList<>(model.neighbourhoodToutElements);
    }

    private float scalar_prod(ArrayList<? extends Number> a, Velocity u) {
        return a.get(0).floatValue()*u.ux + a.get(1).floatValue()*u.uy;
    }

    private float calc(ArrayList<Integer> c_i, Velocity u, float w_i , float variable) {
        return w_i * variable * (1f + (3f * scalar_prod(c_i, u)) + (4.5f * scalar_prod(c_i,u) * scalar_prod(c_i,u)) - (1.5f * (u.ux*u.ux + u.uy*u.uy)));
    }

    public float calcDensity() {
        float d = 0f;
        for (int i =0 ; i < 9; i++) {
            if (this.fin.get(i) == null) System.out.println(i);
            d += this.fin.get(i);
        }
        return d;
    }

    public float calcTemperature() {
        float t = 0f;
        for (int i =0 ; i < 9; i++) {
            t += this.tin.get(i);
        }
        return t;
    }

    public Velocity calcVelocity(float density) {
        float ux = 0f, uy = 0f;
        for (int i = 0; i < 9; i++) {
            ux += this.fin.get(i) * c.get(i).get(0);
            uy += this.fin.get(i) * c.get(i).get(1);
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
    public void calcTinFunctions(List<Float> t) {
        tin.clear();
        for (int i = 0; i < 9; i++) {
            this.tin.add(t.get(i));
        }
    }

    @Override
    public void calcTeqFunctions(Velocity velocity, float temperature) {
        teq.clear();
        for (int i = 0; i < 9; i++) {
            teq.add(calc(c.get(i),velocity,w.get(i),temperature));
        }
    }

    @Override
    public void calcToutFunctions(ArrayList<Float> tin, ArrayList<Float> teq, float time, float tau) {
        tout.clear();
        for (int i = 0; i < 9; i++) {
            tout.add(tin.get(i) + time*((teq.get(i) - tin.get(i))/tau));
        }
    }

    @Override
    public void calcStreaming(List<Cell> cells) {
        neighbourhoodFoutElements.clear();
        //neighbourhoodToutElements.clear();
        neighbourhoodFoutElements.add(cells.get(0).model.getFout().get(0));
        //neighbourhoodToutElements.add(cells.get(0).model.getTout().get(0));
        for (int i = 1; i < 9; i++) {
            if (cells.get(i) == null) {
                neighbourhoodFoutElements.add(null);
                //neighbourhoodToutElements.add(null);
            }
            else {
                neighbourhoodFoutElements.add(cells.get(i).model.getFout().get(i));
                //neighbourhoodToutElements.add(cells.get(i).model.getTout().get(i));
            }
        }
        cells.get(0).model.calcFinFunctions(neighbourhoodFoutElements);
        //cells.get(0).model.calcTinFunctions(neighbourhoodToutElements);
    }

    @Override
    public void calcBoundaryConditions(Cell cell, String direction) {
        Velocity v = new Velocity(0.0f,0.0f);
        float density = 1f;
        switch (cell.getCellState()) {
            case CONST_BC -> {
                cell.model.calcFinFunctions(cell.model.getFout());
                //cell.model.calcTinFunctions(cell.model.getTout());
            }
            case BOUNCE_BACK_BC -> {
//                for (int i = 0; i < 9; i++) {
//                    if (cell.model.getFin().get(i) == null) {
                        if (direction.equals("N")) {
                            cell.model.fin.set(4,cell.model.getFout().get(8));
                            cell.model.fin.set(5,cell.model.getFout().get(1));
                            cell.model.fin.set(6,cell.model.getFout().get(2));
                        }
                        else if (direction.equals("S")) {
                            cell.model.fin.set(8,cell.model.getFout().get(4));
                            cell.model.fin.set(1,cell.model.getFout().get(5));
                            cell.model.fin.set(2,cell.model.getFout().get(6));
                        }
                        else if (direction.equals("W")) {
                            cell.model.fin.set(2,cell.model.getFout().get(6));
                            cell.model.fin.set(3,cell.model.getFout().get(7));
                            cell.model.fin.set(4,cell.model.getFout().get(8));
                        }
                        else if (direction.equals("E")) {
                            cell.model.fin.set(6,cell.model.getFout().get(2));
                            cell.model.fin.set(7,cell.model.getFout().get(3));
                            cell.model.fin.set(8,cell.model.getFout().get(4));
                        }
                        else if (direction.equals("NW")) {
                            cell.model.fin.set(2,cell.model.getFout().get(8));
                            cell.model.fin.set(3,cell.model.getFout().get(7));
                            cell.model.fin.set(4,cell.model.getFout().get(8));
                            cell.model.fin.set(5,cell.model.getFout().get(1));
                            cell.model.fin.set(6,cell.model.getFout().get(8));
                        }
                        else if (direction.equals("NE")) {
                            cell.model.fin.set(4,cell.model.getFout().get(2));
                            cell.model.fin.set(5,cell.model.getFout().get(1));
                            cell.model.fin.set(6,cell.model.getFout().get(2));
                            cell.model.fin.set(7,cell.model.getFout().get(3));
                            cell.model.fin.set(8,cell.model.getFout().get(2));
                        }
                        else if (direction.equals("SW")) {
                            cell.model.fin.set(8,cell.model.getFout().get(6));
                            cell.model.fin.set(1,cell.model.getFout().get(5));
                            cell.model.fin.set(2,cell.model.getFout().get(6));
                            cell.model.fin.set(3,cell.model.getFout().get(7));
                            cell.model.fin.set(4,cell.model.getFout().get(6));
                        }
                        else if (direction.equals("SE")) {
                            cell.model.fin.set(6,cell.model.getFout().get(4));
                            cell.model.fin.set(7,cell.model.getFout().get(3));
                            cell.model.fin.set(8,cell.model.getFout().get(4));
                            cell.model.fin.set(1,cell.model.getFout().get(5));
                            cell.model.fin.set(2,cell.model.getFout().get(4));
                        }
//                for (int i = 0; i < 9; i++) {
//                    if (cell.model.getFin().get(i) == null) System.out.println("null DETECTED! x:" + cell.x + ",y:" + cell.y + "; fi=" + i);
//                }
//                    }
//                }
            }
            case OPEN_DENSITY_BC -> {
                density = 1f;
                if (direction.equals("N")) {
                    v.uy = -1 + (cell.model.fin.get(0) + cell.model.fin.get(3) + cell.model.fin.get(7)
                            + 2 * cell.model.fin.get(8) + 2 * cell.model.fin.get(1) + 2 * cell.model.fin.get(2))/density;
                    v.ux = 6*(cell.model.fin.get(3) - cell.model.fin.get(7) + cell.model.fin.get(8) - cell.model.fin.get(2))/(density*(5+3*v.uy));
                }
                else if (direction.equals("S")) {
                    v.uy = 1 - (cell.model.fin.get(0) + cell.model.fin.get(3) + cell.model.fin.get(7)
                            + 2 * cell.model.fin.get(4) + 2 * cell.model.fin.get(5) + 2 * cell.model.fin.get(6))/density;
                    v.ux = 6*(cell.model.fin.get(3) - cell.model.fin.get(7) + cell.model.fin.get(6) - cell.model.fin.get(4))/(density*(5-3*v.uy));
                }
                else if (direction.equals("W")) {
                    v.ux = 1 - (cell.model.fin.get(0) + cell.model.fin.get(1) + cell.model.fin.get(5)
                            + 2 * cell.model.fin.get(6) + 2 * cell.model.fin.get(7) + 2 * cell.model.fin.get(8))/density;
                    v.uy = 6*(cell.model.fin.get(1) - cell.model.fin.get(5) + cell.model.fin.get(8) - cell.model.fin.get(6))/(density*(5-3*v.ux));
                }
                else if (direction.equals("E")) {
                    v.ux = -1 + (cell.model.fin.get(0) + cell.model.fin.get(1) + cell.model.fin.get(5)
                            + 2 * cell.model.fin.get(2) + 2 * cell.model.fin.get(3) + 2 * cell.model.fin.get(4))/density;
                    v.uy = 6*(cell.model.fin.get(1) - cell.model.fin.get(5) + cell.model.fin.get(2) - cell.model.fin.get(4))/(density*(5+3*v.ux));
                }
            }
            case OPEN_VELOCITY_BC -> {
                v = new Velocity(GlobalValues.UX,0f);
                if (direction.equals("N")) {
                    density = (cell.model.fin.get(0) + cell.model.fin.get(3) + cell.model.fin.get(7)
                            + 2 * cell.model.fin.get(8) + 2 * cell.model.fin.get(1) + 2 * cell.model.fin.get(2))/(1 + v.uy);
                    v.ux = 6*(cell.model.fin.get(3) - cell.model.fin.get(7) + cell.model.fin.get(8) - cell.model.fin.get(2))/(density*(5+3*v.uy));
                }
                else if (direction.equals("S")) {
                    density = (cell.model.fin.get(0) + cell.model.fin.get(3) + cell.model.fin.get(7)
                            + 2 * cell.model.fin.get(4) + 2 * cell.model.fin.get(5) + 2 * cell.model.fin.get(6))/(1 - v.uy);
                    v.ux = 6*(cell.model.fin.get(3) - cell.model.fin.get(7) + cell.model.fin.get(6) - cell.model.fin.get(4))/(density*(5-3*v.uy));
                }
                else if (direction.equals("W")) {
                    density = (cell.model.fin.get(0) + cell.model.fin.get(1) + cell.model.fin.get(5)
                            + 2 * cell.model.fin.get(6) + 2 * cell.model.fin.get(7) + 2 * cell.model.fin.get(8))/(1 - v.ux);
                    v.uy = 6*(cell.model.fin.get(1) - cell.model.fin.get(5) + cell.model.fin.get(8) - cell.model.fin.get(6))/(density*(5-3*v.ux));
                }
                else if (direction.equals("E")) {
                    density = (cell.model.fin.get(0) + cell.model.fin.get(1) + cell.model.fin.get(5)
                            + 2 * cell.model.fin.get(2) + 2 * cell.model.fin.get(3) + 2 * cell.model.fin.get(4))/(1 + v.ux);
                    v.uy = 6*(cell.model.fin.get(1) - cell.model.fin.get(5) + cell.model.fin.get(2) - cell.model.fin.get(4))/(density*(5+3*v.ux));
                }
            }
        }
        if (cell.getCellState() == CellState.OPEN_VELOCITY_BC || cell.getCellState() == CellState.OPEN_DENSITY_BC) {
            if (direction.equals("N")) {
                cell.model.fin.set(4,cell.model.fin.get(8) + density*(v.ux+v.uy)/6);
                cell.model.fin.set(5,cell.model.fin.get(1) - 2*density*v.uy/3);
                cell.model.fin.set(6,cell.model.fin.get(2) - density*(v.ux-v.uy)/6);
            }
            else if (direction.equals("S")) {
                cell.model.fin.set(8,cell.model.fin.get(4) + density*(v.ux+v.uy)/6);
                cell.model.fin.set(1,cell.model.fin.get(5) + 2*density*v.uy/3);
                cell.model.fin.set(2,cell.model.fin.get(6) - density*(v.ux-v.uy)/6);
            }
            else if (direction.equals("W")) {
                cell.model.fin.set(2,cell.model.fin.get(6) + density*(v.ux-v.uy)/6);
                cell.model.fin.set(3,cell.model.fin.get(7) + 2*density*v.ux/3);
                cell.model.fin.set(4,cell.model.fin.get(8) + density*(v.ux+v.uy)/6);
            }
            else if (direction.equals("E")) {
                cell.model.fin.set(6,cell.model.fin.get(2) - density*(v.ux-v.uy)/6);
                cell.model.fin.set(7,cell.model.fin.get(3) - 2*density*v.ux/3);
                cell.model.fin.set(8,cell.model.fin.get(4) - density*(v.ux+v.uy)/6);
            }
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