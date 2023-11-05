package lbm.model;

import util.Velocity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InitValues {
    public static final float UX = 0.2f;
    public static Velocity velocityInitZero() {
        return new Velocity(0f, 0f);
    }
    public static Velocity velocityInitValue() {
        return new Velocity(UX,0f);
    }
}
