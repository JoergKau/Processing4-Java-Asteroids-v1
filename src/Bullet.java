import processing.core.PApplet;
import processing.core.PVector;

public class Bullet {
    private PApplet p;
    private PVector position;
    private PVector velocity;
    private float lifetime;
    private boolean dead;

    private static final float BULLET_SPEED = 400.0f;
    private static final float MAX_LIFETIME = 1.5f; // seconds

    public Bullet(PApplet p, PVector position, float angle) {
        this.p = p;
        this.position = position.copy();

        // Calculate velocity based on angle
        this.velocity = new PVector(
            PApplet.cos(angle - PApplet.HALF_PI) * BULLET_SPEED,
            PApplet.sin(angle - PApplet.HALF_PI) * BULLET_SPEED
        );

        this.lifetime = 0;
        this.dead = false;
    }

    public void update(float deltaTime) {
        // Update position
        position.add(PVector.mult(velocity, deltaTime));

        // Update lifetime
        lifetime += deltaTime;
        if (lifetime > MAX_LIFETIME) {
            dead = true;
        }

        // Check if off screen (wrap or die)
        if (position.x < 0 || position.x > p.width ||
            position.y < 0 || position.y > p.height) {
            dead = true;
        }
    }

    public void draw() {
        p.fill(255, 255, 0);
        p.noStroke();
        p.ellipse(position.x, position.y, 4, 4);
    }

    public boolean isDead() {
        return dead;
    }

    public void kill() {
        dead = true;
    }

    public PVector getPosition() {
        return position.copy();
    }
}
