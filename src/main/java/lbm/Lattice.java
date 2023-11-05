package lbm;

import lbm.model.D2Q9;
import lbm.model.InitValues;
import util.Velocity;

import java.util.ArrayList;

public class Lattice {
    private Cell[][] cells;
    private int latticeWidth;
    private int latticeHeight;

    public Lattice(int width, int height) {
        this.latticeWidth = width;
        this.latticeHeight = height;
        this.cells = initializeLatticeCells();
    }

    private Cell[][] initializeLatticeCells() {
        Cell[][] board = new Cell[latticeHeight][latticeWidth];
        for (int y = 0; y < latticeHeight; y++) {
            for (int x = 0; x < latticeWidth; x++) {
                if (y == 0 && x > 50) {
                    board[y][x] = setInitialValues(x,y,new Velocity(0.2f,0f),CellState.VELOCITY_WALL);
                }
                else if (y == 0 && x <= 50) {
                    board[y][x] = setInitialValues(x,y,new Velocity(0.2f,0f),CellState.VELOCITY_WALL);
                }
                else if (y == latticeHeight-1) {
                    board[y][x] = setInitialValues(x,y,new Velocity(0f,0f),CellState.VELOCITY_WALL);
                }
                else if (x == latticeWidth-1) {
                    board[y][x] = setInitialValues(x,y,new Velocity((99-y)*0.2f/100,0f),CellState.VELOCITY_WALL);
                }
                else if (x == 0) {
                    board[y][x] = setInitialValues(x,y,new Velocity((99-y)*0.2f/100,0f),CellState.VELOCITY_WALL);
                }
//                else if(x > 20 && x < 30 && y > 40 && y < 60) {
//                    board[y][x] = setInitialValues(x,y,new Velocity(0f,0f),CellState.WALL);
//                }
                else {
                    board[y][x] = setInitialValues(x,y,InitValues.velocityInitZero(),CellState.FLUID);
                }
            }
        }
        return board;
    }

    private Cell setInitialValues(int x, int y, Velocity velocity, CellState cellState) {
        Cell cell = new Cell(x,y, 3f, velocity, cellState, new D2Q9());
        cell.model.calcFeqFunctions(cell.velocity, cell.density);
        cell.model.calcFinFunctions((ArrayList<Float>)cell.model.getFeq());
        return cell;
    }

    public void executeOperations() {
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
                         1.0f);
            }
        }
        //cells = copy(tmpCells);
        Cell [][] tmpCells = copy(cells);
        //drugi etap: obliczenie operacji streaming i warunki brzegowe (nie uwzględniamy ścian w pętli)
        for (int y = 0; y < latticeHeight; y++) {
            for (int x = 0; x < latticeWidth; x++) {
                    ArrayList<Cell> neighbourhood = new ArrayList<>(9);
                    neighbourhood.add(cells[y][x]);
                    for (int i = 1; i < 9; i++) {
                        int deltaX = x + tmpCells[y][x].model.getC().get(i).get(0);
                        int deltaY = y - tmpCells[y][x].model.getC().get(i).get(1);
                        if (deltaX < 0 || deltaY < 0 || deltaX > latticeWidth - 1 || deltaY > latticeHeight - 1)
                            neighbourhood.add(null);
                        else neighbourhood.add(tmpCells[deltaY][deltaX]);
                    }
                    tmpCells[y][x].model.calcStreaming(neighbourhood);
                if (x == 0 || y == 0 || x == latticeWidth-1 || y == latticeHeight-1) {
                    tmpCells[y][x].model.calcFinFunctions((ArrayList<Float>) tmpCells[y][x].model.getFout());
                }
            }
        }
        cells = copy(tmpCells);
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
