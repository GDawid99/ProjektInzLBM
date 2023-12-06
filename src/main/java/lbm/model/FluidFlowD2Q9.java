package lbm.model;

import lbm.Cell;
import lbm.boundary.BoundaryDirection;
import lbm.boundary.FluidBoundaryType;
import lbm.GlobalValues;
import lbm.boundary.TempBoundaryType;
import util.Velocity;

import java.util.*;

public class FluidFlowD2Q9 extends Model {
    public static final List<ArrayList<Integer>> c;
    public static final List<Float> w;

    public List<Float> fin;
    public List<Float> feq;
    public List<Float> fout;

    public FluidFlowD2Q9() {
        this.fin = new ArrayList<>(9);
        this.feq = new ArrayList<>(9);
        this.fout = new ArrayList<>(9);
        this.neighbourhoodOutFunctionElements = new LinkedList<>();
    }

    public FluidFlowD2Q9(FluidFlowD2Q9 model) {
        this.fin = new ArrayList<>(model.getFin());
        this.feq = new ArrayList<>(model.getFeq());
        this.fout = new ArrayList<>(model.getFout());
        this.neighbourhoodOutFunctionElements = new LinkedList<>(model.neighbourhoodOutFunctionElements);
    }

    @Override
    public float calc(ArrayList<Integer> c_i, Velocity u, float w_i , float variable) {
        return w_i * variable * (1f + (3f * scalar_prod(c_i, u)) + (4.5f * scalar_prod(c_i,u) * scalar_prod(c_i,u)) - (1.5f * (u.ux*u.ux + u.uy*u.uy)));
    }

    @Override
    public void calcInputFunctions(List<Float> f) {
        fin.clear();
        for (int i = 0; i < 9; i++) {
            this.fin.add(f.get(i));
        }
    }

    @Override
    public void calcEquilibriumFunctions(Velocity velocity, float density) {
        feq.clear();
        for (int i = 0; i < 9; i++) {
            feq.add(calc(c.get(i),velocity,w.get(i),density));
        }
    }

    @Override
    public void calcOutputFunctions(ArrayList<Float> inFunction, ArrayList<Float> eqFunction, float time, float tau) {
        fout.clear();
        for (int i = 0; i < 9; i++) {
            fout.add(inFunction.get(i) + time*((eqFunction.get(i) - inFunction.get(i))/tau));
        }
    }

    @Override
    public void calcStreaming(List<Cell> cells) {
        neighbourhoodOutFunctionElements.clear();
        neighbourhoodOutFunctionElements.add(cells.get(0).model.getFout().get(0));
        for (int i = 1; i < 9; i++) {
            if (cells.get(i) == null || cells.get(i).getFluidBoundaryType() == FluidBoundaryType.WALL) {
                neighbourhoodOutFunctionElements.add(null);
            }
            else {
                neighbourhoodOutFunctionElements.add(cells.get(i).model.getFout().get(i));
            }
        }
        cells.get(0).model.calcInputFunctions(neighbourhoodOutFunctionElements);
    }

    @Override
    public void calcBoundaryConditions(FluidBoundaryType fluidBC, TempBoundaryType tempBC, BoundaryDirection direction, Velocity v, float density) {
        switch (fluidBC) {
            case CONST_BC -> calcInputFunctions(fout);
            case BOUNCE_BACK_BC -> {
                switch (direction) {
                    case NORTH -> {
                        fin.set(4, fout.get(8));
                        fin.set(5, fout.get(1));
                        fin.set(6, fout.get(2));
                    }
                    case SOUTH -> {
                        fin.set(8, fout.get(4));
                        fin.set(1, fout.get(5));
                        fin.set(2, fout.get(6));
                    }
                    case WEST -> {
                        fin.set(2, fout.get(6));
                        fin.set(3, fout.get(7));
                        fin.set(4, fout.get(8));
                    }
                    case EAST -> {
                        fin.set(6, fout.get(2));
                        fin.set(7, fout.get(3));
                        fin.set(8, fout.get(4));
                    }
                    case NORTHWEST -> {
                        fin.set(2, fout.get(8));
                        fin.set(3, fout.get(7));
                        fin.set(4, fout.get(8));
                        fin.set(5, fout.get(1));
                        fin.set(6, fout.get(8));
                    }
                    case NORTHEAST -> {
                        fin.set(4, fout.get(2));
                        fin.set(5, fout.get(1));
                        fin.set(6, fout.get(2));
                        fin.set(7, fout.get(3));
                        fin.set(8, fout.get(2));
                    }
                    case SOUTHWEST -> {
                        fin.set(8, fout.get(6));
                        fin.set(1, fout.get(5));
                        fin.set(2, fout.get(6));
                        fin.set(3, fout.get(7));
                        fin.set(4, fout.get(6));
                    }
                    case SOUTHEAST -> {
                        fin.set(6, fout.get(4));
                        fin.set(7, fout.get(3));
                        fin.set(8, fout.get(4));
                        fin.set(1, fout.get(5));
                        fin.set(2, fout.get(4));
                    }
                    case NORTHWEST_TYPE2 -> {
                        fin.set(8, fout.get(4));
                    }
                    case NORTHEAST_TYPE2 -> {
                        fin.set(2, fout.get(6));
                    }
                }
            }
            case SYMMETRY_BC -> {
                switch (direction) {
                    case NORTH -> {
                        fin.set(4, fout.get(2));
                        fin.set(5, fout.get(1));
                        fin.set(6, fout.get(8));
                    }
                    case SOUTH -> {
                        fin.set(8, fout.get(6));
                        fin.set(1, fout.get(5));
                        fin.set(2, fout.get(4));
                    }
                    case WEST -> {
                        fin.set(2, fout.get(8));
                        fin.set(3, fout.get(7));
                        fin.set(4, fout.get(6));
                    }
                    case EAST -> {
                        fin.set(6, fout.get(4));
                        fin.set(7, fout.get(3));
                        fin.set(8, fout.get(2));
                    }
                    case NORTHWEST -> {
                        fin.set(2, fout.get(8));
                        fin.set(3, fout.get(7));
                        fin.set(4, fout.get(8));
                        fin.set(5, fout.get(1));
                        fin.set(6, fout.get(8));
                    }
                    case NORTHEAST -> {
                        fin.set(4, fout.get(2));
                        fin.set(5, fout.get(1));
                        fin.set(6, fout.get(2));
                        fin.set(7, fout.get(3));
                        fin.set(8, fout.get(2));
                    }
                    case SOUTHWEST -> {
                        fin.set(8, fout.get(6));
                        fin.set(1, fout.get(5));
                        fin.set(2, fout.get(6));
                        fin.set(3, fout.get(7));
                        fin.set(4, fout.get(6));
                    }
                    case SOUTHEAST -> {
                        fin.set(6, fout.get(4));
                        fin.set(7, fout.get(3));
                        fin.set(8, fout.get(4));
                        fin.set(1, fout.get(5));
                        fin.set(2, fout.get(4));
                    }
                }
            }
            case OPEN_DENSITY_BC -> {
                switch (direction) {
                    case NORTH -> {
                        v.uy = -1 + (fin.get(0) + fin.get(3) + fin.get(7)
                                + 2 * fin.get(8) + 2 * fin.get(1) + 2 * fin.get(2))/density;
                        v.ux = 6*(fin.get(3) - fin.get(7) + fin.get(8) - fin.get(2))/(density*(5+3*v.uy));
                    }
                    case SOUTH -> {
                        v.uy = 1 - (fin.get(0) + fin.get(3) + fin.get(7)
                                + 2 * fin.get(4) + 2 * fin.get(5) + 2 * fin.get(6))/density;
                        v.ux = 6*(fin.get(3) - fin.get(7) + fin.get(6) - fin.get(4))/(density*(5-3*v.uy));
                    }
                    case WEST -> {
                        v.ux = 1 - (fin.get(0) + fin.get(1) + fin.get(5)
                                + 2 * fin.get(6) + 2 * fin.get(7) + 2 * fin.get(8))/density;
                        v.uy = 6*(fin.get(1) - fin.get(5) + fin.get(8) - fin.get(6))/(density*(5-3*v.ux));
                    }
                    case EAST -> {
                        v.ux = -1 + (fin.get(0) + fin.get(1) + fin.get(5)
                                + 2 * fin.get(2) + 2 * fin.get(3) + 2 * fin.get(4))/density;
                        v.uy = 6*(fin.get(1) - fin.get(5) + fin.get(2) - fin.get(4))/(density*(5+3*v.ux));
                    }
                }
            }
            case OPEN_VELOCITY_BC -> {
                switch (direction) {
                    case NORTH -> {
                        density = (fin.get(0) + fin.get(3) + fin.get(7)
                                + 2 * fin.get(8) + 2 * fin.get(1) + 2 * fin.get(2))/(1 + v.uy);
                        v.ux = 6*(fin.get(3) - fin.get(7) + fin.get(8) - fin.get(2))/(density*(5+3*v.uy));
                    }
                    case SOUTH -> {
                        density = (fin.get(0) + fin.get(3) + fin.get(7)
                                + 2 * fin.get(4) + 2 * fin.get(5) + 2 * fin.get(6))/(1 - v.uy);
                        v.ux = 6*(fin.get(3) - fin.get(7) + fin.get(6) - fin.get(4))/(density*(5-3*v.uy));
                    }
                    case WEST -> {
                        density = (fin.get(0) + fin.get(1) + fin.get(5)
                                + 2 * fin.get(6) + 2 * fin.get(7) + 2 * fin.get(8))/(1 - v.ux);
                        v.uy = 6*(fin.get(1) - fin.get(5) + fin.get(8) - fin.get(6))/(density*(5-3*v.ux));
                    }
                    case EAST -> {
                        density = (fin.get(0) + fin.get(1) + fin.get(5)
                                + 2 * fin.get(2) + 2 * fin.get(3) + 2 * fin.get(4))/(1 + v.ux);
                        v.uy = 6*(fin.get(1) - fin.get(5) + fin.get(2) - fin.get(4))/(density*(5+3*v.ux));
                    }
                }
            }
        }
        if (fluidBC == FluidBoundaryType.OPEN_VELOCITY_BC || fluidBC == FluidBoundaryType.OPEN_DENSITY_BC) {
            switch (direction) {
                case NORTH -> {
                    fin.set(4,fin.get(8) - density*(v.ux+v.uy)/6);
                    fin.set(5,fin.get(1) - 2*density*v.uy/3);
                    fin.set(6,fin.get(2) - density*(v.ux-v.uy)/6);
                }
                case SOUTH -> {
                    fin.set(8,fin.get(4) + density*(v.ux+v.uy)/6);
                    fin.set(1,fin.get(5) + 2*density*v.uy/3);
                    fin.set(2,fin.get(6) + density*(v.ux-v.uy)/6);
                }
                case WEST -> {
                    fin.set(2,fin.get(6) + density*(v.ux-v.uy)/6);
                    fin.set(3,fin.get(7) + 2*density*v.ux/3);
                    fin.set(4,fin.get(8) + density*(v.ux+v.uy)/6);
                }
                case EAST -> {
                    fin.set(6,fin.get(2) - density*(v.ux-v.uy)/6);
                    fin.set(7,fin.get(3) - 2*density*v.ux/3);
                    fin.set(8,fin.get(4) - density*(v.ux+v.uy)/6);
                }
            }
        }
    }

    public List<Float> getFin() {
        return fin;
    }

    public List<Float> getFeq() {
        return feq;
    }

    public List<Float> getFout() {
        return fout;
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