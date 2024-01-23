package lbm;

import lbm.model.ModelD2Q9;
import util.LatticeInitializer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Lattice {
    public Cell[][] cells;
    private int latticeWidth;
    private int latticeHeight;
    private final float tau;
    private final float tempTau;
    private float timeStep;
    private float gravity;
    private float beta;


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
        this.gravity = latticeInitializer.gravity;
        this.beta = latticeInitializer.beta;
    }

    public void executeOperations() {
        Arrays.stream(cells).parallel().flatMap(arr -> Arrays.stream(arr)).filter(e -> !e.getCellBoundaryType().isSolid()).forEach(cell -> {
            cell.calcMacroscopicValues();
            cell.calcEquilibriumFunction();
            cell.calcCollisionOperation(timeStep, tau, tempTau, gravity, beta);
        });


        Arrays.stream(cells).parallel().flatMap(arr -> Arrays.stream(arr)).filter(e -> !e.getCellBoundaryType().isSolid()).forEach(cell -> {
            int localX = cell.x;
            int localY = cell.y;
            List<Cell> neighbourhood = new LinkedList<>();
            for (int i = 0; i < 9; i++) {
                int deltaX = localX - ModelD2Q9.c.get(i).get(0);
                int deltaY = localY + ModelD2Q9.c.get(i).get(1);
                if (deltaY >= 0 && deltaY <= latticeHeight - 1 && deltaX >= 0 && deltaX <= latticeWidth - 1)
                    neighbourhood.add(cells[deltaY][deltaX]);
                else neighbourhood.add(null);
            }
            cell.calcStreaming(neighbourhood);
            cell.calcBC();
        });
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
