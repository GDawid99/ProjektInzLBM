package lbm.model;

import lbm.Cell;
import util.Velocity;

import java.util.ArrayList;
import java.util.List;

public class TemperatureD2Q9 extends ModelD2Q9{

    public TemperatureD2Q9() {
        super();
    }

    public TemperatureD2Q9(TemperatureD2Q9 temperatureModel) {
        super(temperatureModel);
    }

    @Override
    public float calc(ArrayList<Integer> c_i, Velocity u, float w_i , float variable) {
        return w_i * variable * (1f + (3f * scalar_prod(c_i, u)));
    }

    @Override
    public void calcStreaming(List<Cell> cells) {
        neighbourhoodOutFunctionElements.clear();
        neighbourhoodOutFunctionElements.add(cells.get(0).temperatureModel.fout.get(0));
        for (int i = 1; i < 9; i++) {
            if (cells.get(i) == null || cells.get(i).getCellBoundaryType().isSolid()) {
                neighbourhoodOutFunctionElements.add(null);
            }
            else {
                neighbourhoodOutFunctionElements.add(cells.get(i).temperatureModel.fout.get(i));
            }
        }
        cells.get(0).temperatureModel.calcInputFunctions(neighbourhoodOutFunctionElements);
    }

    @Override
    public void calcBoundaryConditions(Cell cell) {
        switch (cell.getCellBoundaryType().getTempBoundaryType()) {
            case CONST_BC -> calcInputFunctions(fout);
            case CONST_TEMPERATURE_BC -> {
                switch (cell.getCellBoundaryType().getBoundaryDirection()) {
                    case NORTH -> {
                        fin.set(4, constTemperature -  fin.get(8));
                        fin.set(5, 4f * constTemperature -  fin.get(1));
                        fin.set(6, constTemperature -  fin.get(2));
                    }
                    case SOUTH -> {
                        fin.set(8, constTemperature -  fin.get(4));
                        fin.set(1, 4f * constTemperature -  fin.get(5));
                        fin.set(2, constTemperature -  fin.get(6));
                    }
                    case WEST -> {
                        fin.set(2, constTemperature -  fin.get(6));
                        fin.set(3, 4f * constTemperature -  fin.get(7));
                        fin.set(4, constTemperature -  fin.get(8));
                    }
                    case EAST -> {
                        fin.set(6, constTemperature -  fin.get(2));
                        fin.set(7, 4f * constTemperature -  fin.get(3));
                        fin.set(8, constTemperature -  fin.get(4));
                    }
                    case NORTHWEST_CONCAVE -> {
                        fin.set(2, constTemperature -  fin.get(8));
                        fin.set(3, 4f * constTemperature -  fin.get(7));
                        fin.set(4, constTemperature -  fin.get(8));
                        fin.set(5, 4f * constTemperature -  fin.get(1));
                        fin.set(6, constTemperature -  fin.get(8));
                    }
                    case NORTHEAST_CONCAVE -> {
                        fin.set(4, constTemperature -  fin.get(2));
                        fin.set(5, 4f * constTemperature -  fin.get(1));
                        fin.set(6, constTemperature -  fin.get(2));
                        fin.set(7, 4f * constTemperature -  fin.get(3));
                        fin.set(8, constTemperature -  fin.get(2));
                    }
                    case SOUTHWEST_CONCAVE -> {
                        fin.set(8, constTemperature -  fin.get(6));
                        fin.set(1, 4f * constTemperature -  fin.get(5));
                        fin.set(2, constTemperature -  fin.get(6));
                        fin.set(3, 4f * constTemperature -  fin.get(7));
                        fin.set(4, constTemperature -  fin.get(6));
                    }
                    case SOUTHEAST_CONCAVE -> {
                        fin.set(6, constTemperature -  fin.get(4));
                        fin.set(7, 4f * constTemperature -  fin.get(3));
                        fin.set(8, constTemperature -  fin.get(4));
                        fin.set(1, 4f * constTemperature -  fin.get(5));
                        fin.set(2, constTemperature -  fin.get(4));
                    }
                    case SOUTHEAST_CONVEX -> {
                        fin.set(8, constTemperature -  fin.get(4));
                    }
                    case SOUTHWEST_CONVEX -> {
                        fin.set(2, constTemperature -  fin.get(6));
                    }
                    case NORTHEAST_CONVEX -> {
                        fin.set(6, constTemperature -  fin.get(2));
                    }
                    case NORTHWEST_CONVEX -> {
                        fin.set(4, constTemperature -  fin.get(8));
                    }
                }
            }
            case ADIABATIC_BC -> {
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
                    case SOUTHEAST_CONVEX -> {
                        fin.set(8, fout.get(4));
                    }
                    case SOUTHWEST_CONVEX -> {
                        fin.set(2, fout.get(6));
                    }
                    case NORTHEAST_CONVEX -> {
                        fin.set(6, fout.get(2));
                    }
                    case NORTHWEST_CONVEX -> {
                        fin.set(4, fout.get(8));
                    }
                }
            }
            case OPEN_TEMPERATURE_BC -> {
                switch (cell.getCellBoundaryType().getBoundaryDirection()) {
                    case NORTH -> {
                        constTemperature = ( fin.get(0) +  fin.get(3) +  fin.get(7)
                                + 2 * fin.get(8) + 2 * fin.get(1) + 2 * fin.get(2))/(1 + cell.velocity.uy);
                        fin.set(4, fin.get(2) + constTemperature * cell.velocity.uy/6);
                        fin.set(5, fin.get(1) - 2 * constTemperature * cell.velocity.uy/3);
                        fin.set(6, fin.get(8) - constTemperature * cell.velocity.uy/6);
                    }
                    case SOUTH -> {
                        constTemperature = ( fin.get(0) +  fin.get(3) +  fin.get(7)
                                + 2 * fin.get(4) + 2 * fin.get(5) + 2 * fin.get(6))/(1 - cell.velocity.uy);
                        fin.set(8, fin.get(6) + constTemperature * cell.velocity.uy/6);
                        fin.set(1, fin.get(5) + 2 * constTemperature * cell.velocity.uy/3);
                        fin.set(2, fin.get(4) - constTemperature * cell.velocity.uy/6);
                    }
                    case WEST -> {
                        constTemperature = ( fin.get(0) +  fin.get(1) +  fin.get(5)
                                + 2 * fin.get(6) + 2 * fin.get(7) + 2 * fin.get(8))/(1 - cell.velocity.ux);
                        fin.set(2, fin.get(8) + constTemperature * cell.velocity.ux/6);
                        fin.set(3, fin.get(7) + 2 * constTemperature * cell.velocity.ux/3);
                        fin.set(4, fin.get(6) + constTemperature * cell.velocity.ux/6);}
                    case EAST -> {
                        constTemperature = ( fin.get(0) +  fin.get(1) +  fin.get(5)
                                + 2 * fin.get(2) + 2 * fin.get(3) + 2 * fin.get(4))/(1 + cell.velocity.ux);
                        fin.set(6, fin.get(4) - constTemperature * cell.velocity.ux/6);
                        fin.set(7, fin.get(3) - 2 * constTemperature * cell.velocity.ux/3);
                        fin.set(8, fin.get(2) - constTemperature * cell.velocity.ux/6);
                    }
                }
                cell.temperature = constTemperature;
            }
        }
    }
}
