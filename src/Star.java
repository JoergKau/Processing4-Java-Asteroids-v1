import processing.core.PApplet;

public class Star {
    private PApplet p;
    private float x, y;
    private float size;
    private float brightness;
    private float twinkleSpeed;
    private float twinklePhase;

    public Star(PApplet p) {
        this.p = p;
        this.x = p.random(p.width);
        this.y = p.random(p.height);
        this.size = p.random(1, 3);
        this.brightness = p.random(100, 255);
        this.twinkleSpeed = p.random(1, 3);
        this.twinklePhase = p.random(PApplet.TWO_PI);
    }

    public void update(float deltaTime) {
        twinklePhase += twinkleSpeed * deltaTime;
    }

    public void draw() {
        float twinkle = PApplet.map(PApplet.sin(twinklePhase), -1, 1, 0.5f, 1.0f);
        p.fill(brightness * twinkle);
        p.noStroke();
        p.ellipse(x, y, size, size);
    }
}
