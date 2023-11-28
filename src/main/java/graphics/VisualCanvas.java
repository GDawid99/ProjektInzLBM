package graphics;

import javafx.scene.paint.Color;
import lbm.Cell;
import javafx.scene.canvas.Canvas;
import lbm.Lattice;
import lbm.GlobalValues;

import java.util.List;

public class VisualCanvas extends Canvas {
    //metoda kolorująca płótno
    public void draw(Lattice lattice, String currentVisualValue) {
        Cell cell;
        this.getGraphicsContext2D().clearRect(0,0,this.getWidth(),this.getHeight());
        for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
                cell = lattice.getCells()[y][x];
                if (currentVisualValue.equals("Velocity [Vx]")) this.getGraphicsContext2D().getPixelWriter().setColor(x,y,ColorScale.getColor(0.0f, GlobalValues.UX,cell.velocity.ux, currentVisualValue));
                if (currentVisualValue.equals("Velocity [Vy]")) this.getGraphicsContext2D().getPixelWriter().setColor(x,y,ColorScale.getColor(0.0f, GlobalValues.UY ,cell.velocity.uy, currentVisualValue));
                if (currentVisualValue.equals("Density")) this.getGraphicsContext2D().getPixelWriter().setColor(x,y,ColorScale.getColor(0.999f,1.001f,cell.density, currentVisualValue));
                if (currentVisualValue.equals("Temperature")) this.getGraphicsContext2D().getPixelWriter().setColor(x,y,ColorScale.getColor(0.0f,1.0f,cell.temperature, currentVisualValue));
            }
        }
    }


    public void drawLines(Lattice lattice) {
        Cell cell;
        for (int y = 5; y < this.getHeight(); y+=10) {
            for (int x = 5; x < this.getWidth(); x+=10) {
                cell = lattice.getCells()[y][x];
                this.getGraphicsContext2D().setStroke(Color.BLACK);
                this.getGraphicsContext2D().setFill(Color.BLACK);
                this.getGraphicsContext2D().setLineWidth(0.2d);
                this.getGraphicsContext2D().strokeLine(x,y,x+2000*cell.velocity.ux,y-2000*cell.velocity.uy);
            }
        }
    }

    public void scaleLattice(double width, double height) {
        System.out.println(width + " " + height);
        double scaleLattice;
        if (getWidth() > getHeight()) scaleLattice = (width-2)/getWidth();
        else scaleLattice = (height-2)/getHeight();
        this.setScaleX(scaleLattice);
        this.setScaleY(scaleLattice);
    }

}
