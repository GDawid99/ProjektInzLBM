package graphics;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public class GradientBarCanvas extends Canvas {
    public void draw(String currentVisualValue, float min, float max) {
        double sizeX = this.getWidth(), sizeY = this.getHeight();
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                if (i == 0 || j == 0 || i == sizeY-1 || j == sizeX-1) this.getGraphicsContext2D().getPixelWriter().setColor(j,i, Color.BLACK);
                else if ((i == sizeY-2 || i == sizeY-3) && (j == (int)(sizeX/4) || j == (int)(sizeX/2) || j == (int)(sizeX*3/4))) this.getGraphicsContext2D().getPixelWriter().setColor(j,i, Color.BLACK);
                else this.getGraphicsContext2D().getPixelWriter().setColor(j,i,ColorScale.getColor(min,max,(float)(min+(max-min)/sizeX*j), currentVisualValue));
            }
        }
    }
}
