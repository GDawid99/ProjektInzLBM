package graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lbm.*;
import javafx.scene.canvas.Canvas;
import lbm.boundary.FluidBoundaryType;
import util.Velocity;


public class VisualCanvas extends Canvas {
    //metoda kolorująca płótno
    public void draw(Lattice lattice, String currentVisualValue, float min, float max) {
        Cell cell;
        float value = 0f;
        this.getGraphicsContext2D().clearRect(0,0,this.getWidth(),this.getHeight());
        for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
                cell = lattice.getCells()[y][x];
                if (cell.getFluidBoundaryType() == FluidBoundaryType.WALL) {
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


    public void drawLines(Lattice lattice) {
        Cell cell;
        for (int y = 2; y < this.getHeight(); y+=5) {
            for (int x = 2; x < this.getWidth(); x+=5) {
                cell = lattice.getCells()[y][x];
                if (cell == null) continue;
                this.getGraphicsContext2D().setStroke(Color.BLACK);
                this.getGraphicsContext2D().setFill(Color.BLACK);
                this.getGraphicsContext2D().setLineWidth(0.1d);
                this.getGraphicsContext2D().strokeLine(x,y,x+100*cell.velocity.ux,y-100*cell.velocity.uy);
            }
        }
    }

    public void drawParticleTrajectory(Cell cell, float gravity) {
        float mass = 0.5f;
        this.getGraphicsContext2D().setLineWidth(0.5d);
        Particle oldParticle = new Particle(0,25,mass,cell.velocity);
        Particle newParticle = new Particle(0,25,mass,cell.velocity);
        while (newParticle.getX() >= 0 && newParticle.getX() < 127 && newParticle.getY() >= 0 && newParticle.getY() < 127) {
            newParticle.getVelocity().ux = oldParticle.getMass()* oldParticle.getVelocity().ux + (1 - oldParticle.getMass())*cell.velocity.ux;
            newParticle.getVelocity().uy = oldParticle.getMass()* oldParticle.getVelocity().uy + (1 - oldParticle.getMass())*cell.velocity.uy - gravity*200;
            float x = oldParticle.getX() + 100f * (newParticle.getVelocity().ux + oldParticle.getVelocity().ux)/2;
            float y = oldParticle.getY() - 100f * (newParticle.getVelocity().uy + oldParticle.getVelocity().uy)/2;
            newParticle.setPosition(x,y);
            this.getGraphicsContext2D().setStroke(Color.CYAN);
            this.getGraphicsContext2D().setFill(Color.CYAN);
            this.getGraphicsContext2D().strokeLine(
                    4*oldParticle.getX(),
                    4*oldParticle.getY(),
                    4*newParticle.getX(),
                    4*newParticle.getY()
            );
            oldParticle = new Particle(newParticle);
        }
        //System.out.println(oldParticle.getX() + ", " + oldParticle.getY() + ", " + newParticle.getX() + ", " + newParticle.getY());
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
