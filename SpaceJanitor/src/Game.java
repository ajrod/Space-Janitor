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
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Timer;

public class Game extends Applet implements KeyListener, ActionListener {

    /*
     * The offscreen buffer for double buffering.
     */
    Image offscreen;
    /*
     * The off screen graphics for double buffering.
     */
    Graphics offg;
    /*
     * The players ship.
     */
    Spacecraft ship;
    /*
     * The list of all space junk in the game.
     */
    ArrayList<SpaceJunk> spaceJunk;
    /*
     * The list of all bullets in the game.
     */
    ArrayList<Bullet> bullets;
    /*
     * The list of all debris in the game.
     */
    ArrayList<Debris> debris;
    /*
     * The list of all planets in the game.
     */
    ArrayList<Planet> planets;
    /*
     * The game timer. This controls how often the game is updated.
     */
    Timer gameTimer;
    private final int UPDATE_INTERVAL = 20;
    //true if the key has been pressed false otherwise
    boolean upKey, leftKey, rightKey, spaceKey;
    /*
     * The players score.
     */
    int score;
    //sounds (not yet implemented)
    //AudioClip laser, thruster, shipHit, junkHit;

    /*
     * The games resolution width.
     */
    public static final int RESOLUTION_WIDTH = 1280;
    /*
     * The games resolution height.
     */
    public static final int RESOLUTION_HEIGHT = 720;
    /*
     * The total width of the game. This defines the boundary points
     * for the game objects.
     */
    public static int SPACE_WIDTH;
    /*
     * The total height of the game. This defines the boundary points
     * for the game objects.
     */
    public static int SPACE_HEIGHT;
    /**
     * These are offsets that allow the camera to move around the world in
     * relation to the ship.
     */
    private int cameraOffsetX, cameraOffsetY;
    /**
     * The size of the mini map in pixels.
     */
    private final int MINIMAP_SIZE = 150;
    /*
     * The x location of the minimap on the screen.
     */
    private final int MINIMAP_X = RESOLUTION_WIDTH - MINIMAP_SIZE - 3;
    /*
     * The y location of the minimap on the screen.
     */
    private final int MINIMAP_Y = RESOLUTION_HEIGHT - MINIMAP_SIZE - 3;
    /**
     * A dictionary of background star locations and their color.
     */
    public Map<Pair, Color> bgStarMap = new HashMap<>();
    public final int bgStarCount = 200; //number of bg stars
    /**
     * The current density of asteroids and planets.
     */
    public double density;
    /**
     * The level the player is on.
     */
    public static int level = 0;
    /**
     * The total amount of massed needed to be destroyed for the player to reach
     * then next level.
     */
    public int massToNextLevel;

    /**
     * Initialization method that will be called after the applet is loaded into
     * the browser.
     */
    @Override
    public void init() {
        Game.level = 1;
        SPACE_WIDTH = 25000;
        SPACE_HEIGHT = 25000;
        this.setSize(RESOLUTION_WIDTH, RESOLUTION_HEIGHT);
        this.addKeyListener(this);
        ship = new Spacecraft();
        spaceJunk = new ArrayList();
        bullets = new ArrayList();
        debris = new ArrayList();
        planets = new ArrayList();
        gameTimer = new Timer(UPDATE_INTERVAL, this);
        offscreen = createImage(this.getWidth(), this.getHeight());
        offg = offscreen.getGraphics();


        loadNextLevel();
        loadBackground();
//        laser = getAudioClip(getCodeBase(), "laser80.wav");
//        thruster = getAudioClip(getCodeBase(), "thruster.wav");
//        shipHit = getAudioClip(getCodeBase(), "explode1.wav");
//        junkHit = getAudioClip(getCodeBase(), "explode0.wav");
    }

    private void loadBackground() {

        for (int i = 0; i < bgStarCount; i++) {
            addBGStar(0, Game.RESOLUTION_WIDTH);
        }
    }

    /**
     * Add a back ground star to the game. Background stars are generated in a
     * radius around the player.
     *
     * @param minDistance The lower bound on the distance from the player.
     * @param maxDistance The upper bound on the distance from the player.
     */
    private void addBGStar(double minDistance, double maxDistance) {
        double x, y;
        double a = Math.random() * 2 * Math.PI;
        double interval = maxDistance - minDistance;
        x = Math.cos(a) * interval * Math.random() + ship.xPosition;
        y = Math.sin(a) * interval * Math.random() + ship.yPosition;

        double xDir = x - ship.xPosition;
        double yDir = y - ship.yPosition;
        double norm = Math.sqrt(xDir * xDir + yDir * yDir);
        if (norm != 0) {
            xDir /= norm;
            yDir /= norm;
            x += xDir * minDistance;
            y += yDir * minDistance;
        }
        Pair newStar = new Pair<>((int) (x), (int) (y));
        if (!bgStarMap.containsKey(newStar)) {
            //these magic numbers are just creating a random color
            //with some constraints
            bgStarMap.put(newStar, new Color(150
                    + (int) (105 * Math.random()), 150
                    + (int) (105 * Math.random()), (int) (100 * Math.random()),
                    (int) (100 + 155 * Math.random())));
        } else {
            //try to add another star since it failed
            addBGStar(minDistance, maxDistance);
        }
    }

    /**
     * Sets up the next level.
     */
    private void loadNextLevel() {
        spaceJunk.clear();
        planets.clear();
        bullets.clear();
        ship.reset();
        this.level += 1;
        ship.boostLives(1);
        SPACE_WIDTH *= 1.3;
        SPACE_HEIGHT *= 1.3;
        for (int i = 0; i < 250 * level; i++) {
            spaceJunk.add(new SpaceJunk());
        }

        for (int i = 0; i < 25 * level; i++) {
            planets.add(new Planet());
        }
        this.massToNextLevel = (int)( 100000000 * level * 2 *  Math.pow(1.25, level - 1));

    }

    /**
     * Draw the background stars to the game. This also prunes and replaces
     * background stars that have gone out of range; background stars are
     * created dynamically.
     *
     * @param g The screens graphics.
     */
    public void drawBackgroundStars(Graphics g) {

        int numStarsToFar = 0;
        Object[] bgStars = bgStarMap.keySet().toArray();
        //count the number of bg stars out of range
        for (int i = 0; i < bgStars.length; i++) {
            Pair key = (Pair) bgStars[i];
            int x = (int) key.getFirst();
            int y = (int) key.getSecond();
            if (distanceToShip(x, y) > Game.RESOLUTION_HEIGHT + 200) {
                numStarsToFar++;
            }
        }

        //player probably teleported from one side to the other
        //since most of the previous background stars are out of range
        if (numStarsToFar > bgStarCount / 2) {
            bgStarMap.clear();
            loadBackground(); //load a fresh batch of bg stars around the ship
            bgStars = bgStarMap.keySet().toArray();
        }

        //draw all the bg stars in range
        for (int i = 0; i < bgStars.length; i++) {
            Pair key = (Pair) bgStars[i];
            int x = (int) key.getFirst();
            int y = (int) key.getSecond();
            if (distanceToShip(x, y) > Game.RESOLUTION_HEIGHT + 200) {
                //remove and replace the bg star since it is out of range
                bgStarMap.remove(key);
                addBGStar(Game.RESOLUTION_HEIGHT, Game.RESOLUTION_HEIGHT + 200);
            } else {
                Color starColor = bgStarMap.get(key);
                g.setColor(starColor);
                //use stars blue color intensity to determine the size
                int size = starColor.getBlue() < 50 ? 2 : 1;
                g.fillRect(x - cameraOffsetX, y - cameraOffsetY, size, size);
            }
        }
    }

    /**
     * Return the distance from a point to the players ship.
     *
     * @param x The x coordinate of the point.
     * @param y The y coordinate of the point.
     * @return The distance from point to ship.
     */
    public double distanceToShip(int x, int y) {
        double a = ship.xPosition - x;
        double b = ship.yPosition - y;
        return Math.sqrt(a * a + b * b);
    }

    @Override
    public final void start() {
        gameTimer.start();
    }

    @Override
    public final void stop() {
        gameTimer.stop();
    }

    /**
     * Main update engine for the game.
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        ship.checkRespawnShip(spaceJunk, planets);
        handleInput();
        ship.update();
        applyGravity();
        removeDestroyedJunk();
        updateLists();
        checkCollisions();
    }

    /**
     * Apply gravitational attraction to all combinations of game objects in
     * space.
     */
    private void applyGravity() {

        for (int i = 0; i < planets.size(); i++) {
            for (int j = i + 1; j < planets.size(); j++) {
                planets.get(i).applyGravitationalAttraction(planets.get(j));
            }
            planets.get(i).applyGravitationalAttraction(ship);

            for (int a = 0; a < spaceJunk.size(); a++) {
                planets.get(i).applyGravitationalAttraction(spaceJunk.get(a));
            }

            for (int b = 0; b < bullets.size(); b++) {
                planets.get(i).applyGravitationalAttraction(bullets.get(b));
            }
        }

        for (int a = 0; a < spaceJunk.size(); a++) {
            ship.applyGravitationalAttraction(spaceJunk.get(a));
            for (int b = 0; b < bullets.size(); b++) {
                spaceJunk.get(a).applyGravitationalAttraction(bullets.get(b));
            }
        }
    }

    /**
     * Update all the collection of objects in the world.
     */
    private void updateLists() {
        for (int i = 0; i < spaceJunk.size(); i++) {
            spaceJunk.get(i).update();
        }
        for (int i = 0; i < planets.size(); i++) {
            planets.get(i).update();
        }
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).update();
            if (bullets.get(i).remove()) {
                bullets.remove(i);
            }
        }

        for (int i = 0; i < debris.size(); i++) {
            debris.get(i).update();
            if (debris.get(i).remove()) {
                debris.remove(i);
            }
        }


    }

    /**
     * Checks if any junk has been destroyed and removes them from the list.
     */
    public final void removeDestroyedJunk() {
        for (int i = 0; i < spaceJunk.size(); i++) {
            if (!spaceJunk.get(i).isActive()) {
                massToNextLevel -= spaceJunk.get(i).mass;
                spaceJunk.get(i).splitJunk(spaceJunk);
                spaceJunk.remove(i);
            }
        }

        if (massToNextLevel <= 0) {
            loadNextLevel();
        }
    }

    /**
     * Checks if any of the objects in the world are colliding.
     */
    public void checkCollisions() {
        for (int i = 0; i < spaceJunk.size(); i++) {
            if (ship.isColliding(spaceJunk.get(i))) {
                ship.hit(debris);
            }

            for (int j = 0; j < bullets.size(); j++) {
                if (bullets.get(j).isColliding(spaceJunk.get(i))) {
                    bullets.get(j).hit(debris);
                    spaceJunk.get(i).hit(debris);
                }
            }
        }

        for (int p = 0; p < planets.size(); p++) {
            if (planets.get(p).isColliding(ship)) {
                ship.hit(debris);
            }
            for (int j = 0; j < bullets.size(); j++) {
                if (planets.get(p).isColliding(bullets.get(j))) {
                    bullets.get(j).hit(debris);
                    planets.get(p).hit(debris);
                }
            }
        }
    }

    /**
     * Renders the game to the screen.
     */
    public void paint(Graphics g) {

        this.cameraOffsetX = (int) (ship.xPosition - Game.RESOLUTION_WIDTH / 2);
        this.cameraOffsetY = (int) (ship.yPosition - Game.RESOLUTION_HEIGHT / 2);
        offg.setColor(Color.BLACK);
        offg.fillRect(0, 0, RESOLUTION_WIDTH, RESOLUTION_HEIGHT);
        this.drawBackgroundStars(offg);
        ship.paint(offg, cameraOffsetX, cameraOffsetY);
        paintLists();
        offg.setFont(offg.getFont().deriveFont(14f));
        offg.setColor(Color.green);
        offg.drawString("Lives: " + ship.lives, 5, 15);
        offg.drawString("Score: " + score, 5, 30);
        offg.drawString("MASS LEFT: " + massToNextLevel, 5, 45);
        offg.drawString("Level " + Game.level, 5, 60);
        if (ship.lives <= 0) {
            offg.setFont(offg.getFont().deriveFont(40f));
            offg.drawString("Game Over", RESOLUTION_WIDTH / 2 - 45, RESOLUTION_HEIGHT / 2);
        }
        drawMinimap(offg);
        g.drawImage(offscreen, 0, 0, this);
        repaint();
    }

    /**
     * Draw the minimap to the screen.
     *
     * @param g The screens graphics.
     */
    public void drawMinimap(Graphics g) {

        g.setColor(Color.white);

        g.drawRect(MINIMAP_X, MINIMAP_Y, MINIMAP_SIZE, MINIMAP_SIZE);
        g.setColor(new Color(128, 128, 128, 125));
        g.fillRect(MINIMAP_X + 1, MINIMAP_Y + 1, MINIMAP_SIZE - 1, MINIMAP_SIZE - 1);


        g.setColor(Color.red);
        for (int i = 0; i < spaceJunk.size(); i++) {
            drawToMinimap(g, spaceJunk.get(i));
        }
        g.setColor(Color.blue);
        for (int i = 0; i < planets.size(); i++) {
            drawToMinimap(g, planets.get(i));
        }
        g.setColor(Color.GREEN);
        drawToMinimap(g, ship);

    }

    /**
     * Draw the game object to the games mini-map.
     *
     * @param g The screens graphics.
     * @param obj The object to be drawn.
     */
    private void drawToMinimap(Graphics g, GameObject obj) {
        double percentX = obj.xPosition / Game.SPACE_WIDTH;
        double percentY = obj.yPosition / Game.SPACE_HEIGHT;
        int x = MINIMAP_X + 1 + (int) (percentX * (MINIMAP_SIZE - 2)) - obj.minimapSize / 2;
        int y = MINIMAP_Y + 1 + (int) (percentY * (MINIMAP_SIZE - 2)) - obj.minimapSize / 2;

        g.fillRoundRect(x, y, obj.minimapSize, obj.minimapSize, obj.minimapSize,
                obj.minimapSize);
        // g.fillRect(x, y, obj.minimapSize, obj.minimapSize);
    }

    /**
     * Paint all collection of objects in the world.
     */
    public void paintLists() {
        for (int i = 0; i < spaceJunk.size(); i++) {
            SpaceJunk a = spaceJunk.get(i);
            if (distanceToShip((int) a.xPosition, (int) a.yPosition)
                    < Game.RESOLUTION_WIDTH) {
                a.paint(offg, cameraOffsetX, cameraOffsetY);
            }
        }
        for (int i = 0; i < planets.size(); i++) {
            Planet p = planets.get(i);
            if (distanceToShip((int) p.xPosition, (int) p.yPosition)
                    < Game.RESOLUTION_WIDTH * 3) {
                p.paint(offg, cameraOffsetX, cameraOffsetY);
            }
        }
        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            if (distanceToShip((int) b.xPosition,
                    (int) b.yPosition) < Game.RESOLUTION_WIDTH) {
                b.paint(offg, cameraOffsetX, cameraOffsetY);
            }
        }

        for (int i = 0; i < debris.size(); i++) {
            debris.get(i).paint(offg, cameraOffsetX, cameraOffsetY);
        }
    }
    /*
     * Update the graphics.
     */

    public void update(Graphics g) {
        paint(g);
    }

    /*
     * Handles user input.
     */
    public void handleInput() {
        if (upKey) {
            ship.accelerate();
        }

        if (leftKey) {
            ship.rotateLeft();
        }

        if (rightKey) {
            ship.rotateRight();
        }

        if (spaceKey) {
            ship.fireWeapon(bullets);
        }
    }

    /*
     * Checks keys being pressed on the keyboard.
     */
    public void keyPressed(KeyEvent e) {

        if (!ship.isActive()) {
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightKey = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftKey = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            upKey = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            spaceKey = true;
        }
    }

    /*
     * Checks keys being released on the keyboard.
     */
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightKey = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftKey = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            upKey = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            spaceKey = false;
        }
    }

    public void keyTyped(KeyEvent e) {
        //do nothing
    }

    /**
     * Return a copy of the polygon.
     *
     * @param polygon The polygon to be copied.
     * @return A copy of the polygon.
     */
    public static Polygon copyPolygon(Polygon polygon) {
        Polygon copy = new Polygon();

        for (int i = 0; i < polygon.npoints; i++) {
            copy.addPoint(polygon.xpoints[i], polygon.ypoints[i]);
        }
        return copy;
    }
}
