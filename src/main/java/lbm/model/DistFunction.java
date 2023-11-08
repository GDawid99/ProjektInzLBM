package lbm.model;

import util.Velocity;

public class DistFunction {
    public float C, N, S, W, E, NW, NE, SW, SE;

    public DistFunction() {
        this.C = this.N = this.S = this.W = this.E = this.NW = this.NE = this.SW = this.SE = 0f;
    }

    public DistFunction(float C, float N, float NE, float E, float SE, float S, float SW, float W, float NW) {
        this.C = C;
        this.N = N;
        this.S = S;
        this.W = W;
        this.E = E;
        this.NW = NW;
        this.NE = NE;
        this.SW = SW;
        this.SE = SE;
    }

    public DistFunction(DistFunction distFunction) {
        this.C = distFunction.C;
        this.N = distFunction.N;
        this.S = distFunction.S;
        this.W = distFunction.W;
        this.E = distFunction.E;
        this.NW = distFunction.NW;
        this.NE = distFunction.NE;
        this.SW = distFunction.SW;
        this.SE = distFunction.SE;
    }

    public float sum() {
        return C + N + S + W + E + NW + NE + SW + SE;
    }
}
