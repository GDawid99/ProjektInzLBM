package lbm;

import lbm.boundary.BoundaryDirection;
import lbm.boundary.FluidBoundaryType;
import lbm.boundary.TempBoundaryType;
import lbm.model.FluidFlowD2Q9;
import lbm.model.ModelD2Q9;
import lbm.model.TemperatureD2Q9;
import util.LatticeInitializer;
import util.Velocity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Lattice {
    public Cell[][] cells;
    private int latticeWidth;
    private int latticeHeight;
    private final float tau;
    private final float tempTau;
    private float timeStep;
    public int positionXLeftWall = 59;
    public int positionXRightWall = 81;
    public int positionYWall = 79;

    public Lattice(int width, int height, float tau, float tempTau, float timeStep) {
        this.latticeWidth = width;
        this.latticeHeight = height;
        this.tau = tau;
        this.tempTau = tempTau;
        this.timeStep = timeStep;
    }

    public Lattice(LatticeInitializer latticeInitializer) {
        if (latticeInitializer.cells == null) throw new IllegalArgumentException("Nie utworzono siatki.");
        if (latticeInitializer.tau <= 0.5f) throw new IllegalArgumentException("Nie określono poprawnie czasu relaksacji.");
        if (latticeInitializer.tempTau <= 0.5f) throw new IllegalArgumentException("Nie określono poprawnie czasu relaksacji dla temperatury.");
        if (latticeInitializer.timeStep <= 0f) throw new IllegalArgumentException("Nie określono poprawnie kroku czasowego.");
        this.cells = latticeInitializer.cells;
        this.latticeWidth = latticeInitializer.latticeWidth;
        this.latticeHeight = latticeInitializer.latticeHeight;
        this.tau = latticeInitializer.tau;
        this.tempTau = latticeInitializer.tempTau;
        this.timeStep = latticeInitializer.timeStep;
    }


    public void printValues() {
        for (int y = 0; y < latticeHeight; y++) {
            for (int x = 0; x < latticeWidth; x++) {
                //if (cells[y][x].getCellBoundaryType().isBoundary()) System.out.print("[" + "#" + "]");
                //else if (cells[y][x].getCellBoundaryType().isSolid()) System.out.print("[0]");
                //else System.out.print("[" + " " + "]");
                System.out.print("[" + cells[y][x].getCellBoundaryType().getBoundaryDirection() + "]");
            }
            System.out.println();
        }
        System.out.println(cells[79][60].getCellBoundaryType());
    }


//    private Cell[][] initializeLatticeCells() {
//        Cell[][] board = new Cell[latticeHeight][latticeWidth];
//        for (int y = 0; y < latticeHeight; y++) {
//            for (int x = 0; x < latticeWidth; x++) {
//                Velocity velocity = new Velocity(0f,0f);
//                float density = 1f;
//                float temperature = 0f;
//                if (y == 0) {
//                    BoundaryDirection direction = BoundaryDirection.NORTH;
//                    FluidBoundaryType fluidBoundaryType = FluidBoundaryType.SYMMETRY_BC;
//                    if (x == 0)  {
//                        direction = BoundaryDirection.NORTHWEST_CONCAVE;
//                        fluidBoundaryType = FluidBoundaryType.BOUNCE_BACK_BC;
//                    }
//                    if (x == latticeWidth-1) {
//                        direction = BoundaryDirection.NORTHEAST_CONCAVE;
//                        fluidBoundaryType = FluidBoundaryType.BOUNCE_BACK_BC;
//                    }
//                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, fluidBoundaryType, TempBoundaryType.BOUNCE_BACK_BC, direction);
//                }
//                else if (x > positionXLeftWall && x < positionXRightWall && y > positionYWall) {
//                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.SOLID, TempBoundaryType.SOLID, BoundaryDirection.NONE);
//                }
//                else if (y == latticeHeight-1) {
//                    BoundaryDirection direction = BoundaryDirection.SOUTH;
//                    TempBoundaryType tempType = TempBoundaryType.BOUNCE_BACK_BC;
//                    if (x == 0) direction = BoundaryDirection.SOUTHWEST_CONCAVE;
//                    if (x == latticeWidth-1) direction = BoundaryDirection.SOUTHEAST_CONCAVE;
//                    if (x < positionXLeftWall) {
//                        temperature = 1f;
//                    }
//                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.BOUNCE_BACK_BC, tempType, direction);
//                }
//                else if (x == latticeWidth-1) {
//                    board[y][x] = setInitialValues(x,y,density,temperature/*(y) * 1f/128*/,velocity/*velocity*//*new Velocity((latticeHeight-1-y)*(-0.05f)/latticeHeight,0f)*/, FluidBoundaryType.OPEN_VELOCITY_BC, TempBoundaryType.OPEN_TEMPERATURE_BC, BoundaryDirection.EAST);
//                }
//                else if (x == 0) {
//                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.OPEN_DENSITY_BC, TempBoundaryType.OPEN_TEMPERATURE_BC, BoundaryDirection.WEST);
//                }
//                else if (x == positionXLeftWall && y > positionYWall) {
//                    board[y][x] = setInitialValues(x,y,density,1f,velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.EAST);
//                }
//                else if (x == positionXRightWall && y > positionYWall) {
//                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.WEST);
//                }
//                else if (x > positionXLeftWall && x < positionXRightWall && y == positionYWall) {
//                    board[y][x] = setInitialValues(x,y,density,1f,velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.SOUTH);
//                }
//                else {
//                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.FLUID, TempBoundaryType.FLUID, BoundaryDirection.NONE);
//                }
//
//                if (x == positionXLeftWall && y == latticeHeight-1) {
//                    board[y][x] = setInitialValues(x,y,density,1f,velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.SOUTHEAST_CONCAVE);
//                }
//                if (x == positionXLeftWall && y == positionYWall) {
//                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.NORTHWEST_CONVEX);
//                }
//                if (x == positionXRightWall && y == latticeHeight-1) {
//                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.SOUTHWEST_CONCAVE);
//                }
//                if (x == positionXRightWall && y == positionYWall) {
//                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.NORTHEAST_CONVEX);
//                }
//            }
//        }
//        return board;
//    }

//    private Cell setInitialValues(int x, int y, float density, float temperature, Velocity velocity, FluidBoundaryType fluidBoundaryType, TempBoundaryType tempBoundaryType, BoundaryDirection direction) {
//        Cell cell = new Cell(x,y, density, temperature, velocity, fluidBoundaryType, tempBoundaryType, direction, new FluidFlowD2Q9(), new TemperatureD2Q9());
//        cell.model.calcEquilibriumFunctions(cell.velocity, cell.density);
//        cell.model.calcInputFunctions(cell.model.feq);
//        cell.temperatureModel.calcEquilibriumFunctions(cell.velocity, cell.temperature);
//        cell.temperatureModel.calcInputFunctions(cell.temperatureModel.feq);
//        return cell;
//    }

    public void executeOperations(float gravity) {

        //pierwszy etap: obliczenie danych makroskopowych, feq i kolizje
        for (int y = 0; y < latticeHeight; y++) {
            for (int x = 0; x < latticeWidth; x++) {
                Cell cell = cells[y][x];
                if (cell.getCellBoundaryType().isSolid()) continue;
                cell.calcMacroscopicDensity();
                //źródło ciepła
                if ((x == positionXLeftWall && y > positionYWall) ||
                        (x > positionXLeftWall && x < positionXRightWall && y == positionYWall) ||
                        (y == latticeHeight-1 && x < positionXLeftWall)) cell.calcMacroscopicTemperature(cell.temperature);
                else cell.calcMacroscopicTemperature();
                cell.calcMacroscopicVelocity(gravity);
                cell.equilibriumFunction();
                cell.model.calcOutputFunctions(
                        (ArrayList<Float>) cell.model.fin,
                        (ArrayList<Float>) cell.model.feq,
                        timeStep,
                        tau);

                cell.temperatureModel.calcEquilibriumFunctions(cell.velocity, cell.temperature);
                cell.temperatureModel.calcOutputFunctions(
                        (ArrayList<Float>) cell.temperatureModel.fin,
                        (ArrayList<Float>) cell.temperatureModel.feq,
                        timeStep,
                        tempTau);
            }
        }
        //drugi etap: obliczenie operacji streaming i warunki brzegowe
        for (int y = 0; y < latticeHeight; y++) {
            for (int x = 0; x < latticeWidth; x++) {
                Cell cell = cells[y][x];
                if (cell.getCellBoundaryType().isSolid()) continue;
                List<Cell> neighbourhood = new LinkedList<>();
                for (int i = 0; i < 9; i++) {
                    int deltaX = x - ModelD2Q9.c.get(i).get(0);
                    int deltaY = y + ModelD2Q9.c.get(i).get(1);
                    if (deltaY >= 0 && deltaY <= latticeHeight-1 && deltaX >= 0 && deltaX <= latticeWidth-1) neighbourhood.add(cells[deltaY][deltaX]);
                    else neighbourhood.add(null);
                }
                cell.model.calcStreaming(neighbourhood);
                cell.temperatureModel.calcStreaming(neighbourhood);
                cell.model.calcBoundaryConditions(cell);
                cell.temperatureModel.calcBoundaryConditions(cell);
            }
        }
    }

    public void initializeLattice() {
    //    this.cells = initializeLatticeCells();
    }

    public void initializeLattice(boolean onlyatthemoment) {
//        LatticeInitializer initializer = new LatticeInitializer();
//        LatticeInitializer.initSize();
//        LatticeInitializer.initDefaultvalues();
//        LatticeInitializer.initSolidCells();
//        LatticeInitializer.initBoundaryCells();
//
//        cells = initializer.generateDefaultFluidCells(this,1f,0f,new Velocity(0f,0f));

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
