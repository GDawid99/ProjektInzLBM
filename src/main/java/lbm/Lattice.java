package lbm;

import lbm.model.D2Q9;
import lbm.model.GlobalValues;
import util.Velocity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Lattice {
    private Cell[][] cells;
    private int latticeWidth;
    private int latticeHeight;
    public int iter = 0;
    public float avg_density;
    public Velocity avg_velocity;

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
                    board[y][x] = setInitialValues(x,y,1f,1f,new Velocity(0f,0f),CellState.BOUNCE_BACK_BC);
                }
                else if (y == latticeHeight-1) {
                    board[y][x] = setInitialValues(x,y,1f,1f,new Velocity(0f,0f),CellState.BOUNCE_BACK_BC);
                }
                else if (x == latticeWidth-1) {
                    board[y][x] = setInitialValues(x,y,1f,GlobalValues.TEMPERATURE,new Velocity(0f,0f),CellState.OPEN_DENSITY_BC);
                }
                else if (x == 0) {
                    board[y][x] = setInitialValues(x,y,1f,GlobalValues.TEMPERATURE,new Velocity(GlobalValues.UX,0f),CellState.OPEN_VELOCITY_BC);
                }
//                else if (x > latticeWidth*0.45  && x < latticeWidth*0.55 && y > latticeHeight*0.7) {
//                    board[y][x] = setInitialValues(x,y,1f,0f,new Velocity(0f,0f),CellState.BOUNCE_BACK_BC);
//                }
                else {
                    board[y][x] = setInitialValues(x,y,1f,GlobalValues.TEMPERATURE, GlobalValues.velocityInitZero(),CellState.FLUID);
                }
            }
        }
        return board;
    }

    private Cell setInitialValues(int x, int y, float density, float temperature, Velocity velocity, CellState cellState) {
        Cell cell = new Cell(x,y, density, temperature, velocity, cellState, new D2Q9());
        cell.model.calcFeqFunctions(cell.velocity, cell.density);
        cell.model.calcFinFunctions(cell.model.getFeq());
        cell.model.calcTeqFunctions(cell.velocity, cell.temperature);
        cell.model.calcTinFunctions(cell.model.getTeq());
        return cell;
    }

    public void executeOperations() {
        iter++;
        GlobalValues.initGlobalValues();
        avg_density = 0f;
        avg_velocity = new Velocity(0f,0f);

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
                        GlobalValues.TAU);

                //cells[y][x].temperature = cells[y][x].model.calcTemperature();
                //cells[y][x].model.calcTeqFunctions(cells[y][x].velocity, cells[y][x].temperature);
                //cells[y][x].model.calcToutFunctions(
                //        (ArrayList<Float>) cells[y][x].model.getTin(),
                //        (ArrayList<Float>) cells[y][x].model.getTeq(),
                //        1.0f,
                //        GlobalValues.TAU_TEMPERATURE);


                avg_density += cells[y][x].density;
                avg_velocity.ux += cells[y][x].velocity.ux;
                avg_velocity.uy += cells[y][x].velocity.uy;
                if (cells[y][x].velocity.ux < GlobalValues.MIN_VELOCITY.ux) GlobalValues.MIN_VELOCITY.ux = cells[y][x].velocity.ux;
                if (cells[y][x].velocity.ux > GlobalValues.MAX_VELOCITY.ux) GlobalValues.MAX_VELOCITY.ux = cells[y][x].velocity.ux;
                if (cells[y][x].velocity.uy < GlobalValues.MIN_VELOCITY.uy) GlobalValues.MIN_VELOCITY.uy = cells[y][x].velocity.uy;
                if (cells[y][x].velocity.uy > GlobalValues.MAX_VELOCITY.uy) GlobalValues.MAX_VELOCITY.uy = cells[y][x].velocity.uy;
                if (cells[y][x].density < GlobalValues.MIN_DENSITY) GlobalValues.MIN_DENSITY = cells[y][x].density;
                if (cells[y][x].density > GlobalValues.MAX_DENSITY) GlobalValues.MAX_DENSITY = cells[y][x].density;
                if (cells[y][x].temperature < GlobalValues.MIN_TEMPERATURE) GlobalValues.MIN_TEMPERATURE = cells[y][x].temperature;
                if (cells[y][x].temperature > GlobalValues.MAX_TEMPERATURE) GlobalValues.MAX_TEMPERATURE = cells[y][x].temperature;
            }
        }

        if (iter % 50 == 0) {
            dataLog();
            System.out.println("AVERAGE DENSITY:" + (avg_density / (latticeHeight * latticeWidth)));
            System.out.println("AVERAGE VELOCITY: [" + (avg_velocity.ux / (latticeHeight * latticeWidth)) + ", " + (avg_velocity.uy / (latticeHeight * latticeWidth)) + "]");
        }
        //drugi etap: obliczenie operacji streaming i warunki brzegowe
        for (int y = 0; y < latticeHeight; y++) {
            for (int x = 0; x < latticeWidth; x++) {
                List<Cell> neighbourhood = new LinkedList<>();
                for (int i = 0; i < 9; i++) {
                    int deltaX = x - D2Q9.c.get(i).get(0);
                    int deltaY = y + D2Q9.c.get(i).get(1);
                    if (deltaY >= 0 && deltaY <= latticeHeight-1 && deltaX >= 0 && deltaX <= latticeWidth-1) neighbourhood.add(cells[deltaY][deltaX]);
                    else neighbourhood.add(null);
                }
                cells[y][x].model.calcStreaming(neighbourhood);
                if (x == 0 && y == 0) cells[y][x].model.calcBoundaryConditions(cells[y][x],"NW");
                else if (x == 0 && y == latticeHeight-1) cells[y][x].model.calcBoundaryConditions(cells[y][x],"SW");
                else if (x == latticeWidth-1 && y == 0) cells[y][x].model.calcBoundaryConditions(cells[y][x],"NE");
                else if (x == latticeWidth-1 && y == latticeHeight-1) cells[y][x].model.calcBoundaryConditions(cells[y][x],"SE");
                else if (x == 0) cells[y][x].model.calcBoundaryConditions(cells[y][x],"W");
                else if (y == 0) cells[y][x].model.calcBoundaryConditions(cells[y][x],"N");
                else if (x == latticeWidth-1) cells[y][x].model.calcBoundaryConditions(cells[y][x],"E");
                else if (y == latticeHeight-1) cells[y][x].model.calcBoundaryConditions(cells[y][x],"S");

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

    private void dataLog() {
        System.out.println("Iteration: " + iter);
        System.out.println("Velocity:");
        System.out.println("MIN: " + GlobalValues.MIN_VELOCITY);
        System.out.println("MAX: " + GlobalValues.MAX_VELOCITY);
        System.out.println("Density:");
        System.out.println("MIN: " + GlobalValues.MIN_DENSITY);
        System.out.println("MAX: " + GlobalValues.MAX_DENSITY);
        System.out.println("Temperature:");
        System.out.println("MIN: " + GlobalValues.MIN_TEMPERATURE);
        System.out.println("MAX: " + GlobalValues.MAX_TEMPERATURE);
        System.out.println("Tau: " + GlobalValues.TAU);
        System.out.println("Temp. Tau: " + GlobalValues.TAU_TEMPERATURE);
        System.out.println("---------------------");
    }


}
