package lbm;

import lbm.model.D2Q9;
import lbm.model.InitValues;
import util.Velocity;

import java.util.ArrayList;

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
                    board[y][x] = setInitialValues(x,y,1f,new Velocity(0f,0f),CellState.WALL);
                }
                else if (y == latticeHeight-1) {
                    board[y][x] = setInitialValues(x,y,1f,new Velocity(0f,0f),CellState.WALL);
                }
                else if (x == latticeWidth-1) {
                    board[y][x] = setInitialValues(x,y,1f,new Velocity(0f,0f),CellState.WALL);
                }
                else if (x == 0 && y > 9 && y < 70) {
                    board[y][x] = setInitialValues(x,y,1f,new Velocity(0.0001f,0f),CellState.VELOCITY_WALL);
                }
                else if (x == 0) {
                    board[y][x] = setInitialValues(x,y,1f,new Velocity(0f,0f),CellState.WALL);
                }
//                else if((x == 20 || x == 21) && ((y > 0 && y < 45) || y > 55 && y < 100)) {
//                    board[y][x] = setInitialValues(x,y,1f,new Velocity(0f,0f),CellState.WALL);
//                }
                else if(x > latticeWidth*0.1 && x < latticeWidth*0.15 && y >= latticeHeight*0.4 && y <= latticeHeight*0.6) {
                    board[y][x] = setInitialValues(x,y,1f,new Velocity(0f,0f),CellState.WALL);
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
        cell.model.calcFinFunctions((ArrayList<Float>)cell.model.getFeq());
        //cell.model.calcFoutFunctions((ArrayList<Float>) cell.model.getFin(),(ArrayList<Float>)cell.model.getFeq(),1.0f,1.0f);
        return cell;
    }

    public void executeOperations() {
        float tau = 0.52f;
        iter++;
        MIN_DENSITY = 0f;
        MAX_DENSITY = 0f;
        MIN_VELOCITY = new Velocity(0f,0f,0f);
        MAX_VELOCITY = new Velocity(0f,0f,0f);

        //pierwszy etap: obliczenie danych makroskopowych, feq i kolizje
        for (int y = 0; y < latticeHeight; y++) {
            for (int x = 0; x < latticeWidth; x++) {
                cells[y][x].density = cells[y][x].model.calcDensity();
                cells[y][x].velocity = cells[y][x].model.calcVelocity(cells[y][x].density);
                //tau = (3f* (latticeHeight-2f) * cells[y][x].velocity.ux)/100f + 0.5f;
                cells[y][x].model.calcFeqFunctions(cells[y][x].velocity, cells[y][x].density);
                cells[y][x].model.calcFoutFunctions(
                        (ArrayList<Float>) cells[y][x].model.getFin(),
                        (ArrayList<Float>) cells[y][x].model.getFeq(),
                        1.0f,
                         tau);
                if (cells[y][x].velocity.ux < MIN_VELOCITY.ux) MIN_VELOCITY.ux = cells[y][x].velocity.ux;
                if (cells[y][x].velocity.ux > MAX_VELOCITY.ux) MAX_VELOCITY.ux = cells[y][x].velocity.ux;
                if (cells[y][x].density < MIN_DENSITY) MIN_DENSITY = cells[y][x].density;
                if (cells[y][x].density > MAX_DENSITY) MAX_DENSITY = cells[y][x].density;

            }
        }
        if (iter % 50 == 0) {
            System.out.println("Iteration: " + iter);
            System.out.println("Velocity:");
            System.out.println("MIN: " + MIN_VELOCITY);
            System.out.println("MAX: " + MAX_VELOCITY);
            System.out.println("Density:");
            System.out.println("MIN: " + MIN_DENSITY);
            System.out.println("MAX: " + MAX_DENSITY);
            System.out.println("---------------------");
        }

        //cells = copy(tmpCells);
        //Cell [][] tmpCells = copy(cells);
        //drugi etap: obliczenie operacji streaming i warunki brzegowe (nie uwzględniamy ścian w pętli)
        for (int y = 0; y < latticeHeight; y++) {
            for (int x = 0; x < latticeWidth; x++) {
                if (cells[y][x].getCellState() == CellState.FLUID) {
                    ArrayList<Float> neighbourhoodFoutElements = new ArrayList<>(9);
                    for (int i = 0; i < 9; i++) {
                        int deltaX = x - cells[y][x].model.getC().get(i).get(0);
                        int deltaY = y + cells[y][x].model.getC().get(i).get(1);
                        if (cells[deltaY][deltaX].getCellState() != CellState.WALL) neighbourhoodFoutElements.add(cells[deltaY][deltaX].model.getFout().get(i));
                        else {
                            if (i < 5) neighbourhoodFoutElements.add(cells[y][x].model.getFout().get(i+4));
                            else neighbourhoodFoutElements.add(cells[y][x].model.getFout().get(i-4));
                        }
                    }
                    cells[y][x].model.calcFinFunctions(neighbourhoodFoutElements);
                }
                else {
                    cells[y][x].model.calcFinFunctions((ArrayList<Float>) cells[y][x].model.getFout());
                }
            }
        }
        //cells = copy(tmpCells);
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


}
