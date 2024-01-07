package lbm.model;

import lbm.Cell;
import lbm.boundary.FluidBoundaryType;
import util.Velocity;

import java.util.*;

public class FluidFlowD2Q9 extends ModelD2Q9 {
    public FluidFlowD2Q9() {
        super();
    }

    public FluidFlowD2Q9(FluidFlowD2Q9 model) {
        super(model);
    }

    @Override
    public float calc(ArrayList<Integer> c_i, Velocity u, float w_i , float variable) {
        return w_i * variable * (1f + (3f * scalar_prod(c_i, u)) + (4.5f * scalar_prod(c_i,u) * scalar_prod(c_i,u)) - (1.5f * (u.ux*u.ux + u.uy*u.uy)));
    }

    @Override
    public void calcStreaming(List<Cell> cells) {
        neighbourhoodOutFunctionElements.clear();
        neighbourhoodOutFunctionElements.add(cells.get(0).model.fout.get(0));
        for (int i = 1; i < 9; i++) {
            if (cells.get(i) == null || cells.get(i).getCellBoundaryType().isSolid()) {
                neighbourhoodOutFunctionElements.add(null);
            }
            else {
                neighbourhoodOutFunctionElements.add(cells.get(i).model.fout.get(i));
            }
        }
        cells.get(0).model.calcInputFunctions(neighbourhoodOutFunctionElements);
    }

    @Override
    public void calcBoundaryConditions(Cell cell) {
        float density = cell.density;
        Velocity v = cell.velocity;
        switch (cell.getCellBoundaryType().getFluidBoundaryType()) {
            case CONST_BC -> calcInputFunctions(fout);
            case BOUNCE_BACK_BC -> {
                switch (cell.getCellBoundaryType().getBoundaryDirection()) {
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
                    case NORTHWEST_CONCAVE -> {
                        fin.set(2, fout.get(8));
                        fin.set(3, fout.get(7));
                        fin.set(4, fout.get(8));
                        fin.set(5, fout.get(1));
                        fin.set(6, fout.get(8));
                    }
                    case NORTHEAST_CONCAVE -> {
                        fin.set(4, fout.get(2));
                        fin.set(5, fout.get(1));
                        fin.set(6, fout.get(2));
                        fin.set(7, fout.get(3));
                        fin.set(8, fout.get(2));
                    }
                    case SOUTHWEST_CONCAVE -> {
                        fin.set(8, fout.get(6));
                        fin.set(1, fout.get(5));
                        fin.set(2, fout.get(6));
                        fin.set(3, fout.get(7));
                        fin.set(4, fout.get(6));
                    }
                    case SOUTHEAST_CONCAVE -> {
                        fin.set(6, fout.get(4));
                        fin.set(7, fout.get(3));
                        fin.set(8, fout.get(4));
                        fin.set(1, fout.get(5));
                        fin.set(2, fout.get(4));
                    }
                    case SOUTHEAST_CONVEX -> {
                        fin.set(8, fout.get(4));
                    }
                    case SOUTHWEST_CONVEX -> {
                        fin.set(2, fout.get(6));
                    }
                }
            }
            case SYMMETRY_BC -> {
                switch (cell.getCellBoundaryType().getBoundaryDirection()) {
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
                    case NORTHWEST_CONCAVE -> {
                        fin.set(2, fout.get(8));
                        fin.set(3, fout.get(7));
                        fin.set(4, fout.get(8));
                        fin.set(5, fout.get(1));
                        fin.set(6, fout.get(8));
                    }
                    case NORTHEAST_CONCAVE -> {
                        fin.set(4, fout.get(2));
                        fin.set(5, fout.get(1));
                        fin.set(6, fout.get(2));
                        fin.set(7, fout.get(3));
                        fin.set(8, fout.get(2));
                    }
                    case SOUTHWEST_CONCAVE -> {
                        fin.set(8, fout.get(6));
                        fin.set(1, fout.get(5));
                        fin.set(2, fout.get(6));
                        fin.set(3, fout.get(7));
                        fin.set(4, fout.get(6));
                    }
                    case SOUTHEAST_CONCAVE -> {
                        fin.set(6, fout.get(4));
                        fin.set(7, fout.get(3));
                        fin.set(8, fout.get(4));
                        fin.set(1, fout.get(5));
                        fin.set(2, fout.get(4));
                    }
                }
            }
            case OPEN_DENSITY_BC -> {
                switch (cell.getCellBoundaryType().getBoundaryDirection()) {
                    case NORTH -> v.uy = -1 + (fin.get(0) + fin.get(3) + fin.get(7)
                            + 2 * fin.get(8) + 2 * fin.get(1) + 2 * fin.get(2))/density;
                    case SOUTH -> v.uy = 1 - (fin.get(0) + fin.get(3) + fin.get(7)
                            + 2 * fin.get(4) + 2 * fin.get(5) + 2 * fin.get(6))/density;
                    case WEST -> v.ux = 1 - (fin.get(0) + fin.get(1) + fin.get(5)
                            + 2 * fin.get(6) + 2 * fin.get(7) + 2 * fin.get(8))/density;
                    case EAST -> v.ux = -1 + (fin.get(0) + fin.get(1) + fin.get(5)
                            + 2 * fin.get(2) + 2 * fin.get(3) + 2 * fin.get(4))/density;
                }
            }
            case OPEN_VELOCITY_BC -> {
                switch (cell.getCellBoundaryType().getBoundaryDirection()) {
                    case NORTH -> density = (fin.get(0) + fin.get(3) + fin.get(7)
                            + 2 * fin.get(8) + 2 * fin.get(1) + 2 * fin.get(2))/(1 + v.uy);
                    case SOUTH -> density = (fin.get(0) + fin.get(3) + fin.get(7)
                            + 2 * fin.get(4) + 2 * fin.get(5) + 2 * fin.get(6))/(1 - v.uy);
                    case WEST -> density = (fin.get(0) + fin.get(1) + fin.get(5)
                            + 2 * fin.get(6) + 2 * fin.get(7) + 2 * fin.get(8))/(1 - v.ux);
                    case EAST -> density = (fin.get(0) + fin.get(1) + fin.get(5)
                            + 2 * fin.get(2) + 2 * fin.get(3) + 2 * fin.get(4))/(1 + v.ux);
                }
                cell.density = density;
            }
        }
        if (cell.cellBoundaryType.getFluidBoundaryType() == FluidBoundaryType.OPEN_VELOCITY_BC || cell.cellBoundaryType.getFluidBoundaryType() == FluidBoundaryType.OPEN_DENSITY_BC) {
            switch (cell.getCellBoundaryType().getBoundaryDirection()) {
                case NORTH -> {
                    v.ux = 6*(fin.get(3) - fin.get(7) + fin.get(8) - fin.get(2))/(density*(5-3*v.uy));
                    //v.ux = 0f;
                    fin.set(4,fin.get(8) + density*(v.ux+v.uy)/6);
                    fin.set(5,fin.get(1) - 2*density*v.uy/3);
                    fin.set(6,fin.get(2) - density*(v.ux-v.uy)/6);
                }
                case SOUTH -> {
                    v.ux = 6*(fin.get(3) - fin.get(7) + fin.get(6) - fin.get(4))/(density*(5+3*v.uy));
                    //v.ux = 0f;
                    fin.set(8,fin.get(4) + density*(v.ux+v.uy)/6);
                    fin.set(1,fin.get(5) + 2*density*v.uy/3);
                    fin.set(2,fin.get(6) - density*(v.ux-v.uy)/6);
                }
                case WEST -> {
                    v.uy = 6*(fin.get(1) - fin.get(5) + fin.get(8) - fin.get(6))/(density*(5+3*v.ux));
                    //v.uy = 0f;
                    fin.set(2,fin.get(6) + density*(v.ux-v.uy)/6);
                    fin.set(3,fin.get(7) + 2*density*v.ux/3);
                    fin.set(4,fin.get(8) + density*(v.ux+v.uy)/6);
                }
                case EAST -> {
                    v.uy = 6*(fin.get(1) - fin.get(5) + fin.get(2) - fin.get(4))/(density*(5-3*v.ux));
                    //v.uy = 0f;
                    fin.set(6,fin.get(2) - density*(v.ux-v.uy)/6);
                    fin.set(7,fin.get(3) - 2*density*v.ux/3);
                    fin.set(8,fin.get(4) - density*(v.ux+v.uy)/6);
                }
            }
        }
    }
}