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

import java.awt.*;
import java.util.ArrayList;
/**
 * An object in the game.
 * @author Alex Rodrigues
 */
public class GameObject {

    //The position of the object in the world.
    double xPosition;
    double yPosition;
    
    //The velocity of the object.
    double xVelocity;
    double yVelocity;
    
    //These define a constant bias in the game objects velocity.
    double prefXdir, prefYdir, prefSpeed;
    
    //The facing angle of the object.
    double angle;
    
    //The shape of the object represented as a polygon.
    Polygon shape, drawShape;
    
    //The rate at which the object rotates.
    double rotationStep;
    
    //This determines how fast an object accelerates or moves forward.
    double speedFactor;
    
    //True iff the object is active/alive.
    private boolean active;
    
    int counter;
    Color color = Color.GREEN;
    
    //The number of times an object can be hit and remain active.
    double hp = 1;
    
    //If this is true the rendered polygon for this object will be filled.
    //Otherwise the poylgon will be drawn as a wireframe.
    boolean fillShape = false;
    
    //This determines the number of pixels this object is represented by
    //on the minimap.
    public int minimapSize = 4;
    
    double mass, gInfluence = 1;
    
    /**
     * Paint the game object to the world.
     *
     * @param g The graphics for the world.
     */
    public void paint(Graphics g, int xOffset, int yOffset) {

        if (!this.active) {
            return;
        }
        int x, y;
        Polygon translatedDrawShape = Game.copyPolygon(drawShape);
        for (int i = 0; i < shape.npoints; i++) {
            x = (int) Math.round(shape.xpoints[i] * Math.cos(angle) 
                    - shape.ypoints[i] * Math.sin(angle));
            y = (int) Math.round(shape.xpoints[i] * Math.sin(angle) 
                    + shape.ypoints[i] * Math.cos(angle));
            translatedDrawShape.xpoints[i] = x;
            translatedDrawShape.ypoints[i] = y;
        }

        translatedDrawShape.invalidate();
        translatedDrawShape.translate((int) Math.round(xPosition - xOffset), (int) Math.round(yPosition - yOffset));

        g.setColor(color);
        if (fillShape) {
            g.fillPolygon(translatedDrawShape);
        } else {
            g.drawPolygon(translatedDrawShape);
        }
    }

    /**
     * Return true iff the game object is still active.
     *
     * @return
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Sets the game object as active.
     */
    public void setActive() {
        this.active = true;
    }

    /**
     * Kills the game object by setting it to inactive.
     */
    public void kill() {
        this.active = false;
    }

    /**
     * Hit the game object.
     *
     * @param debris The list of explosions in the world.
     */
    public void hit(ArrayList<Debris> debris) {
        hit(debris, false);
    }

    /**
     * Hit the game object.
     *
     * @param debris The list of debris in the world.
     * @param createDebris True if the hit causes an explosion of debris.
     */
    public void hit(ArrayList<Debris> debris, boolean createDebris) {
        if (active) {
            hp--;
            if (hp <= 0) {
                active = false;
                counter = 0;
            }
        }
        if (createDebris) {
            explode(debris);
        }
    }
    /*
     * Create an explosion of debris around the game object.
     */

    public void explode(ArrayList<Debris> debris) {
        double rnd = Math.random() * 12 + 5;
        for (int k = 0; k < rnd; k++) {
            debris.add(new Debris(this.xPosition, this.yPosition));
        }
    }

    /**
     * Return true iff both game objects are colliding.
     *
     * @param object2 The other game object.
     * @return A boolean that is true iff the game objects are colliding.
     */
    public boolean isColliding(GameObject object2) {

        if (!this.active || !object2.isActive()) {
            return false;
        }

        int x, y;
        for (int i = 0; i < object2.drawShape.npoints; i++) {
            x = object2.drawShape.xpoints[i];
            y = object2.drawShape.ypoints[i];

            if (this.drawShape.contains(x, y)) {
                return true;
            }
        }

        for (int i = 0; i < this.drawShape.npoints; i++) {
            x = this.drawShape.xpoints[i];
            y = this.drawShape.ypoints[i];

            if (object2.drawShape.contains(x, y)) {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Applies an ad hoc method of Newtons law of attraction to
     * both the objects.
     * @param obj The other object.
     */
    public void applyGravitationalAttraction(GameObject obj){
        
        Pair center1 = obj.getCenter();
        Pair center2 = this.getCenter();
        double x = (double)center1.getFirst() - (double)center2.getFirst();
        double y = (double)center1.getSecond() - (double)center2.getSecond();
        double gConstant = 0.0000000028 * Math.min(0.2 * Game.level, 1);
        double r = Math.sqrt(x*x + y*y);
        
        if (r > 100){
            r = Math.max(r, 2500);
            double gForce = gConstant * ((obj.mass * this.mass)/(1 + r ));  
            x/= r;
            y/= r;
            this.xVelocity += x * gForce/this.mass * this.gInfluence;
            this.yVelocity += y * gForce/this.mass * this.gInfluence;
            obj.xVelocity -= x * gForce/obj.mass * obj.gInfluence;
            obj.yVelocity -= y * gForce/obj.mass * obj.gInfluence;
        }
    }
    
    /**
     * Gets the best known center point for this game object.
     * @return The center point of the game object.
     */
    public Pair getCenter(){
        return new Pair(this.xPosition, this.yPosition);
    }

    /**
     * Update the position of the game object.
     */
    public void update() {
        
        
        counter++;

        xPosition += xVelocity + prefXdir * prefSpeed ;
        yPosition += yVelocity + prefYdir * prefSpeed;

        checkBounds();

        int x, y;

        for (int i = 0; i < shape.npoints; i++) {
            x = (int) Math.round(shape.xpoints[i] * Math.cos(angle) - shape.ypoints[i] * Math.sin(angle));
            y = (int) Math.round(shape.xpoints[i] * Math.sin(angle) + shape.ypoints[i] * Math.cos(angle));
            drawShape.xpoints[i] = x;
            drawShape.ypoints[i] = y;
        }

        drawShape.invalidate();
        drawShape.translate((int) Math.round(xPosition), (int) Math.round(yPosition));
    }

    /**
     * Create the shape of this game object.
     */
    public void createShape() {
        //by default has no shape
    }

    /**
     * Checks if the game object has crossed any boundary points.
     */
    public void checkBounds() {
        if (xPosition > Game.SPACE_WIDTH) {
            xPosition = 0;
        }

        if (xPosition < 0) {
            xPosition = Game.SPACE_WIDTH;
        }

        if (yPosition > Game.SPACE_HEIGHT) {
            yPosition = 0;
        }

        if (yPosition < 0) {
            yPosition = Game.SPACE_HEIGHT;
        }
    }
}
