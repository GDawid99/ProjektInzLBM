package graphics;

import javafx.scene.paint.Color;
import lbm.*;
import javafx.scene.canvas.Canvas;


public class VisualCanvas extends Canvas {
    //metoda kolorująca płótno
    public void draw(Lattice lattice, String currentVisualValue, float min, float max) {
        Cell cell;
        float value = 0f;
        this.getGraphicsContext2D().clearRect(0,0,this.getWidth(),this.getHeight());
        for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
                cell = lattice.getCells()[y][x];
                if (cell.getCellBoundaryType().isSolid()) {
                    this.getGraphicsContext2D().getPixelWriter().setColor(x,y,Color.BLACK);
                    continue;
                }
                if (currentVisualValue.equals("Velocity [Vx]")) value = cell.velocity.ux;
                if (currentVisualValue.equals("Velocity [Vy]")) value = cell.velocity.uy;
                if (currentVisualValue.equals("Density")) value = cell.density;
                if (currentVisualValue.equals("Temperature")) value = cell.temperature;
                this.getGraphicsContext2D().getPixelWriter().setColor(x,y,ColorScale.getColor(min, max , value, currentVisualValue));
            }
        }
    }


    public void drawLines(Lattice lattice, double length) {
        Cell cell;
        for (int y = 2; y < this.getHeight(); y+=5) {
            for (int x = 2; x < this.getWidth(); x+=5) {
                cell = lattice.getCells()[y][x];
                if (cell == null) continue;
                this.getGraphicsContext2D().setStroke(Color.BLACK);
                this.getGraphicsContext2D().setFill(Color.BLACK);
                this.getGraphicsContext2D().setLineWidth(0.1d);
                this.getGraphicsContext2D().strokeLine(x,y,x+length*100*cell.velocity.ux,y-length*100*cell.velocity.uy);
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
