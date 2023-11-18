package lbm.model;

import util.Velocity;

public class GlobalValues {
    public static final float UX = 0.002f;
    public static final float UY = 0.2f;
    public static final float TEMPERATURE = 0.5f;
    public static Velocity MIN_VELOCITY = new Velocity(0f,0f,0f);
    public static Velocity MAX_VELOCITY = new Velocity(0f,0f,0f);
    public static float MIN_DENSITY = 0f;
    public static float MAX_DENSITY = 0f;
    public static float MIN_TEMPERATURE = 0f;
    public static float MAX_TEMPERATURE = 0f;
    public static final float TAU = 1f;
    public static final float TAU_TEMPERATURE = 1f;
    public static Velocity velocityInitZero() {
        return new Velocity(0f, 0f);
    }
    public static Velocity velocityInitValue() {
        return new Velocity(UX,0f);
    }
    public static void initGlobalValues() {
        MIN_VELOCITY = new Velocity(0f,0f,0f);
        MAX_VELOCITY = new Velocity(0f,0f,0f);
        MIN_DENSITY = 1f;
        MAX_DENSITY = 1f;
        MIN_TEMPERATURE = 0f;
        MAX_TEMPERATURE = 0f;
    }

}
