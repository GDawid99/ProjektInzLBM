package graphics;

import javafx.scene.paint.Color;

public class ColorScale {
    public static Color getColor(float min, float max, float valueVisualization, String visualValue) {
        if (visualValue.equals("Velocity [Vx]") || visualValue.equals("Velocity [Vy]")) return  blueWhiteRedScale(min, max, valueVisualization);
        else if (visualValue.equals("Density")) return blueToRedScale(min, max, valueVisualization);
        else return coloredScale(min, max, valueVisualization);
    }

    private static Color blueToRedScale(double min, double max, double value) {
        if (min > value) return Color.color(0, 0, 1);
        if (max < value) return Color.color(1, 0, 0);
        double colorValue = (value - min)/(max - min);
        return Color.BLUE.interpolate(Color.RED, colorValue);
    }

    private static Color coloredScale(double min, double max, double value) {
        if (min > value) return Color.color(0, 0, 1);
        if (max < value) return Color.color(1, 0, 0);
        double firstColorValue = (value - min)/(max - min), colorValue;
        if (firstColorValue < 0.25) {
            colorValue = firstColorValue/0.25;
            return Color.BLUE.interpolate(Color.CYAN, colorValue);
        }
        else if (firstColorValue >= 0.25 && firstColorValue < 0.5) {
            colorValue = (firstColorValue - 0.25)/0.25;
            return Color.CYAN.interpolate(Color.GREEN, colorValue);
        }
        else if (firstColorValue >= 0.5 && firstColorValue < 0.75) {
            colorValue = (firstColorValue - 0.5)/0.25;
            return Color.GREEN.interpolate(Color.YELLOW, colorValue);
        }
        else {
            colorValue = (firstColorValue - 0.75)/0.25;
            return Color.YELLOW.interpolate(Color.RED, colorValue);
        }
    }

    private static Color blueWhiteRedScale(double min, double max, double value) {
        if (min > value) return Color.color(0, 0, 1);
        if (max < value) return Color.color(1, 0, 0);
        double red, green, blue;
        if (value < 0) {
            red = 1 - value/min;
            green = 1 - value/min;
            blue = 1;
        }
        else if (value > 0) {
            red = 1;
            green = 1 - value/max;
            blue = 1 - value/max;
        }
        else {
            red = 1;
            green = 1;
            blue = 1;
        }
        return Color.color(red, green, blue);
    }
}
