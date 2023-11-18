package lbm;

public enum CellState {
    FLUID,
    CONST_BC,
    BOUNCE_BACK_BC,
    SYMMETRY_BC,
    INFLOW_OUTFLOW_BC;
}
