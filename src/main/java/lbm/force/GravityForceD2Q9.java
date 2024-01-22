package lbm.force;

import lbm.model.ModelD2Q9;

public class GravityForceD2Q9 implements Force {
    private float density;
    private float gravity;
    private boolean isUsed;

    public GravityForceD2Q9() {}

    public GravityForceD2Q9(float density, float gravity) {
        this.density = density;
        this.gravity = gravity;
        this.isUsed = true;
    }

    @Override
    public float calcForce(int id) {
        if (!isUsed) return 0f;
        return -3f* ModelD2Q9.w.get(id)*density*gravity*ModelD2Q9.c.get(id).get(1);
    }
}
