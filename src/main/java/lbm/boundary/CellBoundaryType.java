package lbm.boundary;

public class CellBoundaryType {
    private final FluidBoundaryType fluidBoundaryType;
    private final TempBoundaryType tempBoundaryType;
    private final BoundaryDirection boundaryDirection;

    public CellBoundaryType(boolean isFluid) {
        if (isFluid) {
            this.fluidBoundaryType = FluidBoundaryType.FLUID;
            this.tempBoundaryType = TempBoundaryType.FLUID;
        }
        else {
            this.fluidBoundaryType = FluidBoundaryType.SOLID;
            this.tempBoundaryType = TempBoundaryType.SOLID;
        }
        this.boundaryDirection = BoundaryDirection.NONE;
    }

    public CellBoundaryType(FluidBoundaryType fluidBoundaryType, TempBoundaryType tempBoundaryType, BoundaryDirection boundaryDirection) {
        this.fluidBoundaryType = fluidBoundaryType;
        this.tempBoundaryType = tempBoundaryType;
        this.boundaryDirection = boundaryDirection;
    }

    public FluidBoundaryType getFluidBoundaryType() {
        return fluidBoundaryType;
    }

    public TempBoundaryType getTempBoundaryType() {
        return tempBoundaryType;
    }

    public BoundaryDirection getBoundaryDirection() {
        return boundaryDirection;
    }

    public boolean isBoundary() {
        return (fluidBoundaryType != FluidBoundaryType.FLUID && fluidBoundaryType != FluidBoundaryType.SOLID)
                || (tempBoundaryType != TempBoundaryType.FLUID && tempBoundaryType != TempBoundaryType.SOLID)
                || boundaryDirection != BoundaryDirection.NONE;
    }

    public boolean isSolid() {
        return (fluidBoundaryType == FluidBoundaryType.SOLID && tempBoundaryType == TempBoundaryType.SOLID);
    }

    public boolean isFluid() {
        return (fluidBoundaryType == FluidBoundaryType.FLUID && tempBoundaryType == TempBoundaryType.FLUID);
    }

    @Override
    public String toString() {
        return "CellBoundaryType{" +
                "fluidBoundaryType=" + fluidBoundaryType +
                ", tempBoundaryType=" + tempBoundaryType +
                ", boundaryDirection=" + boundaryDirection +
                '}';
    }
}
