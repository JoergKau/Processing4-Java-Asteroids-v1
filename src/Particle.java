import processing.core.PApplet;
import processing.core.PVector;

public class Particle {
    private PApplet p;
    private PVector position;
    private PVector velocity;
    private float lifetime;
    private float maxLifetime;
    private float size;
    private int color;
    private float alpha;

    public Particle(PApplet p, float x, float y, float angle, float speed) {
        this.p = p;
        this.position = new PVector(x, y);

        // Velocity based on angle and speed with some randomness
        this.velocity = new PVector(
            PApplet.cos(angle) * speed,
            PApplet.sin(angle) * speed
        );

        this.maxLifetime = p.random(0.3f, 0.8f);
        this.lifetime = maxLifetime;
        this.size = p.random(2, 6);

        // Random color (orange to yellow to white)
        float colorChoice = p.random(1);
        if (colorChoice < 0.4f) {
            this.color = p.color(255, 150, 0); // Orange
        } else if (colorChoice < 0.7f) {
            this.color = p.color(255, 200, 50); // Yellow
        } else {
            this.color = p.color(255, 255, 200); // White-yellow
        }

        this.alpha = 255;
    }

    public void update(float deltaTime) {
        // Update position
        position.add(PVector.mult(velocity, deltaTime));

        // Apply slight gravity/deceleration
        velocity.mult(0.98f);

        // Decrease lifetime
        lifetime -= deltaTime;

        // Fade out based on remaining lifetime
        alpha = PApplet.map(lifetime, 0, maxLifetime, 0, 255);
    }

    public void draw() {
        p.pushStyle();
        p.noStroke();

        // Extract RGB components and apply alpha
        float r = p.red(color);
        float g = p.green(color);
        float b = p.blue(color);
        p.fill(r, g, b, alpha);

        // Draw particle
        p.ellipse(position.x, position.y, size, size);

        p.popStyle();
    }

    public boolean isDead() {
        return lifetime <= 0;
    }
}