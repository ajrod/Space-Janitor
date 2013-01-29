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

/**
 * A bullet fired from the ship.
 * @author Alex Rodrigues
 */
public class Bullet extends GameObject {

    //the amount of time the bullet will remain active when a collision
    //has not occured.
    int bulletTimeSpan = 250;

    public Bullet(double startX, double startY, double a, double initXSpeed, 
            double initYSpeed) {

        createShape();
        this.mass = 10000;
        this.gInfluence = 20 * Math.min(0.2 * Game.level, 1);
        xPosition = startX;
        yPosition = startY;
        angle = a;
        speedFactor = 20;
        this.minimapSize = 1;
        xVelocity = Math.cos(angle) * speedFactor + initXSpeed;
        yVelocity = Math.sin(angle) * speedFactor + initYSpeed;

        this.setActive();
    }

    @Override
    public void createShape() {
        shape = new Polygon();
        shape.addPoint(0, 0);
        shape.addPoint(0, 0);
        shape.addPoint(0, 0);
        shape.addPoint(0, 0);

        drawShape = Game.copyPolygon(shape);
    }

    public boolean remove() {
        return this.counter >= bulletTimeSpan || !this.isActive();
    }
}
