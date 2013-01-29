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

import java.awt.Color;
import java.awt.Graphics;

/**
 * A planet in space.
 * @author Alexande Rodrigues
 */
public class Planet extends GameObject {

    /**
     * The diameter of the planet.
     */
    public int diameter;

    public Planet() {

        this.diameter = 50 + (int) (400 * (Math.pow(5, Math.random()) - 1));
        this.diameter *= levelScale();
        init();
    }

    public void init() {
        //randomize color
        this.color = new Color(85 + (int) (Math.random() * 150),
                (int) Math.random() * 150,
                (int) (Math.random() * 150),
                150 + (int) (Math.random() * 100));

        //mass is a function of diameter with some noise
        this.mass = 2000 * diameter * (Math.pow(1.02, diameter
                / (3.50 + Math.random())) + 10000);

        //hp is a function of mass with some noise
        this.hp = this.mass / Math.pow(10, 8);
        this.hp *= (0.85 + Math.random());
        this.hp = Math.max(hp, 2000);

        this.gInfluence = 0.5 + 0.5 * Math.random();
        this.gInfluence *= levelScale();
        this.fillShape = true;
        this.minimapSize = 2 + diameter / 200;
        createShape();

        //randomize intitial velcoity
        double h = (Math.random() + 0.5) * 4 / diameter;
        double a = Math.random() * 2 * Math.PI;
        xVelocity = Math.cos(a) * h;
        yVelocity = Math.sin(a) * h;

        //randomize initial position with a mininum and maximum distance
        //from the center of the world
        h = Math.random() * ((Game.SPACE_WIDTH / 3 + Game.SPACE_HEIGHT / 3) / 2)
                + Game.SPACE_WIDTH * 0.1;
        a = Math.random() * 2 * Math.PI;
        xPosition = Math.cos(a) * h + Game.SPACE_WIDTH / 2;
        yPosition = Math.sin(a) * h + Game.SPACE_HEIGHT / 2;

        
        rotationStep = (Math.random() / 2 - 0.25) / diameter;

        //create a constant bias in the velocity of the planet
        this.prefSpeed = Math.random() * 20 + 8;
        this.prefSpeed *= levelScale()*1.20;
        a = Math.random() * 2 * Math.PI;
        this.prefXdir = Math.cos(a);
        this.prefYdir = Math.sin(a);
        
        //finally set this planet to be active
        this.setActive();
    }

    /**
     * Used to scale some attributes of the planet below level 5.
     * @return The level scale factor.
     */
    private double levelScale(){
         return Math.min(1, 0.2 * Game.level);
    }
    /**
     * Paint the game object to the world.
     *
     * @param g The graphics for the world.
     */
    public void paint(Graphics g, int xOffset, int yOffset) {

        if (!this.isActive()) {
            return;
        }
        g.setColor(color);
        int x = (int) (this.xPosition - diameter / 2) - xOffset;
        int y = (int) (this.yPosition - diameter / 2) - yOffset;
        if (fillShape) {
            g.fillOval(x, y, diameter, diameter);
        } else {
            g.drawOval(x, y, diameter, diameter);
        }
    }

    @Override
    public boolean isColliding(GameObject object2) {

        if (!this.isActive() || !object2.isActive()) {
            return false;
        }
        int x1 = (int) (this.xPosition);
        int y1 = (int) (this.yPosition);


        int x2, y2;
        for (int i = 0; i < object2.drawShape.npoints; i++) {
            x2 = object2.drawShape.xpoints[i];
            y2 = object2.drawShape.ypoints[i];

            double xDiff = Math.abs(x2 - x1);
            double yDiff = Math.abs(y2 - y1);
            double m = Math.max(xDiff, yDiff);

            double d = m * Math.sqrt(Math.pow(xDiff / m, 2)
                    + Math.pow(yDiff / m, 2));
            if (d < 0.98 * diameter / 2) {
                return true;
            }
        }
        return false;
    }

    /**
     * Update the position of the game object.
     */
    public void update() {
        counter++;
        this.xVelocity *= 0.998;
        this.yVelocity *= 0.998;
        xPosition += xVelocity + prefXdir * prefSpeed;
        yPosition += yVelocity + prefYdir * prefSpeed;

        checkBounds();

    }
}
