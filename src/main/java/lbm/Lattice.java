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
    public float avg_temperature;
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
                Velocity velocity = new Velocity(0f,0f);
                float density = 1f;
                float temperature = 0f;
                if (y == 0) {
                    BoundaryDirection direction = BoundaryDirection.NORTH;
                    if (x == 0) direction = BoundaryDirection.NORTHWEST;
                    if (x == latticeWidth-1) direction = BoundaryDirection.NORTHEAST;
                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.SYMMETRY_BC, TempBoundaryType.SYMMETRY_BC, direction);
                }
                else if (x > positionXLeftWall && x < positionXRightWall && y > positionYWall) {
                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.WALL, TempBoundaryType.WALL, BoundaryDirection.NONE);;
                }
                else if (y == latticeHeight-1) {
                    BoundaryDirection direction = BoundaryDirection.SOUTH;
                    if (x == 0) direction = BoundaryDirection.SOUTHWEST;
                    if (x == latticeWidth-1) direction = BoundaryDirection.SOUTHEAST;
                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, direction);
                }
                else if (x == 0) {
                    board[y][x] = setInitialValues(x,y,density,(latticeHeight - 1 -y) * (temperature+20f)/128,new Velocity((latticeHeight-1-y)*GlobalValues.UX/latticeHeight,0f), FluidBoundaryType.OPEN_VELOCITY_BC, TempBoundaryType.OPEN_TEMPERATURE_BC, BoundaryDirection.WEST);
                }
                else if (x == latticeWidth-1) {
                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.OPEN_DENSITY_BC, TempBoundaryType.OPEN_TEMPERATURE_BC, BoundaryDirection.EAST);
                }
                else if (x == positionXLeftWall && y > positionYWall) {
                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.EAST);
                }
                else if (x == positionXRightWall && y > positionYWall) {
                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.WEST);
                }
                else if (x > positionXLeftWall && x < positionXRightWall && y == positionYWall) {
                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.SOUTH);
                }
                else {
                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.NONE, TempBoundaryType.NONE, BoundaryDirection.NONE);
                }

                if (x == positionXLeftWall && y == latticeHeight-1) {
                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.SOUTHEAST);
                }
                if (x == positionXLeftWall && y == positionYWall) {
                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.NORTHWEST_TYPE2);
                }
                if (x == positionXRightWall && y == latticeHeight-1) {
                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.SOUTHWEST);
                }
                if (x == positionXRightWall && y == positionYWall) {
                    board[y][x] = setInitialValues(x,y,density,temperature,velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.NORTHEAST_TYPE2);
                }
            }
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
        avg_temperature = 0f;
        avg_velocity = new Velocity(0f,0f);

        //pierwszy etap: obliczenie danych makroskopowych, feq i kolizje
        for (int y = 0; y < latticeHeight; y++) {
            for (int x = 0; x < latticeWidth; x++) {
                Cell cell = cells[y][x];
                if (cell.getFluidBoundaryType() == FluidBoundaryType.WALL) continue;
                cell.calcMacroscopicValues(gravity);
                if (x != 0) cell.calcMacroscopicTemperature();
                cell.model.calcEquilibriumFunctions(cell.velocity, cell.density);
                cell.model.calcOutputFunctions(
                        (ArrayList<Float>) cell.model.getFin(),
                        (ArrayList<Float>) cell.model.getFeq(),
                        1.0f,
                        GlobalValues.TAU);

                cell.temperatureModel.calcEquilibriumFunctions(cell.velocity, cell.temperature);
                cell.temperatureModel.calcOutputFunctions(
                        (ArrayList<Float>) cell.temperatureModel.getTin(),
                        (ArrayList<Float>) cell.temperatureModel.getTeq(),
                        1.0f,
                        GlobalValues.TAU_TEMPERATURE);

                avg_density += cell.density;
                avg_temperature += cell.temperature;
                avg_velocity.ux += cell.velocity.ux;
                avg_velocity.uy += cell.velocity.uy;
            }
        }
        //drugi etap: obliczenie operacji streaming i warunki brzegowe
        for (int y = 0; y < latticeHeight; y++) {
            for (int x = 0; x < latticeWidth; x++) {
                Cell cell = cells[y][x];
                if (cell.getFluidBoundaryType() == FluidBoundaryType.WALL) continue;
                List<Cell> neighbourhood = new LinkedList<>();
                for (int i = 0; i < 9; i++) {
                    int deltaX = x - FluidFlowD2Q9.c.get(i).get(0);
                    int deltaY = y + FluidFlowD2Q9.c.get(i).get(1);
                    if (deltaY >= 0 && deltaY <= latticeHeight-1 && deltaX >= 0 && deltaX <= latticeWidth-1) neighbourhood.add(cells[deltaY][deltaX]);
                    else neighbourhood.add(null);
                }
                cell.model.calcStreaming(neighbourhood);
                cell.temperatureModel.calcStreaming(neighbourhood);
                cell.model.calcBoundaryConditions(cell.getFluidBoundaryType(),cell.getTempBoundaryType(),cell.getBoundaryDirection(),cell.velocity,cell.density);
                cell.temperatureModel.calcBoundaryConditions(cell.getFluidBoundaryType(),cell.getTempBoundaryType(),cell.getBoundaryDirection(),cell.velocity,cell.temperature);
            }
        }
    }

    public Cell[][] getCells() {
        return cells;
    }

    public int getLatticeWidth() {
        return latticeWidth;
    }

    public int getLatticeHeight() {
        return latticeHeight;
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
