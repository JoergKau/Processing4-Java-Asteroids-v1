import processing.core.PApplet;
import processing.core.PVector;

public class Spaceship {
    private PApplet p;
    private PVector position;
    private PVector velocity;
    private float rotation; // Rotation in Radians
    private float rotationSpeed;

    private final float MAX_SPEED = 300.0f;
    private final float ACCELERATION = 400.0f;
    private final float ROTATION_SPEED = 3.0f;
    private final float FRICTION = 0.98f;

    private final float SIZE = 40.0f;

    // Thruster effect
    private boolean thrusterActive = false;
    private float thrusterFlicker = 0;

    public Spaceship(PApplet p, float x, float y) {
        this.p = p;
        this.position = new PVector(x, y);
        this.velocity = new PVector(0, 0);
        this.rotation = 0;
        this.rotationSpeed = 0;
    }

    public void update(float deltaTime) {
        // Apply friction
        velocity.mult(FRICTION);

        // Update position
        position.add(PVector.mult(velocity, deltaTime));

        // Update rotation
        rotation += rotationSpeed * deltaTime;

        // Wrap around screen edges
        if (position.x > p.width + SIZE) position.x = -SIZE;
        if (position.x < -SIZE) position.x = p.width + SIZE;
        if (position.y > p.height + SIZE) position.y = -SIZE;
        if (position.y < -SIZE) position.y = p.height + SIZE;

        // Update thruster flicker
        if (thrusterActive) {
            thrusterFlicker = p.random(0.5f, 1.0f);
        }
    }

    public void thrust(float deltaTime) {
        thrusterActive = true;

        // Calculate thrust direction based on rotation
        PVector direction = new PVector(
            PApplet.cos(rotation - PApplet.HALF_PI),
            PApplet.sin(rotation - PApplet.HALF_PI)
        );

        // Apply acceleration
        PVector acceleration = PVector.mult(direction, ACCELERATION * deltaTime);
        velocity.add(acceleration);

        // Limit speed
        if (velocity.mag() > MAX_SPEED) {
            velocity.setMag(MAX_SPEED);
        }
    }

    public void rotate(float direction) {
        rotationSpeed = direction * ROTATION_SPEED;
    }

    public void stopRotation() {
        rotationSpeed = 0;
    }

    public void stopThrust() {
        thrusterActive = false;
    }

    public void draw() {
        p.pushMatrix();
        p.translate(position.x, position.y);
        p.rotate(rotation);

        // Draw thruster flame
        if (thrusterActive) {
            p.fill(255, 150, 0, 200 * thrusterFlicker);
            p.noStroke();
            p.beginShape();
            p.vertex(-8, SIZE * 0.4f);
            p.vertex(0, SIZE * 0.4f + 15 * thrusterFlicker);
            p.vertex(8, SIZE * 0.4f);
            p.endShape(PApplet.CLOSE);
        }

        // Draw spaceship body (triangular ship)
        p.fill(200, 200, 220);
        p.stroke(100, 100, 150);
        p.strokeWeight(2);
        p.beginShape();
        p.vertex(0, -SIZE * 0.5f);           // Nose
        p.vertex(-SIZE * 0.3f, SIZE * 0.4f);  // Left wing
        p.vertex(0, SIZE * 0.2f);             // Center back
        p.vertex(SIZE * 0.3f, SIZE * 0.4f);   // Right wing
        p.endShape(PApplet.CLOSE);

        // Draw cockpit
        p.fill(100, 150, 200, 180);
        p.ellipse(0, -SIZE * 0.15f, SIZE * 0.25f, SIZE * 0.35f);

        // Draw wing details
        p.stroke(150, 150, 180);
        p.strokeWeight(1);
        p.line(-SIZE * 0.2f, SIZE * 0.1f, -SIZE * 0.25f, SIZE * 0.35f);
        p.line(SIZE * 0.2f, SIZE * 0.1f, SIZE * 0.25f, SIZE * 0.35f);

        p.popMatrix();
    }

    public PVector getPosition() {
        return position.copy();
    }

    public float getRotation() {
        return rotation;
    }

    public Bullet shoot() {
        // Create bullet at ship's nose position
        PVector noseOffset = new PVector(
            PApplet.cos(rotation - PApplet.HALF_PI) * SIZE * 0.5f,
            PApplet.sin(rotation - PApplet.HALF_PI) * SIZE * 0.5f
        );
        PVector bulletPos = PVector.add(position, noseOffset);

        return new Bullet(p, bulletPos, rotation);
    }

    public boolean collidesWith(PVector point, float radius) {
        return PVector.dist(position, point) < SIZE * 0.3f + radius;
    }
}
