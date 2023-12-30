package lbm;

import util.Velocity;

public class Simulation {
    private Lattice lattice;

    public int iteration;
    public final float gravity = 0.000025f;

    public Simulation(int width, int height) {
        this.lattice = new Lattice(width, height);
        this.iteration = 0;
    }

    public int loopLBM() {
        lattice.executeOperations(1f, 1f, 1f, gravity);
        return iteration++;
    }

    public Lattice getLattice() {
        return this.lattice;
    }

}
