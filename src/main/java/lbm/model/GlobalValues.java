package lbm.model;

import util.Velocity;

public class GlobalValues {
    public static final float UX = 0.02f;
    public static final float UY = 0.2f;
    public static final float TEMPERATURE = 0.5f;
    public static Velocity MIN_VELOCITY = new Velocity(0f,0f,0f);
    public static Velocity MAX_VELOCITY = new Velocity(0f,0f,0f);
    public static float MIN_DENSITY = 0f;
    public static float MAX_DENSITY = 0f;
    public static float MIN_TEMPERATURE = 0f;
    public static float MAX_TEMPERATURE = 0f;
    public static final float TAU = 0.7f;
    public static final float TAU_TEMPERATURE = 0.7f;
    public static Velocity velocityInitZero() {
        return new Velocity(0f, 0f);
    }
    public static Velocity velocityInitValue() {
        return new Velocity(UX,0f);
    }
}
