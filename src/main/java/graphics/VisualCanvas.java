package graphics;

import lbm.Cell;
import lbm.CellState;
import javafx.scene.canvas.Canvas;
import lbm.Lattice;
import lbm.model.InitValues;

public class VisualCanvas extends Canvas {
    //metoda kolorująca płótno
    public void draw(Lattice lattice, String visualValue) {
        Cell cell;
        this.getGraphicsContext2D().clearRect(0,0,this.getWidth(),this.getHeight());
        for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
                cell = lattice.getCells()[y][x];
                if (visualValue.equals("VelocityX")) this.getGraphicsContext2D().getPixelWriter().setColor(x,y,cell.getColor(0.0f,InitValues.UX,cell.velocity.ux));
                if (visualValue.equals("VelocityY")) this.getGraphicsContext2D().getPixelWriter().setColor(x,y,cell.getColor(-0.003f, 0.004f ,cell.velocity.uy));
                if (visualValue.equals("Density")) this.getGraphicsContext2D().getPixelWriter().setColor(x,y,cell.getColor(0.9f,1.1f,cell.density));
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
