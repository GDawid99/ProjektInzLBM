package lbm;

import lbm.boundary.BoundaryDirection;
import lbm.boundary.FluidBoundaryType;
import lbm.boundary.TempBoundaryType;
import lbm.model.FluidFlowD2Q9;
import lbm.model.TemperatureD2Q9;
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
    public final float gravity = 0.000025f;
    public ParticleTrajectory particleTrajectory;
    public int positionXLeftWall = 55;
    public int positionXRightWall = 65;
    public int positionYWall = 80;

    public Lattice(int width, int height) {
        this.latticeWidth = width;
        this.latticeHeight = height;
        particleTrajectory = new ParticleTrajectory(new Particle(0f,height/5f,0.5f, new Velocity(0.0f,0.0f)));
        this.cells = initializeLatticeCells();
    }

    private Cell[][] initializeLatticeCells() {
        Cell[][] board = new Cell[latticeHeight][latticeWidth];
        for (int y = 0; y < latticeHeight; y++) {
            for (int x = 0; x < latticeWidth; x++) {
                if (y == 0) {
                    BoundaryDirection direction = BoundaryDirection.NORTH;
                    if (x == 0) direction = BoundaryDirection.NORTHWEST;
                    if (x == latticeWidth-1) direction = BoundaryDirection.NORTHEAST;
                    board[y][x] = setInitialValues(x,y,1f,GlobalValues.TEMPERATURE,new Velocity(0f,0f), FluidBoundaryType.SYMMETRY_BC, TempBoundaryType.SYMMETRY_BC, direction);
                }
                else if (x > positionXLeftWall && x < positionXRightWall && y > positionYWall) {
                    board[y][x] = null;
                }
                else if (y == latticeHeight-1) {
                    BoundaryDirection direction = BoundaryDirection.SOUTH;
                    if (x == 0) direction = BoundaryDirection.SOUTHWEST;
                    if (x == latticeWidth-1) direction = BoundaryDirection.SOUTHEAST;
                    board[y][x] = setInitialValues(x,y,1f,GlobalValues.TEMPERATURE,new Velocity(0f,0f), FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, direction);
                }
                else if (x == 0) {
                    board[y][x] = setInitialValues(x,y,1f,y * (GlobalValues.TEMPERATURE+0.5f)/128,new Velocity((latticeHeight-1-y)*GlobalValues.UX/latticeHeight,0f), FluidBoundaryType.OPEN_VELOCITY_BC, TempBoundaryType.OPEN_TEMPERATURE_BC, BoundaryDirection.WEST);
                }
                else if (x == latticeWidth-1) {
                    board[y][x] = setInitialValues(x,y,1f,GlobalValues.TEMPERATURE,new Velocity(0f,0f), FluidBoundaryType.OPEN_DENSITY_BC, TempBoundaryType.OPEN_TEMPERATURE_BC, BoundaryDirection.EAST);
                }
                else if (x == positionXLeftWall && y > positionYWall) {
                    board[y][x] = setInitialValues(x,y,1f,GlobalValues.TEMPERATURE,new Velocity(0f,0f), FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.EAST);
                }
                else if (x == positionXRightWall && y > positionYWall) {
                    board[y][x] = setInitialValues(x,y,1f,GlobalValues.TEMPERATURE,new Velocity(0f,0f), FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.WEST);
                }
                else if (x > positionXLeftWall && x < positionXRightWall && y == positionYWall) {
                    board[y][x] = setInitialValues(x,y,1f,GlobalValues.TEMPERATURE,new Velocity(0f,0f), FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.SOUTH);
                }
                else {
                    board[y][x] = setInitialValues(x,y,1f,GlobalValues.TEMPERATURE, GlobalValues.velocityInitZero(), FluidBoundaryType.NONE, TempBoundaryType.NONE, BoundaryDirection.NONE);
                }

                if (x == positionXLeftWall && y == latticeHeight-1) {
                    board[y][x] = setInitialValues(x,y,1f,GlobalValues.TEMPERATURE,new Velocity(0f,0f), FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.SOUTHEAST);
                }
                if (x == positionXLeftWall && y == positionYWall) {
                    board[y][x] = setInitialValues(x,y,1f,GlobalValues.TEMPERATURE,new Velocity(0f,0f), FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.NORTHWEST_TYPE2);
                }
                if (x == positionXRightWall && y == latticeHeight-1) {
                    board[y][x] = setInitialValues(x,y,1f,GlobalValues.TEMPERATURE,new Velocity(0f,0f), FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.SOUTHWEST);
                }
                if (x == positionXRightWall && y == positionYWall) {
                    board[y][x] = setInitialValues(x,y,1f,GlobalValues.TEMPERATURE,new Velocity(0f,0f), FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.NORTHEAST_TYPE2);
                }



                if (board[y][x] == null) System.out.print("[    ]");
                else System.out.print("[" + board[y][x].getBoundaryDirection() + "]");
            }
            System.out.println();
        }
        return board;
    }

    private Cell setInitialValues(int x, int y, float density, float temperature, Velocity velocity, FluidBoundaryType fluidBoundaryType, TempBoundaryType tempBoundaryType, BoundaryDirection direction) {
        Cell cell = new Cell(x,y, density, temperature, velocity, fluidBoundaryType, tempBoundaryType, direction, new FluidFlowD2Q9(), new TemperatureD2Q9());
        cell.model.calcEquilibriumFunctions(cell.velocity, cell.density);
        cell.model.calcInputFunctions(cell.model.getFeq());
        cell.temperatureModel.calcEquilibriumFunctions(cell.velocity, cell.temperature);
        cell.temperatureModel.calcInputFunctions(cell.temperatureModel.getTeq());
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
                if (cells[y][x] == null) continue;
                cells[y][x].calcMacroscopicValues(gravity);
                if (x != 0) cells[y][x].calcMacroscopicTemperature();
                cells[y][x].model.calcEquilibriumFunctions(cells[y][x].velocity, cells[y][x].density);
                cells[y][x].model.calcOutputFunctions(
                        (ArrayList<Float>) cells[y][x].model.getFin(),
                        (ArrayList<Float>) cells[y][x].model.getFeq(),
                        1.0f,
                        GlobalValues.TAU);

                cells[y][x].temperatureModel.calcEquilibriumFunctions(cells[y][x].velocity, cells[y][x].temperature);
                cells[y][x].temperatureModel.calcOutputFunctions(
                        (ArrayList<Float>) cells[y][x].temperatureModel.getTin(),
                        (ArrayList<Float>) cells[y][x].temperatureModel.getTeq(),
                        1.0f,
                        GlobalValues.TAU_TEMPERATURE);


                avg_density += cells[y][x].density;
                avg_velocity.ux += cells[y][x].velocity.ux;
                avg_velocity.uy += cells[y][x].velocity.uy;
                //if (y == 30) System.out.print(cells[y][x].temperature + ", ");
                //if (y == 31 && x == 0) System.out.println();
                //if (y == 70) System.out.print(cells[y][x].temperature + ", ");
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
                if (cells[y][x] == null) continue;
                List<Cell> neighbourhood = new LinkedList<>();
                for (int i = 0; i < 9; i++) {
                    int deltaX = x - FluidFlowD2Q9.c.get(i).get(0);
                    int deltaY = y + FluidFlowD2Q9.c.get(i).get(1);
                    if (deltaY >= 0 && deltaY <= latticeHeight-1 && deltaX >= 0 && deltaX <= latticeWidth-1) neighbourhood.add(cells[deltaY][deltaX]);
                    else neighbourhood.add(null);
                }
                cells[y][x].model.calcStreaming(neighbourhood);
                cells[y][x].temperatureModel.calcStreaming(neighbourhood);
                cells[y][x].model.calcBoundaryConditions(cells[y][x].getFluidBoundaryType(),cells[y][x].getTempBoundaryType(),cells[y][x].getBoundaryDirection(),cells[y][x].velocity,cells[y][x].density);
                cells[y][x].temperatureModel.calcBoundaryConditions(cells[y][x].getFluidBoundaryType(),cells[y][x].getTempBoundaryType(),cells[y][x].getBoundaryDirection(),cells[y][x].velocity,cells[y][x].temperature);
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
