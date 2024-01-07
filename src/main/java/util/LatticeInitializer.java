package util;

import lbm.Cell;
import lbm.Lattice;
import lbm.boundary.BoundaryDirection;
import lbm.boundary.CellBoundaryType;
import lbm.boundary.FluidBoundaryType;
import lbm.boundary.TempBoundaryType;
import lbm.model.FluidFlowD2Q9;
import lbm.model.TemperatureD2Q9;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LatticeInitializer {
    public Cell[][] cells;
    public int latticeWidth;
    public int latticeHeight;
    public float tau;
    public float tempTau;
    public float timeStep;


    private LatticeInitializer(int latticeWidth, int latticeHeight) {
        this.cells = new Cell[latticeHeight][latticeWidth];
        this.latticeWidth = latticeWidth;
        this.latticeHeight = latticeHeight;
    }

    public static LatticeInitializer initialize(int latticeWidth, int latticeHeight) {
        return new LatticeInitializer(latticeWidth,latticeHeight);
    }

    public LatticeInitializer withTau(float tau) {
        this.tau = tau;
        return this;
    }

    public LatticeInitializer withTempTau(float tempTau) {
        this.tempTau = tempTau;
        return this;
    }

    public LatticeInitializer withTimeStep(float timeStep) {
        this.timeStep = timeStep;
        return this;
    }

    public LatticeInitializer withInitializeLattice(String path) {
        cells = initialData(path);
        cells = additionData(path);
        cells = boundaryData(path);
        cells = heatSourceData(path);
        return this;
    }

    private Cell[][] initialData(String path) {
        float density = 0f, temperature = 0f, deltaDensity = 0f, deltaTemperature = 0f;
        Velocity velocity = null;
        Velocity deltaVelocity = new Velocity(0f,0f);
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("#")) {
                    if (line.charAt(0) == '#') continue;
                    else line = line.substring(0,line.indexOf('#'));
                }
                if (line.contains("-INITIAL BOUNDARY CONDITIONS")) break;
                if (line.contains("density")) {
                    if (line.contains("linear")) {
                        density = Float.parseFloat(line.substring(line.indexOf("(") + 1, line.indexOf(",")));
                        line = line.substring(line.indexOf(",") + 1, line.indexOf(")"));
                        float maxDensity = Float.parseFloat(line);
                        deltaDensity = (maxDensity-density)/(latticeHeight-1);
                    }
                    else density = Float.parseFloat(line.substring(line.indexOf("=")+1,line.indexOf(";")));
                }
                else if (line.contains("temperature")) {
                    if (line.contains("linear")) {
                        temperature = Float.parseFloat(line.substring(line.indexOf("(") + 1, line.indexOf(",")));
                        line = line.substring(line.indexOf(",") + 1, line.indexOf(")"));
                        float maxTemperature = Float.parseFloat(line);
                        deltaTemperature = (maxTemperature-temperature)/(latticeHeight-1);
                    }
                    else temperature = Float.parseFloat(line.substring(line.indexOf("=")+1,line.indexOf(";")));
                }
                else if (line.contains("velocity")) {
                    if (line.contains("linear")) {
                        line = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                        float ux = Float.parseFloat(line.substring(line.indexOf("[") + 1, line.indexOf(",")));
                        float uy = Float.parseFloat(line.substring(line.indexOf(",") + 1, line.indexOf("]")));
                        velocity = new Velocity(ux, uy);
                        line = line.replace(line.substring(line.indexOf("["), line.indexOf("]")+2), "");
                        System.out.println(line);
                        ux = Float.parseFloat(line.substring(line.indexOf("[") + 1, line.indexOf(",")));
                        uy = Float.parseFloat(line.substring(line.indexOf(",") + 1, line.indexOf("]")));
                        Velocity maxVelocity = new Velocity(ux,uy);
                        deltaVelocity = new Velocity((maxVelocity.ux - velocity.ux)/(latticeHeight-1),(maxVelocity.uy - velocity.uy)/(latticeHeight-1));
                    }
                    else {
                        float ux = Float.parseFloat(line.substring(line.indexOf("[") + 1, line.indexOf(",")));
                        float uy = Float.parseFloat(line.substring(line.indexOf(",") + 1, line.indexOf("]")));
                        velocity = new Velocity(ux, uy);
                    }
                }
            }
            cells = generateDefaultFluidCells(density,deltaDensity,temperature,deltaTemperature,velocity,deltaVelocity);
        }
        catch (IOException e) {
            System.err.println("I/O: Blad z plikiem.");
            System.exit(-1);
        }
        return cells;
    }

    private Cell[][] additionData(String path) {
        BufferedReader br;
        boolean isUsedPartOfFile = false;
        try {
            br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("#")) {
                    if (line.charAt(0) == '#') continue;
                    else line = line.substring(0,line.indexOf('#'));
                }
                if (line.contains("-ADDITION DATA-")) isUsedPartOfFile = true;
                if (isUsedPartOfFile) {
                    if (line.contains("-SHAPE")) {
                        getWallData(line,true);
                    }
                }
            }
        }
        catch (IOException e) {
            System.err.println("I/O: Blad z plikiem.");
            System.exit(-1);
        }
        return cells;
    }

    private int[] getWallData(String line, boolean generateWall) {
        String s = line.substring(line.indexOf("(")+1,line.indexOf(")"));
        switch (line.substring(line.indexOf(":") + 1, line.indexOf("("))) {
            case "RECTANGLE" -> {
                int x1 = Integer.parseInt(s.substring(0, s.indexOf(",")));
                s = s.substring(s.indexOf(",") + 1);
                int y1 = Integer.parseInt(s.substring(0, s.indexOf(",")));
                s = s.substring(s.indexOf(",") + 1);
                int x2 = Integer.parseInt(s.substring(0, s.indexOf(",")));
                s = s.substring(s.indexOf(",") + 1);
                int y2 = Integer.parseInt(s);
                if (generateWall) generateRectangleWall(x1, y1, x2, y2);
                else return new int[]{x1, y1, x2, y2};
            }
            case "TRIANGLE" -> {
                int x1 = Integer.parseInt(s.substring(0, s.indexOf(",")));
                s = s.substring(s.indexOf(",") + 1);
                int y1 = Integer.parseInt(s.substring(0, s.indexOf(",")));
                s = s.substring(s.indexOf(",") + 1);
                int x2 = Integer.parseInt(s.substring(0, s.indexOf(",")));
                s = s.substring(s.indexOf(",") + 1);
                int y2 = Integer.parseInt(s.substring(0, s.indexOf(",")));
                s = s.substring(s.indexOf(",") + 1);
                int x3 = Integer.parseInt(s.substring(0, s.indexOf(",")));
                s = s.substring(s.indexOf(",") + 1);
                int y3 = Integer.parseInt(s);
                if (generateWall) generateTriangleWall(x1, y1, x2, y2, x3, y3);
                else return new int[]{x1, y1, x2, y2, x3, y3};
            }
            case "FLOOR" -> {
                int x1 = Integer.parseInt(s.substring(0, s.indexOf(",")));
                s = s.substring(s.indexOf(",") + 1);
                int x2 = Integer.parseInt(s);
                return new int[]{x1, x2};
            }
        }
        if (generateWall) return new int[0];
        throw new IllegalArgumentException("Niepoprawna nazwa kształtu.");
    }

    private Cell[][] boundaryData(String path) {
        BufferedReader br;
        float density = 0f, temperature = 0f, deltaDensity = 0f, deltaTemperature = 0f;
        Velocity velocity = null;
        Velocity deltaVelocity = new Velocity(0f,0f);
        FluidBoundaryType fluidBoundaryType = FluidBoundaryType.FLUID;
        TempBoundaryType tempBoundaryType = TempBoundaryType.FLUID;
        BoundaryDirection bd = BoundaryDirection.NONE;
        boolean isBCPartOfFile = false;
        boolean isAdditionPartOfFile = false;
        int x1 = 0, x2 = 0, x3 = 0, y1 = 0, y2 = 0, y3 = 0;
        String typeOfShape = "";
        try {
            br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("#")) {
                    if (line.charAt(0) == '#') continue;
                    else line = line.substring(0,line.indexOf('#'));
                }
                if (line.contains("-INITIAL BOUNDARY CONDITIONS")) isBCPartOfFile = true;
                if (line.contains("-ADDITION DATA-")) {
                    isBCPartOfFile = false;
                    isAdditionPartOfFile = true;
                }
                if (isBCPartOfFile) {
                    if (line.contains("NORTH")) bd = BoundaryDirection.NORTH;
                    if (line.contains("SOUTH")) bd = BoundaryDirection.SOUTH;
                    if (line.contains("WEST")) bd = BoundaryDirection.WEST;
                    if (line.contains("EAST")) bd = BoundaryDirection.EAST;
                    if (line.contains("NORTHWEST_CONCAVE")) bd = BoundaryDirection.NORTHWEST_CONCAVE;
                    if (line.contains("NORTHEAST_CONCAVE")) bd = BoundaryDirection.NORTHEAST_CONCAVE;
                    if (line.contains("SOUTHWEST_CONCAVE")) bd = BoundaryDirection.SOUTHWEST_CONCAVE;
                    if (line.contains("SOUTHEAST_CONCAVE")) bd = BoundaryDirection.SOUTHEAST_CONCAVE;
                    if (line.contains("NORTHWEST_CONVEX")) bd = BoundaryDirection.NORTHWEST_CONVEX;
                    if (line.contains("NORTHEAST_CONVEX")) bd = BoundaryDirection.NORTHEAST_CONVEX;
                    if (line.contains("SOUTHWEST_CONVEX")) bd = BoundaryDirection.SOUTHWEST_CONVEX;
                    if (line.contains("SOUTHEAST_CONVEX")) bd = BoundaryDirection.SOUTHEAST_CONVEX;
                }
                if (!isAdditionPartOfFile && isBCPartOfFile) {
                    if (line.contains("density")) {
                        if (line.contains("linear")) {
                            density = Float.parseFloat(line.substring(line.indexOf("(") + 1, line.indexOf(",")));
                            line = line.substring(line.indexOf(",") + 1, line.indexOf(")"));
                            float maxDensity = Float.parseFloat(line);
                            deltaDensity = (maxDensity-density)/(latticeHeight-1);
                        }
                        else density = Float.parseFloat(line.substring(line.indexOf("=")+1, line.indexOf(";")));
                    }
                    else if (line.contains("temperature")) {
                        if (line.contains("linear")) {
                            temperature = Float.parseFloat(line.substring(line.indexOf("(") + 1, line.indexOf(",")));
                            line = line.substring(line.indexOf(",") + 1, line.indexOf(")"));
                            float maxTemperature = Float.parseFloat(line);
                            deltaTemperature = (maxTemperature-temperature)/(latticeHeight-1);
                        }
                        temperature = Float.parseFloat(line.substring(line.indexOf("=")+1, line.indexOf(";")));
                    }
                    else if (line.contains("velocity")) {
                        if (line.contains("linear")) {
                            System.out.println(line);
                            line = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                            float ux = Float.parseFloat(line.substring(line.indexOf("[") + 1, line.indexOf(",")));
                            float uy = Float.parseFloat(line.substring(line.indexOf(",") + 1, line.indexOf("]")));
                            velocity = new Velocity(ux, uy);
                            line = line.replace(line.substring(line.indexOf("["), line.indexOf("]")+2), "");
                            System.out.println(line);
                            ux = Float.parseFloat(line.substring(line.indexOf("[") + 1, line.indexOf(",")));
                            uy = Float.parseFloat(line.substring(line.indexOf(",") + 1, line.indexOf("]")));
                            Velocity maxVelocity = new Velocity(ux,uy);
                            deltaVelocity = new Velocity((maxVelocity.ux - velocity.ux)/(latticeHeight-1),(maxVelocity.uy - velocity.uy)/(latticeHeight-1));
                        }
                        else {
                            float ux = Float.parseFloat(line.substring(line.indexOf("[") + 1, line.indexOf(",")));
                            float uy = Float.parseFloat(line.substring(line.indexOf(",") + 1, line.indexOf("]")));
                            velocity = new Velocity(ux, uy);
                        }
                    }
                    else if (line.contains("fluid_boundary_type")) fluidBoundaryType = FluidBoundaryType.valueOf(line.substring(line.indexOf("=")+1,line.indexOf(";")));
                    else if (line.contains("temp_boundary_type")) {
                        tempBoundaryType = TempBoundaryType.valueOf(line.substring(line.indexOf("=")+1,line.indexOf(";")));
                        switch (bd) {
                            case NORTH -> generateBoundaryCellsForConstY(1,0,latticeWidth-1,density,deltaDensity,temperature,deltaTemperature,velocity,deltaVelocity,new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                            case SOUTH -> generateBoundaryCellsForConstY(1,latticeHeight-1,latticeWidth-1,density,deltaDensity,temperature,deltaTemperature,velocity,deltaVelocity,new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                            case EAST -> generateBoundaryCellsForConstX(latticeWidth-1,1,latticeHeight-1,density,deltaDensity,temperature,deltaTemperature,velocity,deltaVelocity,new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                            case WEST -> generateBoundaryCellsForConstX(0,1,latticeHeight-1,density,deltaDensity,temperature,deltaTemperature,velocity,deltaVelocity,new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                            case NORTHEAST_CONCAVE -> generateBoundaryCellCorner(latticeWidth-1,0,density,temperature,velocity,new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                            case NORTHWEST_CONCAVE -> generateBoundaryCellCorner(0,0,density,temperature,velocity,new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                            case SOUTHEAST_CONCAVE -> generateBoundaryCellCorner(latticeWidth-1,latticeHeight-1,density,temperature,velocity,new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                            case SOUTHWEST_CONCAVE -> generateBoundaryCellCorner(0,latticeHeight-1,density,temperature,velocity,new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                        }
                    }
                }
                else {
                    if (line.contains("SHAPE:")) {
                        switch (typeOfShape = line.substring(line.indexOf(":")+1, line.indexOf("("))) {
                            case "RECTANGLE": {
                                int[] values = getWallData(line,false);
                                x1 = values[0];
                                y1 = values[1];
                                x2 = values[2];
                                y2 = values[3];
                            }
                            case "TRIANGLE": {
                                break;
                            }
                            case "FLOOR": {
                                break;
                            }
                            default: {
                                throw new IllegalArgumentException("Niepoprawna nazwa przeszkody.");
                            }
                        }
                    }
                    if (typeOfShape.equals("RECTANGLE")) {
                        if (line.contains("density")) {
                            if (line.contains("linear")) {
                                density = Float.parseFloat(line.substring(line.indexOf("(") + 1, line.indexOf(",")));
                                line = line.substring(line.indexOf(",") + 1, line.indexOf(")"));
                                float maxDensity = Float.parseFloat(line);
                                deltaDensity = (maxDensity-density)/(latticeHeight-1);
                            }
                            else density = Float.parseFloat(line.substring(line.indexOf("=")+1, line.indexOf(";")));
                        }
                        else if (line.contains("temperature")) {
                            if (line.contains("linear")) {
                                temperature = Float.parseFloat(line.substring(line.indexOf("(") + 1, line.indexOf(",")));
                                line = line.substring(line.indexOf(",") + 1, line.indexOf(")"));
                                float maxTemperature = Float.parseFloat(line);
                                deltaTemperature = (maxTemperature-temperature)/(latticeHeight-1);
                            }
                            temperature = Float.parseFloat(line.substring(line.indexOf("=")+1, line.indexOf(";")));
                        }
                        else if (line.contains("velocity")) {
                            if (line.contains("linear")) {
                                System.out.println(line);
                                line = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                                float ux = Float.parseFloat(line.substring(line.indexOf("[") + 1, line.indexOf(",")));
                                float uy = Float.parseFloat(line.substring(line.indexOf(",") + 1, line.indexOf("]")));
                                velocity = new Velocity(ux, uy);
                                line = line.replace(line.substring(line.indexOf("["), line.indexOf("]")+2), "");
                                System.out.println(line);
                                ux = Float.parseFloat(line.substring(line.indexOf("[") + 1, line.indexOf(",")));
                                uy = Float.parseFloat(line.substring(line.indexOf(",") + 1, line.indexOf("]")));
                                Velocity maxVelocity = new Velocity(ux,uy);
                                deltaVelocity = new Velocity((maxVelocity.ux - velocity.ux)/(latticeHeight-1),(maxVelocity.uy - velocity.uy)/(latticeHeight-1));
                            }
                            else {
                                float ux = Float.parseFloat(line.substring(line.indexOf("[") + 1, line.indexOf(",")));
                                float uy = Float.parseFloat(line.substring(line.indexOf(",") + 1, line.indexOf("]")));
                                velocity = new Velocity(ux, uy);
                            }
                        }
                        else if (line.contains("fluid_boundary_type")) fluidBoundaryType = FluidBoundaryType.valueOf(line.substring(line.indexOf("=")+1,line.indexOf(";")));
                        else if (line.contains("temp_boundary_type")) {
                            System.out.println(bd);
                            switch (bd) {
                                case NORTH -> generateBoundaryCellsForConstY(x1, y2 + 1, x1 + (x2 - x1 + 1),density,deltaDensity,temperature,deltaTemperature,velocity,deltaVelocity, new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                                case SOUTH -> {
                                    System.out.println("[" + x1 + "," + (y1-1) + "]");
                                    System.out.println(fluidBoundaryType);
                                    System.out.println(tempBoundaryType);
                                    generateBoundaryCellsForConstY(x1, y1 - 1, x1 + (x2 - x1 + 1),density,deltaDensity,temperature,deltaTemperature,velocity,deltaVelocity, new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                                }
                                case WEST -> generateBoundaryCellsForConstX(x2 + 1, y1, y1 + (y2 - y1 + 1),density,deltaDensity,temperature,deltaTemperature,velocity,deltaVelocity, new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                                case EAST -> generateBoundaryCellsForConstX(x1 - 1, y1, y1 + (y2 - y1 + 1),density,deltaDensity,temperature,deltaTemperature,velocity,deltaVelocity, new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                                case NORTHEAST_CONCAVE -> {
                                    if (y1 <= 0)
                                        generateBoundaryCellCorner(x1-1,y1-1, density, temperature, velocity, new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                                }
                                case NORTHWEST_CONCAVE -> {
                                    if (y1 <= 0)
                                        generateBoundaryCellCorner(x2+1,y1-1, density, temperature, velocity, new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                                }
                                case SOUTHEAST_CONCAVE -> {
                                    if (y2 >= latticeHeight-1)
                                        generateBoundaryCellCorner(x1-1,y2+1, density, temperature, velocity, new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                                }
                                case SOUTHWEST_CONCAVE -> {
                                    if (y2 >= latticeHeight-1)
                                        generateBoundaryCellCorner(x2+1,y2+1, density, temperature, velocity, new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                                }
                                case NORTHWEST_CONVEX -> generateBoundaryCellCorner(x2+1,y2+1, density, temperature, velocity, new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                                case NORTHEAST_CONVEX -> generateBoundaryCellCorner(x1-1,y2+1, density, temperature, velocity, new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                                case SOUTHWEST_CONVEX -> generateBoundaryCellCorner(x2+1,y1-1, density, temperature, velocity, new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                                case SOUTHEAST_CONVEX -> generateBoundaryCellCorner(x1-1,y1-1, density, temperature, velocity, new CellBoundaryType(fluidBoundaryType,tempBoundaryType,bd));
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            System.err.println("I/O: Blad z plikiem.");
            System.exit(-1);
        }
        return cells;
    }

    private Cell[][] heatSourceData(String path) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("#")) {
                    if (line.charAt(0) == '#') continue;
                    else line = line.substring(0,line.indexOf('#'));
                }
                if (line.contains("source")) {
                    line = line.substring(line.indexOf("(")+1,line.indexOf(")"));
                    int x1 = Integer.parseInt(line.substring(0, line.indexOf(",")));
                    line = line.substring(line.indexOf(",") + 1);
                    int y1 = Integer.parseInt(line.substring(0, line.indexOf(",")));
                    line = line.substring(line.indexOf(",") + 1);
                    int x2 = Integer.parseInt(line.substring(0, line.indexOf(",")));
                    line = line.substring(line.indexOf(",") + 1);
                    int y2 = Integer.parseInt(line.substring(0, line.indexOf(",")));
                    line = line.substring(line.indexOf(",") + 1);
                    float temperature = Float.parseFloat(line);
                    if (x1 == x2) for (int i = y1; i < y1 + (y2 - y1 + 1); i++) {
                        cells[i][x1].isHeatSource = true;
                        cells[i][x1].temperature = temperature;
                    }
                    else if (y1 == y2) for (int i = x1; i < x1 + (x2 - x1 + 1); i++) {
                        cells[y1][i].isHeatSource = true;
                        cells[y1][i].temperature = temperature;
                    }
                    //DO POPRAWY
                    else throw new IllegalArgumentException("Bledne dane w pliku w sekcji HEAT SOURCE: linie muszą być prostopadłe do ścian siatki.");
                }
            }
        }
        catch (IOException e) {
            System.err.println("I/O: Blad z plikiem.");
            System.exit(-1);
        }
        return cells;
    }

    public Lattice build() {
        return new Lattice(this);
    }


    private Cell[][] generateRectangleWall(int x1, int y1, int x2, int y2) {
        //obsługa błędów
        if (x1 < 0) x1 = 0;
        if (x2 > latticeWidth-1) x2 = latticeWidth-1;
        if (y1 < 0) y1 = 0;
        if (y2 > latticeHeight-1) y2 = latticeHeight-1;
        for (int y = y1; y <= y2; y++) {
            for (int x = x1; x <= x2; x++) {
                cells[y][x] = initSolidCell(x,y);
            }
        }
        return cells;
    }

    private Cell[][] generateDefaultFluidCells(float minDensity, float deltaDensity, float minTemperature, float deltaTemperature, Velocity minVelocity,  Velocity deltaVelocity) {
        for (int y = 0; y < latticeHeight; y++) {
            for (int x = 0; x < latticeWidth; x++) {
                cells[y][x] = setInitialValues(x,y,minDensity,minTemperature,new Velocity(minVelocity),new CellBoundaryType(true));
            }
            minDensity += deltaDensity;
            minTemperature += deltaTemperature;
            minVelocity = new Velocity(minVelocity.ux + deltaVelocity.ux, minVelocity.uy + deltaVelocity.uy);
        }
        return cells;
    }

    private Cell[][] generateBoundaryCellsForConstX(int startX, int startY, int length, float density, float deltaDensity, float temperature, float deltaTemperature, Velocity velocity, Velocity deltaVelocity, CellBoundaryType boundaryType) {
        if (startX < 0) startX = 0;
        if (startX > latticeWidth-1) startX = latticeWidth-1;
        if (startY < 0) startY = 0;
        if (startY > latticeHeight-1) startY = latticeHeight-1;
        if (length > 128) length = 128;
        for (int i = startY; i < length; i++) {
            if (!cells[i][startX].getCellBoundaryType().isSolid()) cells[i][startX] = setInitialValues(startX,i,density,temperature,velocity,boundaryType);
            density += deltaDensity;
            temperature += deltaTemperature;
            velocity = new Velocity(velocity.ux + deltaVelocity.ux, velocity.uy + deltaVelocity.uy);
        }
        return cells;
    }

    private Cell[][] generateBoundaryCellsForConstY(int startX, int startY, int length, float density, float deltaDensity, float temperature, float deltaTemperature, Velocity velocity, Velocity deltaVelocity, CellBoundaryType boundaryType) {
        if (startX < 0) startX = 0;
        if (startX > latticeWidth-1) startX = latticeWidth-1;
        if (startY < 0) startY = 0;
        if (startY > latticeHeight-1) startY = latticeHeight-1;
        if (length > 128) length = 128;
        for (int i = startX; i < length; i++) {
            if (!cells[startY][i].getCellBoundaryType().isSolid()) cells[startY][i] = setInitialValues(i,startY,density,temperature,velocity,boundaryType);
            density += deltaDensity;
            temperature += deltaTemperature;
            velocity = new Velocity(velocity.ux + deltaVelocity.ux, velocity.uy + deltaVelocity.uy);
        }
        return cells;
    }

    private Cell generateBoundaryCellCorner(int x, int y, float density, float temperature, Velocity velocity, CellBoundaryType boundaryType) {
        if (x < 0) x = 0;
        if (x > latticeWidth-1) x = latticeWidth-1;
        if (y < 0) y = 0;
        if (y > latticeHeight-1) y = latticeHeight-1;
        cells[y][x] = setInitialValues(x,y,density,temperature,velocity,boundaryType);
        return cells[y][x];
    }

    public Cell[][] generateTriangleWall(int x1, int y1, int x2, int y2, int x3, int y3) {
        return cells;
    }

    private Cell initSolidCell(int x, int y) {
        return setInitialValues(x,y,1f,0f,new Velocity(0f,0f),new CellBoundaryType(false));
    }

    private Cell setInitialValues(int x, int y, float density, float temperature, Velocity velocity, CellBoundaryType cellBoundaryType) {
        Cell cell = new Cell(x,y, density, temperature, velocity, cellBoundaryType.getFluidBoundaryType(), cellBoundaryType.getTempBoundaryType(), cellBoundaryType.getBoundaryDirection(), new FluidFlowD2Q9(), new TemperatureD2Q9());
        cell.model.calcEquilibriumFunctions(cell.velocity, cell.density);
        cell.model.calcInputFunctions(cell.model.feq);
        cell.temperatureModel.calcEquilibriumFunctions(cell.velocity, cell.temperature);
        cell.temperatureModel.calcInputFunctions(cell.temperatureModel.feq);
        return cell;
    }


}
