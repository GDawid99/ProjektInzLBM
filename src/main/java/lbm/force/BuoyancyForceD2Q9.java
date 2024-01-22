package lbm.force;

import lbm.model.ModelD2Q9;

public class BuoyancyForceD2Q9 implements Force {
    private float density;
    private float temperature;
    private float beta;
    private float gravity;
    private float temperature_ref;
    private boolean isUsed;

    public BuoyancyForceD2Q9() {}

    public BuoyancyForceD2Q9(float density, float gravity, float beta, float temperature, float temperature_ref) {
        this.density = density;
        this.gravity = gravity;
        this.beta = beta;
        this.temperature = temperature;
        this.temperature_ref = temperature_ref;
        this.isUsed = true;
    }

    @Override
    public float calcForce(int id) {
        if (!isUsed) return 0f;
        return 3f* ModelD2Q9.w.get(id)*density*gravity*beta*ModelD2Q9.c.get(id).get(1)*(temperature-temperature_ref);
    }
}
