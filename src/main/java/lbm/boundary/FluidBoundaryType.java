package lbm.boundary;

public enum FluidBoundaryType {
    NONE,
    WALL,
    CONST_BC,
    BOUNCE_BACK_BC,
    SYMMETRY_BC,
    OPEN_DENSITY_BC,
    OPEN_VELOCITY_BC;
}
