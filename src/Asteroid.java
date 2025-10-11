import processing.core.PApplet;
import processing.core.PVector;

public class Asteroid {
    private PApplet p;
    private PVector position;
    private PVector velocity;
    private float rotation;
    private float rotationSpeed;
    private float size;
    private int type; // 0 = large, 1 = medium, 2 = small

    // Asteroid shape (irregular polygon)
    private PVector[] shape;
    private int numPoints;

    public Asteroid(PApplet p, float x, float y, int type) {
        this.p = p;
        this.position = new PVector(x, y);
        this.type = type;

        // Size based on type
        switch(type) {
            case 0: this.size = 50; break;  // Large
            case 1: this.size = 30; break;  // Medium
            case 2: this.size = 15; break;  // Small
            default: this.size = 50;
        }

        // Random velocity
        float speed = p.random(30, 80);
        float angle = p.random(PApplet.TWO_PI);
        this.velocity = new PVector(
            PApplet.cos(angle) * speed,
            PApplet.sin(angle) * speed
        );

        this.rotation = p.random(PApplet.TWO_PI);
        this.rotationSpeed = p.random(-2, 2);

        // Create irregular shape
        createShape();
    }

    // Constructor for creating child asteroids with specific velocity
    public Asteroid(PApplet p, float x, float y, int type, PVector inheritedVelocity) {
        this(p, x, y, type);

        // Add some randomness to inherited velocity
        this.velocity.add(new PVector(p.random(-50, 50), p.random(-50, 50)));
    }

    private void createShape() {
        numPoints = (int) p.random(6, 10);
        shape = new PVector[numPoints];

        for (int i = 0; i < numPoints; i++) {
            float angle = PApplet.map(i, 0, numPoints, 0, PApplet.TWO_PI);
            float radius = size * p.random(0.6f, 1.0f);
            shape[i] = new PVector(
                PApplet.cos(angle) * radius,
                PApplet.sin(angle) * radius
            );
        }
    }

    public void update(float deltaTime) {
        // Update position
        position.add(PVector.mult(velocity, deltaTime));

        // Update rotation
        rotation += rotationSpeed * deltaTime;

        // Wrap around screen
        if (position.x > p.width + size) position.x = -size;
        if (position.x < -size) position.x = p.width + size;
        if (position.y > p.height + size) position.y = -size;
        if (position.y < -size) position.y = p.height + size;
    }

    public void draw() {
        p.pushMatrix();
        p.translate(position.x, position.y);
        p.rotate(rotation);

        // Draw asteroid
        p.fill(120, 100, 80);
        p.stroke(180, 160, 140);
        p.strokeWeight(2);

        p.beginShape();
        for (PVector point : shape) {
            p.vertex(point.x, point.y);
        }
        p.endShape(PApplet.CLOSE);

        p.popMatrix();
    }

    public boolean collidesWith(PVector point) {
        return PVector.dist(position, point) < size;
    }

    public PVector getPosition() {
        return position.copy();
    }

    public int getType() {
        return type;
    }

    public float getSize() {
        return size;
    }

    public PVector getVelocity() {
        return velocity.copy();
    }
}
