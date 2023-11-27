package lbm.model;

import lbm.Cell;
import lbm.CellState;
import lbm.GlobalValues;
import util.Velocity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class D2Q9Temperature extends Model{

    public D2Q9Temperature() {
        this.fin = new ArrayList<>(9);
        this.feq = new ArrayList<>(9);
        this.fout = new ArrayList<>(9);
        this.neighbourhoodOutFunctionElements = new LinkedList<>();
    }

    public D2Q9Temperature(D2Q9Temperature temperatureModel) {
        this.fin = new ArrayList<>(temperatureModel.getFin());
        this.feq = new ArrayList<>(temperatureModel.getFeq());
        this.fout = new ArrayList<>(temperatureModel.getFout());
        this.neighbourhoodOutFunctionElements = new LinkedList<>(temperatureModel.neighbourhoodOutFunctionElements);
    }

    @Override
    public void calcInputFunctions(List<Float> f) {
        fin.clear();
        for (int i = 0; i < 9; i++) {
            this.fin.add(f.get(i));
        }
    }

    @Override
    public void calcEquilibriumFunctions(Velocity velocity, float temperature) {
        feq.clear();
        for (int i = 0; i < 9; i++) {
            feq.add(calc(D2Q9.c.get(i),velocity,D2Q9.w.get(i),temperature));
        }
    }

    @Override
    public void calcOutputFunctions(ArrayList<Float> fin, ArrayList<Float> feq, float time, float tau) {
        fout.clear();
        for (int i = 0; i < 9; i++) {
            fout.add(fin.get(i) + time*((feq.get(i) - fin.get(i))/tau));
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
    public void calcBoundaryConditions(Cell cell, String direction) {
        Velocity v = new Velocity(0.0f,0.0f);
        float temperature = 0f;
        switch (cell.getCellState()) {
            case CONST_BC -> {
                cell.model.calcInputFunctions(cell.model.getFout());
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
            }
            case OPEN_DENSITY_BC -> {
                temperature = 1f;
                if (direction.equals("N")) {
                    v.uy = -1 + (cell.model.fin.get(0) + cell.model.fin.get(3) + cell.model.fin.get(7)
                            + 2 * cell.model.fin.get(8) + 2 * cell.model.fin.get(1) + 2 * cell.model.fin.get(2))/temperature;
                    v.ux = 6*(cell.model.fin.get(3) - cell.model.fin.get(7) + cell.model.fin.get(8) - cell.model.fin.get(2))/(temperature*(5+3*v.uy));
                }
                else if (direction.equals("S")) {
                    v.uy = 1 - (cell.model.fin.get(0) + cell.model.fin.get(3) + cell.model.fin.get(7)
                            + 2 * cell.model.fin.get(4) + 2 * cell.model.fin.get(5) + 2 * cell.model.fin.get(6))/temperature;
                    v.ux = 6*(cell.model.fin.get(3) - cell.model.fin.get(7) + cell.model.fin.get(6) - cell.model.fin.get(4))/(temperature*(5-3*v.uy));
                }
                else if (direction.equals("W")) {
                    v.ux = 1 - (cell.model.fin.get(0) + cell.model.fin.get(1) + cell.model.fin.get(5)
                            + 2 * cell.model.fin.get(6) + 2 * cell.model.fin.get(7) + 2 * cell.model.fin.get(8))/temperature;
                    v.uy = 6*(cell.model.fin.get(1) - cell.model.fin.get(5) + cell.model.fin.get(8) - cell.model.fin.get(6))/(temperature*(5-3*v.ux));
                }
                else if (direction.equals("E")) {
                    v.ux = -1 + (cell.model.fin.get(0) + cell.model.fin.get(1) + cell.model.fin.get(5)
                            + 2 * cell.model.fin.get(2) + 2 * cell.model.fin.get(3) + 2 * cell.model.fin.get(4))/temperature;
                    v.uy = 6*(cell.model.fin.get(1) - cell.model.fin.get(5) + cell.model.fin.get(2) - cell.model.fin.get(4))/(temperature*(5+3*v.ux));
                }
            }
            case OPEN_VELOCITY_BC -> {
                v = new Velocity(cell.y* GlobalValues.UX/128,0f);
                if (direction.equals("N")) {
                    temperature = (cell.model.fin.get(0) + cell.model.fin.get(3) + cell.model.fin.get(7)
                            + 2 * cell.model.fin.get(8) + 2 * cell.model.fin.get(1) + 2 * cell.model.fin.get(2))/(1 + v.uy);
                    v.ux = 6*(cell.model.fin.get(3) - cell.model.fin.get(7) + cell.model.fin.get(8) - cell.model.fin.get(2))/(temperature*(5+3*v.uy));
                }
                else if (direction.equals("S")) {
                    temperature = (cell.model.fin.get(0) + cell.model.fin.get(3) + cell.model.fin.get(7)
                            + 2 * cell.model.fin.get(4) + 2 * cell.model.fin.get(5) + 2 * cell.model.fin.get(6))/(1 - v.uy);
                    v.ux = 6*(cell.model.fin.get(3) - cell.model.fin.get(7) + cell.model.fin.get(6) - cell.model.fin.get(4))/(temperature*(5-3*v.uy));
                }
                else if (direction.equals("W")) {
                    temperature = (cell.model.fin.get(0) + cell.model.fin.get(1) + cell.model.fin.get(5)
                            + 2 * cell.model.fin.get(6) + 2 * cell.model.fin.get(7) + 2 * cell.model.fin.get(8))/(1 - v.ux);
                    v.uy = 6*(cell.model.fin.get(1) - cell.model.fin.get(5) + cell.model.fin.get(8) - cell.model.fin.get(6))/(temperature*(5-3*v.ux));
                }
                else if (direction.equals("E")) {
                    temperature = (cell.model.fin.get(0) + cell.model.fin.get(1) + cell.model.fin.get(5)
                            + 2 * cell.model.fin.get(2) + 2 * cell.model.fin.get(3) + 2 * cell.model.fin.get(4))/(1 + v.ux);
                    v.uy = 6*(cell.model.fin.get(1) - cell.model.fin.get(5) + cell.model.fin.get(2) - cell.model.fin.get(4))/(temperature*(5+3*v.ux));
                }
            }
        }
        if (cell.getCellState() == CellState.OPEN_VELOCITY_BC || cell.getCellState() == CellState.OPEN_DENSITY_BC) {
            if (direction.equals("N")) {
                cell.model.fin.set(4,cell.model.fin.get(8) + temperature*(v.ux+v.uy)/6);
                cell.model.fin.set(5,cell.model.fin.get(1) - 2*temperature*v.uy/3);
                cell.model.fin.set(6,cell.model.fin.get(2) - temperature*(v.ux-v.uy)/6);
            }
            else if (direction.equals("S")) {
                cell.model.fin.set(8,cell.model.fin.get(4) + temperature*(v.ux+v.uy)/6);
                cell.model.fin.set(1,cell.model.fin.get(5) + 2*temperature*v.uy/3);
                cell.model.fin.set(2,cell.model.fin.get(6) - temperature*(v.ux-v.uy)/6);
            }
            else if (direction.equals("W")) {
                cell.model.fin.set(2,cell.model.fin.get(6) + temperature*(v.ux-v.uy)/6);
                cell.model.fin.set(3,cell.model.fin.get(7) + 2*temperature*v.ux/3);
                cell.model.fin.set(4,cell.model.fin.get(8) + temperature*(v.ux+v.uy)/6);
            }
            else if (direction.equals("E")) {
                cell.model.fin.set(6,cell.model.fin.get(2) - temperature*(v.ux-v.uy)/6);
                cell.model.fin.set(7,cell.model.fin.get(3) - 2*temperature*v.ux/3);
                cell.model.fin.set(8,cell.model.fin.get(4) - temperature*(v.ux+v.uy)/6);
            }
        }
    }
}
