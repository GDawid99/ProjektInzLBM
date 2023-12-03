package lbm.model;

import lbm.Cell;
import lbm.boundary.BoundaryDirection;
import lbm.boundary.FluidBoundaryType;
import lbm.GlobalValues;
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
            if (cells.get(i) == null) {
                neighbourhoodOutFunctionElements.add(null);
            }
            else {
                neighbourhoodOutFunctionElements.add(cells.get(i).model.getFout().get(i));
            }
        }
        cells.get(0).model.calcInputFunctions(neighbourhoodOutFunctionElements);
    }

    @Override
    public void calcBoundaryConditions(Cell cell) {
        Velocity v = new Velocity(0.0f,0.0f);
        float density = 1f;
        switch (cell.getFluidBoundaryType()) {
            case CONST_BC -> {
                cell.model.calcInputFunctions(cell.model.getFout());
            }
            case BOUNCE_BACK_BC -> {
//                for (int i = 0; i < 9; i++) {
//                    if (cell.model.getFin().get(i) == null) {
                        if (cell.getBoundaryDirection() == BoundaryDirection.NORTH) {
                            cell.model.fin.set(4,cell.model.getFout().get(8));
                            cell.model.fin.set(5,cell.model.getFout().get(1));
                            cell.model.fin.set(6,cell.model.getFout().get(2));
                        }
                        else if (cell.getBoundaryDirection() == BoundaryDirection.SOUTH) {
                            cell.model.fin.set(8,cell.model.getFout().get(4));
                            cell.model.fin.set(1,cell.model.getFout().get(5));
                            cell.model.fin.set(2,cell.model.getFout().get(6));
                        }
                        else if (cell.getBoundaryDirection() == BoundaryDirection.WEST) {
                            cell.model.fin.set(2,cell.model.getFout().get(6));
                            cell.model.fin.set(3,cell.model.getFout().get(7));
                            cell.model.fin.set(4,cell.model.getFout().get(8));
                        }
                        else if (cell.getBoundaryDirection() == BoundaryDirection.EAST) {
                            cell.model.fin.set(6,cell.model.getFout().get(2));
                            cell.model.fin.set(7,cell.model.getFout().get(3));
                            cell.model.fin.set(8,cell.model.getFout().get(4));
                        }
                        else if (cell.getBoundaryDirection() == BoundaryDirection.NORTHWEST) {
                            cell.model.fin.set(2,cell.model.getFout().get(8));
                            cell.model.fin.set(3,cell.model.getFout().get(7));
                            cell.model.fin.set(4,cell.model.getFout().get(8));
                            cell.model.fin.set(5,cell.model.getFout().get(1));
                            cell.model.fin.set(6,cell.model.getFout().get(8));
                        }
                        else if (cell.getBoundaryDirection() == BoundaryDirection.NORTHEAST) {
                            cell.model.fin.set(4,cell.model.getFout().get(2));
                            cell.model.fin.set(5,cell.model.getFout().get(1));
                            cell.model.fin.set(6,cell.model.getFout().get(2));
                            cell.model.fin.set(7,cell.model.getFout().get(3));
                            cell.model.fin.set(8,cell.model.getFout().get(2));
                        }
                        else if (cell.getBoundaryDirection() == BoundaryDirection.SOUTHWEST) {
                            cell.model.fin.set(8,cell.model.getFout().get(6));
                            cell.model.fin.set(1,cell.model.getFout().get(5));
                            cell.model.fin.set(2,cell.model.getFout().get(6));
                            cell.model.fin.set(3,cell.model.getFout().get(7));
                            cell.model.fin.set(4,cell.model.getFout().get(6));
                        }
                        else if (cell.getBoundaryDirection() == BoundaryDirection.SOUTHEAST) {
                            cell.model.fin.set(6,cell.model.getFout().get(4));
                            cell.model.fin.set(7,cell.model.getFout().get(3));
                            cell.model.fin.set(8,cell.model.getFout().get(4));
                            cell.model.fin.set(1,cell.model.getFout().get(5));
                            cell.model.fin.set(2,cell.model.getFout().get(4));
                        }
            }
            case SYMMETRY_BC -> {
                if (cell.getBoundaryDirection() == BoundaryDirection.NORTH) {
                    cell.model.fin.set(4,cell.model.getFout().get(2));
                    cell.model.fin.set(5,cell.model.getFout().get(1));
                    cell.model.fin.set(6,cell.model.getFout().get(8));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.SOUTH) {
                    cell.model.fin.set(8,cell.model.getFout().get(6));
                    cell.model.fin.set(1,cell.model.getFout().get(5));
                    cell.model.fin.set(2,cell.model.getFout().get(4));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.WEST) {
                    cell.model.fin.set(2,cell.model.getFout().get(8));
                    cell.model.fin.set(3,cell.model.getFout().get(7));
                    cell.model.fin.set(4,cell.model.getFout().get(6));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.EAST) {
                    cell.model.fin.set(6,cell.model.getFout().get(4));
                    cell.model.fin.set(7,cell.model.getFout().get(3));
                    cell.model.fin.set(8,cell.model.getFout().get(2));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.NORTHWEST) {
                    cell.model.fin.set(2,cell.model.getFout().get(8));
                    cell.model.fin.set(3,cell.model.getFout().get(7));
                    cell.model.fin.set(4,cell.model.getFout().get(8));
                    cell.model.fin.set(5,cell.model.getFout().get(1));
                    cell.model.fin.set(6,cell.model.getFout().get(8));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.NORTHEAST) {
                    cell.model.fin.set(4,cell.model.getFout().get(2));
                    cell.model.fin.set(5,cell.model.getFout().get(1));
                    cell.model.fin.set(6,cell.model.getFout().get(2));
                    cell.model.fin.set(7,cell.model.getFout().get(3));
                    cell.model.fin.set(8,cell.model.getFout().get(2));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.SOUTHWEST) {
                    cell.model.fin.set(8,cell.model.getFout().get(6));
                    cell.model.fin.set(1,cell.model.getFout().get(5));
                    cell.model.fin.set(2,cell.model.getFout().get(6));
                    cell.model.fin.set(3,cell.model.getFout().get(7));
                    cell.model.fin.set(4,cell.model.getFout().get(6));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.SOUTHEAST) {
                    cell.model.fin.set(6,cell.model.getFout().get(4));
                    cell.model.fin.set(7,cell.model.getFout().get(3));
                    cell.model.fin.set(8,cell.model.getFout().get(4));
                    cell.model.fin.set(1,cell.model.getFout().get(5));
                    cell.model.fin.set(2,cell.model.getFout().get(4));
                }
            }
            case OPEN_DENSITY_BC -> {
                density = cell.density;
                if (cell.getBoundaryDirection() == BoundaryDirection.NORTH) {
                    v.uy = -1 + (cell.model.fin.get(0) + cell.model.fin.get(3) + cell.model.fin.get(7)
                            + 2 * cell.model.fin.get(8) + 2 * cell.model.fin.get(1) + 2 * cell.model.fin.get(2))/density;
                    v.ux = 6*(cell.model.fin.get(3) - cell.model.fin.get(7) + cell.model.fin.get(8) - cell.model.fin.get(2))/(density*(5+3*v.uy));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.SOUTH) {
                    v.uy = 1 - (cell.model.fin.get(0) + cell.model.fin.get(3) + cell.model.fin.get(7)
                            + 2 * cell.model.fin.get(4) + 2 * cell.model.fin.get(5) + 2 * cell.model.fin.get(6))/density;
                    v.ux = 6*(cell.model.fin.get(3) - cell.model.fin.get(7) + cell.model.fin.get(6) - cell.model.fin.get(4))/(density*(5-3*v.uy));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.WEST) {
                    v.ux = 1 - (cell.model.fin.get(0) + cell.model.fin.get(1) + cell.model.fin.get(5)
                            + 2 * cell.model.fin.get(6) + 2 * cell.model.fin.get(7) + 2 * cell.model.fin.get(8))/density;
                    v.uy = 6*(cell.model.fin.get(1) - cell.model.fin.get(5) + cell.model.fin.get(8) - cell.model.fin.get(6))/(density*(5-3*v.ux));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.EAST) {
                    v.ux = -1 + (cell.model.fin.get(0) + cell.model.fin.get(1) + cell.model.fin.get(5)
                            + 2 * cell.model.fin.get(2) + 2 * cell.model.fin.get(3) + 2 * cell.model.fin.get(4))/density;
                    v.uy = 6*(cell.model.fin.get(1) - cell.model.fin.get(5) + cell.model.fin.get(2) - cell.model.fin.get(4))/(density*(5+3*v.ux));
                }
            }
            case OPEN_VELOCITY_BC -> {
                v = cell.velocity;
                if (cell.getBoundaryDirection() == BoundaryDirection.NORTH) {
                    density = (cell.model.fin.get(0) + cell.model.fin.get(3) + cell.model.fin.get(7)
                            + 2 * cell.model.fin.get(8) + 2 * cell.model.fin.get(1) + 2 * cell.model.fin.get(2))/(1 + v.uy);
                    v.ux = 6*(cell.model.fin.get(3) - cell.model.fin.get(7) + cell.model.fin.get(8) - cell.model.fin.get(2))/(density*(5+3*v.uy));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.SOUTH) {
                    density = (cell.model.fin.get(0) + cell.model.fin.get(3) + cell.model.fin.get(7)
                            + 2 * cell.model.fin.get(4) + 2 * cell.model.fin.get(5) + 2 * cell.model.fin.get(6))/(1 - v.uy);
                    v.ux = 6*(cell.model.fin.get(3) - cell.model.fin.get(7) + cell.model.fin.get(6) - cell.model.fin.get(4))/(density*(5-3*v.uy));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.WEST) {
                    density = (cell.model.fin.get(0) + cell.model.fin.get(1) + cell.model.fin.get(5)
                            + 2 * cell.model.fin.get(6) + 2 * cell.model.fin.get(7) + 2 * cell.model.fin.get(8))/(1 - v.ux);
                    v.uy = 6*(cell.model.fin.get(1) - cell.model.fin.get(5) + cell.model.fin.get(8) - cell.model.fin.get(6))/(density*(5-3*v.ux));
                }
                else if (cell.getBoundaryDirection() == BoundaryDirection.EAST) {
                    density = (cell.model.fin.get(0) + cell.model.fin.get(1) + cell.model.fin.get(5)
                            + 2 * cell.model.fin.get(2) + 2 * cell.model.fin.get(3) + 2 * cell.model.fin.get(4))/(1 + v.ux);
                    v.uy = 6*(cell.model.fin.get(1) - cell.model.fin.get(5) + cell.model.fin.get(2) - cell.model.fin.get(4))/(density*(5+3*v.ux));
                }
            }
        }
        if (cell.getFluidBoundaryType() == FluidBoundaryType.OPEN_VELOCITY_BC || cell.getFluidBoundaryType() == FluidBoundaryType.OPEN_DENSITY_BC) {
            if (cell.getBoundaryDirection() == BoundaryDirection.NORTH) {
                cell.model.fin.set(4,cell.model.fin.get(8) - density*(v.ux+v.uy)/6);
                cell.model.fin.set(5,cell.model.fin.get(1) - 2*density*v.uy/3);
                cell.model.fin.set(6,cell.model.fin.get(2) - density*(v.ux-v.uy)/6);
            }
            else if (cell.getBoundaryDirection() == BoundaryDirection.SOUTH) {
                cell.model.fin.set(8,cell.model.fin.get(4) + density*(v.ux+v.uy)/6);
                cell.model.fin.set(1,cell.model.fin.get(5) + 2*density*v.uy/3);
                cell.model.fin.set(2,cell.model.fin.get(6) + density*(v.ux-v.uy)/6);
            }
            else if (cell.getBoundaryDirection() == BoundaryDirection.WEST) {
                cell.model.fin.set(2,cell.model.fin.get(6) + density*(v.ux-v.uy)/6);
                cell.model.fin.set(3,cell.model.fin.get(7) + 2*density*v.ux/3);
                cell.model.fin.set(4,cell.model.fin.get(8) + density*(v.ux+v.uy)/6);
            }
            else if (cell.getBoundaryDirection() == BoundaryDirection.EAST) {
                cell.model.fin.set(6,cell.model.fin.get(2) - density*(v.ux-v.uy)/6);
                cell.model.fin.set(7,cell.model.fin.get(3) - 2*density*v.ux/3);
                cell.model.fin.set(8,cell.model.fin.get(4) - density*(v.ux+v.uy)/6);
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