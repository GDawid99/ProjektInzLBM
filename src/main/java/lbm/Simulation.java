package lbm;

import util.LatticeInitializer;

public class Simulation {
    private Lattice lattice;

    public int iteration;
    public final float gravity = 0.000025f;

    public Simulation() {
        this.iteration = 0;
    }

    public void initLBM(int width, int height) {
        this.lattice = LatticeInitializer.initialize(width,height)
                .withTau(1f)
                .withTempTau(1f)
                .withTimeStep(1f)
                .withInitializeLattice("data2.txt")
                .build();
        //this.lattice.printValues();
    }

    public int loopLBM() {
        lattice.executeOperations(gravity);
        return iteration++;
    }

    public Lattice getLattice() {
        return this.lattice;
    }

}
