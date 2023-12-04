package lbm;

import util.Velocity;

public class ParticleTrajectory {
    private Particle oldParticle;
    private Particle newParticle;
    private int originX;
    private int originY;
    public ParticleTrajectory(Particle oldParticle) {
        this.oldParticle = oldParticle;
        this.newParticle = new Particle(oldParticle);
        this.originX = (int)oldParticle.getX();
        this.originY = (int)oldParticle.getY();
    }

    public void calcParticleTrajectory(Velocity fluidVelocity, float gravity) {
        newParticle = oldParticle;
        newParticle.getVelocity().ux = oldParticle.getMass()* oldParticle.getVelocity().ux + (1 - oldParticle.getMass())*fluidVelocity.ux;
        newParticle.getVelocity().uy = oldParticle.getMass()* oldParticle.getVelocity().uy + (1 - oldParticle.getMass())*fluidVelocity.uy - gravity;
        float x = oldParticle.getX() + (newParticle.getVelocity().ux + oldParticle.getVelocity().ux)/2;
        float y = oldParticle.getY() + (newParticle.getVelocity().uy + oldParticle.getVelocity().uy)/2;
        newParticle.setPosition(x,y);
    }

    public Particle getOldParticle() {
        return oldParticle;
    }

    public Particle getNewParticle() {
        return newParticle;
    }

    public int getOriginX() {
        return originX;
    }

    public int getOriginY() {
        return originY;
    }
}
