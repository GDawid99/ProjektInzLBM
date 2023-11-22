package graphics;

import javafx.scene.paint.Color;
import lbm.Cell;
import javafx.scene.canvas.Canvas;
import lbm.Lattice;
import lbm.model.GlobalValues;

public class VisualCanvas extends Canvas {
    //metoda kolorująca płótno
    public void draw(Lattice lattice, String visualValue) {
        Cell cell;
        this.getGraphicsContext2D().clearRect(0,0,this.getWidth(),this.getHeight());
        for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
                cell = lattice.getCells()[y][x];
                if (visualValue.equals("VelocityX")) this.getGraphicsContext2D().getPixelWriter().setColor(x,y,cell.getColor(0.0f, GlobalValues.UX,cell.velocity.ux));
                if (visualValue.equals("VelocityY")) this.getGraphicsContext2D().getPixelWriter().setColor(x,y,cell.getColor(0.0f, GlobalValues.UY ,cell.velocity.uy));
                if (visualValue.equals("Density")) this.getGraphicsContext2D().getPixelWriter().setColor(x,y,cell.getColor(GlobalValues.MIN_DENSITY,GlobalValues.MAX_DENSITY,cell.density));
                if (visualValue.equals("Temperature")) this.getGraphicsContext2D().getPixelWriter().setColor(x,y,cell.getColor(0.0f,1.0f,cell.temperature));
            }
        }
    }

    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
    }


}
