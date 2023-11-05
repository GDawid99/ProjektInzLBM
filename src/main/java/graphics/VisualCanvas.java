package graphics;

import lbm.Cell;
import lbm.CellState;
import javafx.scene.canvas.Canvas;
import lbm.Lattice;

public class VisualCanvas extends Canvas {
    //metoda kolorująca płótno
    public void draw(Lattice lattice) {
        this.getGraphicsContext2D().clearRect(0,0,this.getWidth(),this.getHeight());
        for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
                this.getGraphicsContext2D().getPixelWriter().setColor(x,y,lattice.getCells()[y][x].getColor());
            }
        }
    }

    public void init() {
        resize(this.getWidth()+2,this.getHeight()+2);   //powiększamy domyślny rozmiar płótna o 2 dla ścian
    }

    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
    }


}
