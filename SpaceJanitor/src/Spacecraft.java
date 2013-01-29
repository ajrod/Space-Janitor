/**
 *Copyright (C) 2013 Alex Rodrigues
 *
 *Permission is hereby granted, free of charge, to any person obtaining a copy of this 
 *software and associated documentation files (the "Software"), to deal in the Software without 
 *restriction, including without limitation the rights to use, copy, modify, merge, publish, 
 *distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom 
 *the Software is furnished to do so, subject to the following conditions:
 *
 *The above copyright notice and this permission notice shall be included in all copies or 
 *substantial portions of the Software.
 *
 *THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 *INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 *PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR 
 *ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
 *ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 *SOFTWARE.
 * 
 * Author: Alex Rodrigues
 */

import java.awt.Polygon;
import java.util.ArrayList;

/**
 * The players controllable ship.
 *
 * @author Alex Rodrigues
 */
public class Spacecraft extends GameObject {

    /**
     * The number of lives the ship has remaining.
     */
    int lives;
    /**
     * The time elapsed until a respawn occurs.
     */
    int respawnDelay = 50;
    int weaponDelay = 5;
    /**
     * The factor at which speed slows over time.
     */
    private final double speedDecay = 0.992;

    public Spacecraft() {
        createShape();
        this.gInfluence = 10 * Math.min(0.1 * Game.level, 1);
        this.mass = 924739;
        xPosition = Game.SPACE_WIDTH / 2;
        yPosition = Game.SPACE_HEIGHT / 2;
        rotationStep = 0.15;
        speedFactor = 0.4;
        this.setActive();
        angle = -Math.PI / 2;
        this.minimapSize = 4;
        lives = 3;
    }

    @Override
    /**
     * Update the ship.
     */
    public void update() {
        if (!this.isActive()){
            this.xVelocity = 0;
            this.yVelocity = 0;
        }
        this.xVelocity *= speedDecay;
        this.yVelocity *= speedDecay;       
        super.update();
    }

    @Override
    /**
     * Creates and defines the polygon that represents the ship.
     */
    public void createShape() {
        shape = new Polygon();
        shape.addPoint(15, 0);
        shape.addPoint(-10, 10);
        shape.addPoint(-10, -10);
        drawShape = Game.copyPolygon(shape);
    }

    /**
     * Notify that the ship has been hit.
     */
    public void hit(ArrayList<Debris> debris) {
        boolean oldActive = this.isActive();
        super.hit(debris, true);
        //Checks if the ships active has changed.
        //This will only happen if the ship died
        if (oldActive != this.isActive()) {
            lives--;
        }
    }

    /**
     * Accelerates the ship in the forward direction.
     */
    public void accelerate() {
        xVelocity += Math.cos(angle) * speedFactor;
        yVelocity += Math.sin(angle) * speedFactor;
    }

    /**
     * Rotates the ship left;
     */
    public void rotateLeft() {
        angle -= rotationStep;
    }

    /**
     * Rotates the ship right.
     */
    public void rotateRight() {
        angle += rotationStep;
    }

    /**
     * Reset the ships properties back to default.
     */
    public void reset() {
        xVelocity = 0;
        yVelocity = 0;
        xPosition = Game.SPACE_WIDTH / 2;
        yPosition = Game.SPACE_HEIGHT / 2;
        angle = -Math.PI / 2;
        this.setActive();
    }

    /**
     * Checks if the ship needs to be respawned and handles if it does.
     *
     * @param spaceJunk The list of space junk.
     * @param planets The list of planets.
     */
    public void checkRespawnShip(ArrayList<SpaceJunk> spaceJunk,
            ArrayList<Planet> planets) {
        if (!this.isActive() && this.counter > respawnDelay
                && isRespawnSafe(spaceJunk, planets)
                && this.lives > 0) {
            this.reset();
        }
    }

    /*
     * Adds lives to the ship.
     */
    public void boostLives(int num){
        this.lives += num;
    }
    /*
     * Return true iff the ship is safe to respawn. This checks
     * if there is junk, or other threats in the spawn area.
     */
    public boolean isRespawnSafe(ArrayList<SpaceJunk> spaceJunk, ArrayList<Planet> planets) {
        double x, y, h;
        boolean ret = true;
        for (int i = 0; i < spaceJunk.size(); i++) {
            SpaceJunk sj = spaceJunk.get(i);
            x = sj.xPosition - Game.SPACE_WIDTH / 2;
            y = sj.yPosition - Game.SPACE_HEIGHT / 2;
            h = Math.sqrt(x * x + y * y);

            if (h < 600) {
                if (h > 0) {
                    double normX = x / h;
                    double normY = y / h;
                    sj.xVelocity += normX * 3;
                    sj.yVelocity += normX * 3;
                }
                ret = false;
            }
        }

        for (int i = 0; i < planets.size(); i++) {
            Planet p = planets.get(i);
            x = p.xPosition - Game.SPACE_WIDTH / 2;
            y = p.yPosition - Game.SPACE_HEIGHT / 2;
            h = Math.sqrt(x * x + y * y);

            if (h < 700 + p.diameter/2) {
                if (h > 0) {
                    double normX = x / h;
                    double normY = y / h;
                    p.xVelocity += normX * 3;
                    p.yVelocity += normX * 3;
                }
                ret = false;
            }
        }

        return ret;
    }

    /**
     * Fires the ships weapon.
     *
     * @param bulletList The list of bullets in the world.
     */
    public void fireWeapon(ArrayList<Bullet> bulletList) {
        if (this.counter > weaponDelay && this.isActive()) {
            bulletList.add(new Bullet(this.drawShape.xpoints[0],
                    this.drawShape.ypoints[0],
                    this.angle, this.xVelocity, this.yVelocity));
            this.counter = 0;

        }
    }
}
