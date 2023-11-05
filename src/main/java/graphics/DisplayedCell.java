package graphics;

import lbm.Cell;
import javafx.scene.paint.Color;

//wyswietlana komorka na plotnie wraz z cechami
public class DisplayedCell {
    private Cell displayedCell;
    private Color color;


    public DisplayedCell(Cell cell) {
        this.displayedCell = cell;
        switch (cell.getCellState()) {
            case WALL -> color = Color.rgb(0,0,0);
            case FLUID -> color = Color.rgb(255,0,0);
        }
    }

    public Color getColor() {
        return color;
    }

}
