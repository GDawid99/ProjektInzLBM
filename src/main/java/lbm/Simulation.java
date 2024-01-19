package lbm;

import util.LatticeInitializer;

public class Simulation {
    private Lattice lattice;

    public int iteration;

    public Simulation() {
        this.iteration = 0;
    }

    public boolean initLBM(int width, int height) {
        System.out.println(width + " " + height);
        this.lattice = LatticeInitializer.initialize(width,height)
                .withTimeStep(1f)
                .withInitializeLattice("data6.txt")
                .build();
        this.lattice.printValues();
        return true;
    }

    public int loopLBM() {
        lattice.executeOperations();
        return iteration++;
    }

    public Lattice getLattice() {
        return this.lattice;
    }

}
