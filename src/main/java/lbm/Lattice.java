package lbm;

import lbm.model.D2Q9;
import lbm.model.InitValues;
import util.Velocity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Lattice {
    private Cell[][] cells;
    private int latticeWidth;
    private int latticeHeight;
    public static Velocity MIN_VELOCITY = new Velocity(0f,0f,0f);
    public static Velocity MAX_VELOCITY = new Velocity(0f,0f,0f);
    public static float MIN_DENSITY = 0f;
    public static float MAX_DENSITY = 0f;
    public int iter = 0;

    public Lattice(int width, int height) {
        this.latticeWidth = width;
        this.latticeHeight = height;
        this.cells = initializeLatticeCells();
    }

    private Cell[][] initializeLatticeCells() {
        Cell[][] board = new Cell[latticeHeight][latticeWidth];
        for (int y = 0; y < latticeHeight; y++) {
            for (int x = 0; x < latticeWidth; x++) {
                if (y == 0) {
                    board[y][x] = setInitialValues(x,y,1f,new Velocity(0f,0f),CellState.SYMMETRY_BC);
                }
                else if (y == latticeHeight-1) {
                    board[y][x] = setInitialValues(x,y,1f,new Velocity(0f,0f),CellState.BOUNCE_BACK_BC);
                }
                else if (x == latticeWidth-1) {
                    board[y][x] = setInitialValues(x,y,1f,new Velocity(InitValues.UX - (latticeHeight-1-y)*InitValues.UX/100,0f),CellState.CONST_BC);
                }
                else if (x == 0) {
                    board[y][x] = setInitialValues(x,y,1f,new Velocity(0f,0f),CellState.CONST_BC);
                }
                else {
                    board[y][x] = setInitialValues(x,y,1f,InitValues.velocityInitZero(),CellState.FLUID);
                }
            }
        }
        return board;
    }

    private Cell setInitialValues(int x, int y, float density, Velocity velocity, CellState cellState) {
        Cell cell = new Cell(x,y, density, velocity, cellState, new D2Q9());
        cell.model.calcFeqFunctions(cell.velocity, cell.density);
        cell.model.calcFinFunctions(cell.model.getFeq());
        return cell;
    }

    public void executeOperations() {
        float tau = 1f;
        iter++;
        MIN_DENSITY = 1f;
        MAX_DENSITY = 1f;
        MIN_VELOCITY = new Velocity(0f,0f,0f);
        MAX_VELOCITY = new Velocity(0f,0f,0f);

        //pierwszy etap: obliczenie danych makroskopowych, feq i kolizje
        for (int y = 0; y < latticeHeight; y++) {
            for (int x = 0; x < latticeWidth; x++) {
                cells[y][x].density = cells[y][x].model.calcDensity();
                cells[y][x].velocity = cells[y][x].model.calcVelocity(cells[y][x].density);
                cells[y][x].model.calcFeqFunctions(cells[y][x].velocity, cells[y][x].density);
                cells[y][x].model.calcFoutFunctions(
                        (ArrayList<Float>) cells[y][x].model.getFin(),
                        (ArrayList<Float>) cells[y][x].model.getFeq(),
                        1.0f,
                         tau);
                if (cells[y][x].velocity.ux < MIN_VELOCITY.ux) MIN_VELOCITY.ux = cells[y][x].velocity.ux;
                if (cells[y][x].velocity.ux > MAX_VELOCITY.ux) MAX_VELOCITY.ux = cells[y][x].velocity.ux;
                if (cells[y][x].velocity.uy < MIN_VELOCITY.uy) MIN_VELOCITY.uy = cells[y][x].velocity.uy;
                if (cells[y][x].velocity.uy > MAX_VELOCITY.uy) MAX_VELOCITY.uy = cells[y][x].velocity.uy;
                if (cells[y][x].density < MIN_DENSITY) MIN_DENSITY = cells[y][x].density;
                if (cells[y][x].density > MAX_DENSITY) MAX_DENSITY = cells[y][x].density;

            }
        }

        if (iter % 50 == 0) dataLog(tau);

        //drugi etap: obliczenie operacji streaming i warunki brzegowe (nie uwzględniamy ścian w pętli)
        for (int y = 0; y < latticeHeight; y++) {
            for (int x = 0; x < latticeWidth; x++) {
                List<Cell> neighbourhood = new LinkedList<>();
                for (int i = 0; i < 9; i++) {
                    int deltaX = x - D2Q9.c.get(i).get(0);
                    int deltaY = y + D2Q9.c.get(i).get(1);
                    if (deltaY >= 0 && deltaY <= latticeHeight-1 && deltaX >= 0 && deltaX <= latticeWidth-1) neighbourhood.add(cells[deltaY][deltaX]);
                    else neighbourhood.add(null);
                }
                cells[y][x].model.calcStreamingAndBoundaryConditions(neighbourhood);
            }
        }
    }

    public Cell[][] getCells() {
        return cells;
    }


    public Cell[][] copy(Cell[][] arr) {
        Cell[][] copy = new Cell[arr.length][arr[0].length];
        for (int y = 0; y < arr.length; y++) {
            for (int x = 0; x < arr[y].length; x++) {
                copy[y][x] = new Cell(arr[y][x]);
            }
        }
        return copy;
    }

    private void dataLog(float tau) {
        System.out.println("Iteration: " + iter);
        System.out.println("Velocity:");
        System.out.println("MIN: " + MIN_VELOCITY);
        System.out.println("MAX: " + MAX_VELOCITY);
        System.out.println("Density:");
        System.out.println("MIN: " + MIN_DENSITY);
        System.out.println("MAX: " + MAX_DENSITY);
        System.out.println("Tau: " + tau);
        System.out.println("---------------------");
    }


}
