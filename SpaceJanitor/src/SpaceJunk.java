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
 * A piece of junk in space.
 * @author Alex Rodrigues
 */
public class SpaceJunk extends GameObject {

    /**
     * Controls how large the space junk is.
     */
    int scale;

    public SpaceJunk() {
        scale = 3;
        init();
    }

    /**
     * Splits the junk into smaller pieces if it is large 
     * enough to be split.
     *
     * @param junk The list of junk in the world.
     */
    public void splitJunk(ArrayList<SpaceJunk> junk) {
        if (scale > 1) {

            junk.add(new SpaceJunk(xPosition + 5,
                    yPosition - 5,
                    scale - 1));
            junk.add(new SpaceJunk(xPosition - 5, yPosition + 5,
                    scale - 1));
        }
    }

    public SpaceJunk(double x, double y, int s) {
        scale = s;
        init();
        xPosition = x;
        yPosition = y;
    }

    /*
     * Initializes all the properties of the space junk.
     */
    public void init() {
        this.minimapSize = 1 + scale;
        
        //mass is a function of scale
        double massFactor = Math.pow(10, scale);
        this.mass = 10000 * massFactor - 1000 * 
                massFactor *(0.5 - Math.random())*scale;
        
        //hp is a function of mass
        //this.hp = this.mass / 10000;
        this.hp = scale; //use scale until weapons are implemented
        createShape();
        
        //create a random initial velocity
        double h, a;
        h = (Math.random()*5 + 5.0) / scale;
        a = Math.random() * 2 * Math.PI;
        xVelocity = Math.cos(a) * h;
        yVelocity = Math.sin(a) * h;
        
        //create an initial position with a minimum and maximum distance
        //from the center of the world
        h = Math.random() * ((Game.SPACE_WIDTH/3 + Game.SPACE_HEIGHT/3)/2) + 100;
        a = Math.random() * 2 * Math.PI;
        xPosition = Math.cos(a) * h + Game.SPACE_WIDTH/2;
        yPosition = Math.sin(a) * h + Game.SPACE_HEIGHT/2;

        rotationStep = (Math.random() / 2 - 0.25) / scale;
              
        //create a random constant bias in the velocity
        this.prefSpeed = Math.random() * 8 + 3;
        this.prefSpeed *= Math.min(1, 0.2 * Game.level);
        a = Math.random() * 2 * Math.PI;
        this.prefXdir = Math.cos(a);
        this.prefYdir = Math.sin(a);
        
        this.setActive();
    }

    @Override
    public void createShape() {
        shape = new Polygon();
        shape.addPoint(15 * scale, 6 * scale);
        shape.addPoint(7 * scale, 17 * scale);
        shape.addPoint(-13 * scale, 8 * scale);
        shape.addPoint(-11 * scale, -10 * scale);
        shape.addPoint(12 * scale, -16 * scale);
        drawShape = Game.copyPolygon(shape);
    }

    public void hit(ArrayList<Debris> debris) {
        super.hit(debris, true);
    }

    public void update() {
        angle += rotationStep;
        super.update();
        this.xVelocity *= (0.9999 - 0.03*Math.random());
        this.yVelocity *= (0.9999 - 0.03*Math.random());
    }
}
