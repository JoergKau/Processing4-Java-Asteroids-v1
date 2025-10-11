// https://processing.github.io/processing4-javadocs/
// LINUX: /snap/processing/current/opt/processing/lib/app/resources/core/library
// WIN: C:\Program Files\Processing\app\resources\core\library


/* Initialization:
1) last_time initialization in setup() to ensure accurate timing from the first frame.
2) Readability & Precision: Introduced NANOS_TO_SECONDS constant for clearer time unit conversion.
3) Simplified the delta time calculation using multiplication instead of division.
4) Stability: Added MAX_DELTA_TIME (100ms) to prevent large jumps in case of frame rate drops or debugging pauses.
5) This ensures the circle's movement remains smooth and predictable.
6) Performance: The calculation is now more efficient with a single multiplication instead of division.*/

// src/Sketch.java
import processing.core.PApplet;
import processing.core.PVector;
import processing.sound.*;
import java.util.ArrayList;

public class App extends PApplet {
    public static void main(String[] args) {
        PApplet.main("App", args);
    }

    long last_time;
    float delta_time;
    long time;
    static final float NANOS_TO_SECONDS = 1.0f / 1_000_000_000.0f;
    static final float MAX_DELTA_TIME = 0.1f;
    final int WIDTH = 800;
    final int HEIGHT = 600;

    // Spaceship
    Spaceship spaceship;

    // Starfield
    ArrayList<Star> stars;
    final int NUM_STARS = 150;

    // Asteroids
    ArrayList<Asteroid> asteroids;
    final int INITIAL_ASTEROIDS = 5;

    // Bullets
    ArrayList<Bullet> bullets;
    float shootCooldown = 0;
    final float SHOOT_DELAY = 0.15f; // seconds between shots

    // Particles
    ArrayList<Particle> particles;

    // Sound effects
    SoundFile thrustSound;
    SoundFile fireSound;
    SoundFile explodeSound;
    boolean thrustPlaying = false;

    // Game state
    int score = 0;
    int lives = 3;
    boolean playerAlive = true;
    boolean gameOver = false;
    float respawnTimer = 0;
    final float RESPAWN_DELAY = 2.0f; // seconds before respawn
    final int NUM_EXPLOSION_PARTICLES = 50;

    int shownFPS = 0;
    long lastFPSUpdate = 0;

    public void settings() {
        size(WIDTH, HEIGHT);
        smooth(4); // Höchste Anti-Aliasing-Stufe ist 8
    }

    public void setup() {
        pixelDensity(2);
        frameRate(60);
        strokeWeight(4);

        last_time = System.nanoTime();

        // Load sound files
        thrustSound = new SoundFile(this, "snd/thrust.wav");
        fireSound = new SoundFile(this, "snd/fire.wav");
        explodeSound = new SoundFile(this, "snd/explode.wav");

        // Initialize spaceship
        spaceship = new Spaceship(this, WIDTH / 2.0f, HEIGHT / 2.0f);

        // Create starfield
        stars = new ArrayList<>();
        for (int i = 0; i < NUM_STARS; i++) {
            stars.add(new Star(this));
        }

        // Create asteroids
        asteroids = new ArrayList<>();
        bullets = new ArrayList<>();
        particles = new ArrayList<>();
        spawnAsteroids(INITIAL_ASTEROIDS);
    }

    void spawnAsteroids(int count) {
        for (int i = 0; i < count; i++) {
            // Spawn at random edge of screen
            float x, y;
            if (random(1) < 0.5) {
                x = random(1) < 0.5 ? -50 : WIDTH + 50;
                y = random(HEIGHT);
            } else {
                x = random(WIDTH);
                y = random(1) < 0.5 ? -50 : HEIGHT + 50;
            }
            asteroids.add(new Asteroid(this, x, y, 0)); // Type 0 = large
        }
    }

    public void draw() {
        // Space background (dark blue to black - simple fill)
        background(5, 5, 15);

        // Calculate delta time
        time = System.nanoTime();
        delta_time = (time - last_time) * NANOS_TO_SECONDS;
        delta_time = Math.min(delta_time, MAX_DELTA_TIME);
        last_time = time;

        // Update and draw stars
        for (Star star : stars) {
            star.update(delta_time);
            star.draw();
        }

        // Update shoot cooldown
        if (shootCooldown > 0) {
            shootCooldown -= delta_time;
        }

        // Handle respawn timer
        if (!playerAlive && !gameOver) {
            respawnTimer -= delta_time;
            if (respawnTimer <= 0) {
                if (lives > 0) {
                    // Respawn player
                    spaceship = new Spaceship(this, WIDTH / 2.0f, HEIGHT / 2.0f);
                    playerAlive = true;
                } else {
                    // Game over
                    gameOver = true;
                }
            }
        }

        // Handle keyboard input for spaceship (only if alive)
        if (playerAlive) {
            if (keyPressed) {
                if (key == CODED) {
                    if (keyCode == UP) {
                        spaceship.thrust(delta_time);
                        // Play thrust sound in loop
                        if (!thrustPlaying) {
                            thrustSound.loop();
                            thrustPlaying = true;
                        }
                    } else if (keyCode == LEFT) {
                        spaceship.rotate(-1);
                    } else if (keyCode == RIGHT) {
                        spaceship.rotate(1);
                    }
                } else if (key == ' ') {
                    // Shoot
                    if (shootCooldown <= 0) {
                        bullets.add(spaceship.shoot());
                        shootCooldown = SHOOT_DELAY;
                        // Play fire sound
                        fireSound.play();
                    }
                }
            } else {
                spaceship.stopThrust();
                spaceship.stopRotation();
                // Stop thrust sound
                if (thrustPlaying) {
                    thrustSound.stop();
                    thrustPlaying = false;
                }
            }

            // Update and draw spaceship
            spaceship.update(delta_time);
            spaceship.draw();
        }

        // Update and draw asteroids
        for (int i = asteroids.size() - 1; i >= 0; i--) {
            Asteroid asteroid = asteroids.get(i);
            asteroid.update(delta_time);
            asteroid.draw();

            // Check player-asteroid collision (only if player is alive)
            if (playerAlive && spaceship.collidesWith(asteroid.getPosition(), asteroid.getSize())) {
                // Player hit!
                playerAlive = false;
                lives--;
                respawnTimer = RESPAWN_DELAY;

                // Play explosion sound
                explodeSound.play();

                // Stop thrust sound if playing
                if (thrustPlaying) {
                    thrustSound.stop();
                    thrustPlaying = false;
                }

                // Create explosion at player position
                PVector playerPos = spaceship.getPosition();
                for (int j = 0; j < NUM_EXPLOSION_PARTICLES; j++) {
                    float angle = random(TWO_PI);
                    float speed = random(50, 200);
                    particles.add(new Particle(this, playerPos.x, playerPos.y, angle, speed));
                }
            }
        }

        // Update and draw particles
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle particle = particles.get(i);
            particle.update(delta_time);
            particle.draw();

            // Remove dead particles
            if (particle.isDead()) {
                particles.remove(i);
            }
        }

        // Update and draw bullets
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(delta_time);
            bullet.draw();

            // Remove dead bullets
            if (bullet.isDead()) {
                bullets.remove(i);
            }
        }

        // Check bullet-asteroid collisions
        for (int i = asteroids.size() - 1; i >= 0; i--) {
            Asteroid asteroid = asteroids.get(i);
            for (int j = bullets.size() - 1; j >= 0; j--) {
                Bullet bullet = bullets.get(j);

                if (asteroid.collidesWith(bullet.getPosition())) {
                    // Hit!
                    bullet.kill();
                    bullets.remove(j);

                    // Play explosion sound
                    explodeSound.play();

                    // Split asteroid
                    int type = asteroid.getType();
                    if (type < 2) { // Not the smallest
                        // Create 2 smaller asteroids
                        for (int k = 0; k < 2; k++) {
                            asteroids.add(new Asteroid(
                                    this,
                                    asteroid.getPosition().x,
                                    asteroid.getPosition().y,
                                    type + 1,
                                    asteroid.getVelocity()));
                        }
                    }

                    // Remove asteroid
                    asteroids.remove(i);

                    // Update score
                    score += (3 - type) * 10; // Larger = more points
                    break;
                }
            }
        }

        // Spawn new asteroids if all are destroyed
        if (asteroids.isEmpty()) {
            spawnAsteroids(INITIAL_ASTEROIDS + 2);
        }

        // FPS display
        if (millis() - lastFPSUpdate > 100) {
            shownFPS = (int) frameRate;
            lastFPSUpdate = millis();
        }
        fill(0, 180);
        noStroke();
        rect(10, 10, 80, 30, 8);
        fill(255);
        textSize(18);
        textAlign(LEFT, TOP);
        text("FPS: " + shownFPS, 18, 16);

        // Score display
        fill(0, 180);
        noStroke();
        rect(WIDTH - 120, 10, 110, 30, 8);
        fill(255);
        textSize(18);
        textAlign(LEFT, TOP);
        text("Score: " + score, WIDTH - 110, 16);

        // Lives display
        fill(0, 180);
        noStroke();
        rect(WIDTH - 120, 50, 110, 30, 8);
        fill(255);
        textSize(18);
        textAlign(LEFT, TOP);
        text("Lives: " + lives, WIDTH - 110, 56);

        // Controls display
        fill(0, 180);
        rect(10, HEIGHT - 90, 200, 80, 8);
        fill(255);
        textSize(14);
        textAlign(LEFT, TOP);
        text("Controls:", 18, HEIGHT - 85);
        text("↑ = Thrust", 18, HEIGHT - 68);
        text("← → = Rotate", 18, HEIGHT - 51);
        text("SPACE = Shoot", 18, HEIGHT - 34);

        // Game over screen
        if (gameOver) {
            // Semi-transparent overlay
            fill(0, 0, 0, 200);
            noStroke();
            rect(0, 0, WIDTH, HEIGHT);

            // Game Over text
            fill(255, 50, 50);
            textSize(64);
            textAlign(CENTER, CENTER);
            text("GAME OVER", WIDTH / 2.0f, HEIGHT / 2.0f - 60);

            // Final score
            fill(255);
            textSize(32);
            text("Final Score: " + score, WIDTH / 2.0f, HEIGHT / 2.0f);

            // Restart instruction
            textSize(24);
            text("Press R to Restart", WIDTH / 2.0f, HEIGHT / 2.0f + 60);
        }
    }

    public void keyPressed() {
        // Restart game when R is pressed and game is over
        if (gameOver && (key == 'r' || key == 'R')) {
            restartGame();
        }
    }

    void restartGame() {
        // Reset game state
        score = 0;
        lives = 3;
        playerAlive = true;
        gameOver = false;
        respawnTimer = 0;

        // Clear all objects
        asteroids.clear();
        bullets.clear();
        particles.clear();

        // Recreate spaceship
        spaceship = new Spaceship(this, WIDTH / 2.0f, HEIGHT / 2.0f);

        // Spawn initial asteroids
        spawnAsteroids(INITIAL_ASTEROIDS);
    }
}
