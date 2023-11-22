package lbm;

public enum CellState {
    FLUID,
    CONST_BC,
    BOUNCE_BACK_BC,
    OPEN_DENSITY_BC,
    OPEN_VELOCITY_BC;
}
