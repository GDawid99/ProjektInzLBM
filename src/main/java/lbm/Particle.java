package lbm;

import util.Velocity;

public class Particle {
    private float x;
    private float y;
    private float mass;
    private Velocity velocity;

    public Particle(float x, float y, float mass, Velocity velocity) {
        this.x = x;
        this.y = y;
        this.mass = mass;
        this.velocity = velocity;
    }

    public Particle(Particle particle) {
        this.x = particle.x;
        this.y = particle.y;
        this.mass = particle.mass;
        this.velocity = new Velocity(particle.velocity);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Velocity getVelocity() {
        return this.velocity;
    }

    public float getMass() {
        return this.mass;
    }
}
