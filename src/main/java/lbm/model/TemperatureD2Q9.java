package lbm.model;

import lbm.Cell;
import lbm.boundary.FluidBoundaryType;
import lbm.boundary.TempBoundaryType;
import util.Velocity;

import java.util.ArrayList;
import java.util.LinkedList;
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
            if (cells.get(i) == null || cells.get(i).getFluidBoundaryType() == FluidBoundaryType.WALL) {
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
        float constTemperature = 0f;
        float alphaT = 1;
        switch (cell.getTempBoundaryType()) {
            case CONST_BC -> calcInputFunctions(fout);
            case BOUNCE_BACK_BC -> {
                switch (cell.getBoundaryDirection()) {
                    case NORTH -> {
                        alphaT = 0f;
                        fin.set(4, (1 - alphaT) * fin.get(8) + alphaT * (cell.temperature -  fin.get(8)));
                        fin.set(5, (1 - alphaT) * fin.get(1) + alphaT * (4f * cell.temperature -  fin.get(1)));
                        fin.set(6, (1 - alphaT) * fin.get(2) + alphaT * (cell.temperature -  fin.get(2)));
                    }
                    case SOUTH -> {
                        alphaT = 0f;
                        fin.set(8, (1 - alphaT) * fin.get(4) + alphaT * (cell.temperature -  fin.get(4)));
                        fin.set(1, (1 - alphaT) * fin.get(5) + alphaT * (4f * cell.temperature -  fin.get(5)));
                        fin.set(2, (1 - alphaT) * fin.get(6) + alphaT * (cell.temperature -  fin.get(6)));
                    }
                    case WEST -> {
                        alphaT = 0f;
                        fin.set(2, (1 - alphaT) * fin.get(6) + alphaT * (cell.temperature -  fin.get(6)));
                        fin.set(3, (1 - alphaT) * fin.get(7) + alphaT * (4f * cell.temperature -  fin.get(7)));
                        fin.set(4, (1 - alphaT) * fin.get(8) + alphaT * (cell.temperature -  fin.get(8)));
                    }
                    case EAST -> {
                        alphaT = 0f;
                        fin.set(6, (1 - alphaT) * fin.get(2) + alphaT * (cell.temperature -  fin.get(2)));
                        fin.set(7, (1 - alphaT) * fin.get(3) + alphaT * (4f * cell.temperature -  fin.get(3)));
                        fin.set(8, (1 - alphaT) * fin.get(4) + alphaT * (cell.temperature -  fin.get(4)));
                    }
                    case NORTHWEST -> {
                        alphaT = 0f;
                        fin.set(2, (1 - alphaT) * fin.get(8) + alphaT * (cell.temperature -  fin.get(8)));
                        fin.set(3, (1 - alphaT) * fin.get(7) + alphaT * (4f * cell.temperature -  fin.get(7)));
                        fin.set(4, (1 - alphaT) * fin.get(8) + alphaT * (cell.temperature -  fin.get(8)));
                        fin.set(5, (1 - alphaT) * fin.get(1) + alphaT * (4f * cell.temperature -  fin.get(1)));
                        fin.set(6, (1 - alphaT) * fin.get(8) + alphaT * (cell.temperature -  fin.get(8)));
                    }
                    case NORTHEAST -> {
                        alphaT = 0f;
                        fin.set(4, (1 - alphaT) * fin.get(2) + alphaT * (cell.temperature -  fin.get(2)));
                        fin.set(5, (1 - alphaT) * fin.get(1) + alphaT * (4f * cell.temperature -  fin.get(1)));
                        fin.set(6, (1 - alphaT) * fin.get(2) + alphaT * (cell.temperature -  fin.get(2)));
                        fin.set(7, (1 - alphaT) * fin.get(3) + alphaT * (4f * cell.temperature -  fin.get(3)));
                        fin.set(8, (1 - alphaT) * fin.get(2) + alphaT * (cell.temperature -  fin.get(2)));
                    }
                    case SOUTHWEST -> {
                        alphaT = 0f;
                        fin.set(8, (1 - alphaT) * fin.get(6) + alphaT * (cell.temperature -  fin.get(6)));
                        fin.set(1, (1 - alphaT) * fin.get(5) + alphaT * (4f * cell.temperature -  fin.get(5)));
                        fin.set(2, (1 - alphaT) * fin.get(6) + alphaT * (cell.temperature -  fin.get(6)));
                        fin.set(3, (1 - alphaT) * fin.get(7) + alphaT * (4f * cell.temperature -  fin.get(7)));
                        fin.set(4, (1 - alphaT) * fin.get(6) + alphaT * (cell.temperature -  fin.get(6)));
                    }
                    case SOUTHEAST -> {
                        alphaT = 0f;
                        fin.set(6, (1 - alphaT) * fin.get(4) + alphaT * (cell.temperature -  fin.get(4)));
                        fin.set(7, (1 - alphaT) * fin.get(3) + alphaT * (4f * cell.temperature -  fin.get(3)));
                        fin.set(8, (1 - alphaT) * fin.get(4) + alphaT * (cell.temperature -  fin.get(4)));
                        fin.set(1, (1 - alphaT) * fin.get(5) + alphaT * (4f * cell.temperature -  fin.get(5)));
                        fin.set(2, (1 - alphaT) * fin.get(4) + alphaT * (cell.temperature -  fin.get(4)));
                    }
                    case NORTHWEST_TYPE2 -> {
                        alphaT = 0f;
                        fin.set(8, (1 - alphaT) * fin.get(4) + alphaT * (cell.temperature -  fin.get(4)));
                    }
                    case NORTHEAST_TYPE2 -> {
                        alphaT = 0f;
                        fin.set(2, (1 - alphaT) * fin.get(6) + alphaT * (cell.temperature -  fin.get(6)));
                    }
                }
            }
            case SYMMETRY_BC -> {
                switch (cell.getBoundaryDirection()) {
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
            case OPEN_TEMPERATURE_BC -> {
                switch (cell.getBoundaryDirection()) {
                    case NORTH -> {
                        cell.temperature = ( fin.get(0) +  fin.get(3) +  fin.get(7)
                                + 2 * fin.get(8) + 2 * fin.get(1) + 2 * fin.get(2))/(1 + cell.velocity.uy);
                        fin.set(4, fin.get(8) - cell.temperature * cell.velocity.uy/6);
                        fin.set(5, fin.get(1) - 2 * cell.temperature * cell.velocity.uy/3);
                        fin.set(6, fin.get(2) - cell.temperature * cell.velocity.uy/6);
                    }
                    case SOUTH -> {
                        cell.temperature = ( fin.get(0) +  fin.get(3) +  fin.get(7)
                                + 2 * fin.get(4) + 2 * fin.get(5) + 2 * fin.get(6))/(1 - cell.velocity.uy);
                        fin.set(8, fin.get(4) + cell.temperature * cell.velocity.uy/6);
                        fin.set(1, fin.get(5) + 2 * cell.temperature * cell.velocity.uy/3);
                        fin.set(2, fin.get(6) + cell.temperature * cell.velocity.uy/6);
                    }
                    case WEST -> {
                        cell.temperature = ( fin.get(0) +  fin.get(1) +  fin.get(5)
                                + 2 * fin.get(6) + 2 * fin.get(7) + 2 * fin.get(8))/(1 - cell.velocity.ux);
                        fin.set(2, fin.get(6) + cell.temperature * cell.velocity.ux/6);
                        fin.set(3, fin.get(7) + 2 * cell.temperature * cell.velocity.ux/3);
                        fin.set(4, fin.get(8) + cell.temperature * cell.velocity.ux/6);}
                    case EAST -> {
                        cell.temperature = ( fin.get(0) +  fin.get(1) +  fin.get(5)
                                + 2 * fin.get(2) + 2 * fin.get(3) + 2 * fin.get(4))/(1 + cell.velocity.ux);
                        fin.set(6, fin.get(2) - cell.temperature * cell.velocity.ux/6);
                        fin.set(7, fin.get(3) - 2 * cell.temperature * cell.velocity.ux/3);
                        fin.set(8, fin.get(4) - cell.temperature * cell.velocity.ux/6);
                    }
                }
            }
        }
    }
}
