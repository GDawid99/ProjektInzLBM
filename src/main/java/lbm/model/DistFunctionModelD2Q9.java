package lbm.model;

public class DistFunctionModelD2Q9 {
    public float C, N, S, W, E, NW, NE, SW, SE;

    public DistFunctionModelD2Q9() {
        this.C = this.N = this.S = this.W = this.E = this.NW = this.NE = this.SW = this.SE = 0f;
    }

    public DistFunctionModelD2Q9(float C, float N, float S, float W, float E, float NW, float NE, float SW, float SE) {
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



}
