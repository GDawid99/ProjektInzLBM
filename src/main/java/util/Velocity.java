package util;

public class Velocity {
    public float ux;
    public float uy;
    public float uz;


    public Velocity(float ux, float uy, float uz) {
        this.ux = ux;
        this.uy = uy;
        this.uz = uz;
    }

    public Velocity(float ux, float uy) {
        this.ux = ux;
        this.uy = uy;
        this.uz = 0.0f;
    }

    public Velocity(Velocity u) {
        this.ux = u.ux;
        this.uy = u.uy;
        this.uz = u.uz;
    }

    @Override
    public String toString() {
        return "[" + ux + ", " + uy + ", " + uz + "]";
    }
}
