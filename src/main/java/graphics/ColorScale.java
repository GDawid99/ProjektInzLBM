package graphics;

import controller.MainSceneController;
import javafx.scene.paint.Color;

public class ColorScale {
    public static Color getColor(float min, float max, float valueVisualization, String visualValue) {
        if (visualValue.equals("VelocityX") || visualValue.equals("VelocityY")) return  blueWhiteRedScale(valueVisualization, max);
        else return blueToRedScale(min, max, valueVisualization);
    }

    private static Color blueToRedScale(double min, double max, double value) {
        if (min > value) return Color.color(0, 0, 0.75);
        if (max < value) return Color.color(0.75, 0, 0);
        double firstColorValue = (value - min)/(max - min), colorValue;
        if (firstColorValue < 1/6d) {
            colorValue = (0.25*firstColorValue)/(1/6d) + 0.75;
            return Color.color(0, 0, colorValue);
        }
        else if (firstColorValue >= 1/6d && firstColorValue < 5/6d) {
            colorValue = (firstColorValue - 1/6d)/(5/6d - 1/6d);
            return Color.color(colorValue, 0, 1- colorValue);
        }
        else {
            colorValue =(0.25*(1 - firstColorValue))/(1/6d) + 0.75;
            return Color.color(colorValue, 0, 0);
        }
    }

    private static Color blueWhiteRedScale(double value, double max) {
        if (value > 0) {
            double color = 1 - value / max;
            if (color > 1) color = 1.0;
            if (color < 0) color = 0.0;
            return Color.color(1, color, color);
        }
        else {
            double color = 1 - Math.abs(value) / max;
            if (color > 1) color = 1.0;
            if (color < 0) color = 0.0;
            return Color.color(color, color, 1);
        }
    }
}
