package util;

import lbm.Cell;
import lbm.boundary.BoundaryDirection;
import lbm.boundary.FluidBoundaryType;
import lbm.boundary.TempBoundaryType;
import lbm.model.FluidFlowD2Q9;
import lbm.model.TemperatureD2Q9;

import java.util.List;

//public class CellInitializer {
//
//    private BoundaryDirection setBoundaryConditionDirection(List<Cell> cells) {
//        if (cells.get(0).getFluidBoundaryType() == FluidBoundaryType.SOLID) return BoundaryDirection.NONE;
//        int[] values = {0,0,0,0,0};
//        int j = 0;
//        for (int i = 1; i < 9; i++) {
//            if (cells.get(i) != null || cells.get(i).getFluidBoundaryType() != FluidBoundaryType.SOLID) {
//                while(j < 5) {
//                    if (values[j] != 0) {
//                        values[j] = i;
//                        break;
//                    }
//                    j++;
//                }
//                j = 0;
//            }
//        }
//        for (int i = 0; i < values.length; i++) {
//            if (values[i] != 0) j++;
//        }
//        if (j == 0) return BoundaryDirection.NONE;
//        if (j == 1) {
//            switch (values[0]) {
//                case 1: return BoundaryDirection.NORTH_CORNER;
//                case 2: return BoundaryDirection.NORTHEAST_CONVEX;
//                case 3: return BoundaryDirection.EAST_CORNER;
//                case 4: return BoundaryDirection.SOUTHEAST_CONVEX;
//                case 5: return BoundaryDirection.SOUTH_CORNER;
//                case 6: return BoundaryDirection.SOUTHWEST_CONVEX;
//                case 7: return BoundaryDirection.WEST_CORNER;
//                case 8: return BoundaryDirection.NORTHWEST_CONVEX;
//            }
//        }
//        if (j == 2) {
//            switch (values[0]) {
//                case 1: return BoundaryDirection.NORTH;
//                case 3: return BoundaryDirection.EAST;
//                case 5: return BoundaryDirection.SOUTH;
//                case 7: return BoundaryDirection.WEST;
//            }
//            switch (values[1]) {
//                case 1: return BoundaryDirection.NORTH;
//                case 3: return BoundaryDirection.EAST;
//                case 5: return BoundaryDirection.SOUTH;
//                case 7: return BoundaryDirection.WEST;
//            }
//        }
//        if (j == 3) {
//
//        }
//    }
//
//    public Cell[][] generateRectangleWall(Cell[][] lattice, int x1, int y1, int x2, int y2) {
//        for (int y = 0; y < lattice.length; y++) {
//            for (int x = 0; x < lattice[0].length; x++) {
//                if (x == x1 && y > y1) {
//                    lattice[y][x] = setInitialValues(x, y, density, 1f, velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.EAST);
//                }
//                if (x == x2 && y > y1) {
//                    lattice[y][x] = setInitialValues(x, y, density, temperature, velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.WEST);
//                }
//                if (x > x1 && x < x2 && y == y1) {
//                    lattice[y][x] = setInitialValues(x, y, density, 1f, velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.SOUTH);
//                }
//                if (x == x1 && y == y2) {
//                    lattice[y][x] = setInitialValues(x, y, density, 1f, velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.SOUTHEAST_CONCAVE);
//                }
//                if (x == x1 && y == y1) {
//                    lattice[y][x] = setInitialValues(x, y, density, temperature, velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.NORTHWEST_CONVEX);
//                }
//                if (x == x2 && y == y2) {
//                    lattice[y][x] = setInitialValues(x, y, density, temperature, velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.SOUTHWEST_CONCAVE);
//                }
//                if (x == x2 && y == y1) {
//                    lattice[y][x] = setInitialValues(x, y, density, temperature, velocity, FluidBoundaryType.BOUNCE_BACK_BC, TempBoundaryType.BOUNCE_BACK_BC, BoundaryDirection.NORTHEAST_CONVEX);
//                }
//            }
//            return lattice;
//        }
//    }
//
//    public Cell[][] generateTriangleWall(Cell[][] lattice, int x1, int y1, int x2, int y2, int x3, int y3) {
//        return lattice;
//    }
//
//    public Cell[][] generateNonStandardFloor(Cell[][] lattice, int x1, int x2) {
//        return lattice;
//    }
//
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
//
//    private Cell setInitialValues(int x, int y, float density, float temperature, Velocity velocity, FluidBoundaryType fluidBoundaryType, TempBoundaryType tempBoundaryType, BoundaryDirection direction) {
//        Cell cell = new Cell(x,y, density, temperature, velocity, fluidBoundaryType, tempBoundaryType, direction, new FluidFlowD2Q9(), new TemperatureD2Q9());
//        cell.model.calcEquilibriumFunctions(cell.velocity, cell.density);
//        cell.model.calcInputFunctions(cell.model.feq);
//        cell.temperatureModel.calcEquilibriumFunctions(cell.velocity, cell.temperature);
//        cell.temperatureModel.calcInputFunctions(cell.temperatureModel.feq);
//        return cell;
//    }
//
//
//    public Cell setValuesInLatticeCell() {
//
//        return new Cell();
//    }
//
//}
