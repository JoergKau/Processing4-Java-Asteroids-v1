Asteroids Game by JoKausch and Clode-Code 0.1.11-beta

A simple modern implementation of the classic arcade game Asteroids, built with Processing and Java. Navigate through space, shoot asteroids, and survive as long as possible!
Mainly a template how to use the Processing library within VS Code or intelliJ when you do not want to use the Processing IDE

## Game Features

- **Smooth Spaceship Controls**: Rotate and thrust your way through space with responsive controls
- **Realistic Physics**: Experience realistic movement and collision detection
- **Particle Effects**: Enjoy satisfying visual feedback with explosion and thruster particles
- **Sound Effects**: Immersive audio with thruster, shooting, and explosion sounds
- **Progressive Difficulty**: The game gets more challenging as you progress
- **Score System**: Earn points for destroying asteroids
- **Lives System**: You have 3 lives before game over

## How to Play

### Controls
- **Left Arrow**: Rotate spaceship counter-clockwise
- **Right Arrow**: Rotate spaceship clockwise
- **Up Arrow**: Apply thrust in the current direction
- **Space**: Shoot bullets
- **R**: Restart game after game over

### Game Rules
1. Destroy asteroids by shooting them with your ship's blaster
2. Each asteroid breaks into smaller pieces when hit
3. Avoid colliding with asteroids or you'll lose a life
4. The game ends when you lose all your lives
5. Try to achieve the highest score possible!

## Technical Details

- Built with **Processing 4** and **Java**
- Uses object-oriented programming principles
- Features smooth animations with delta time calculations
- Includes sound effects for an immersive experience

## Requirements

- Java Development Kit (JDK) 17 (current Processing Version 4.4.8 is build upon JDK 17)
- install Processing 4 from https://processing.org
- This project is not using the Processing IDE but VS Code or IntelliJ.
- Ensure Processing core library is added to the project (come along with the Processing app)
- of today this is "core-4.4.8.jar"


## Running the Game

1. Open the project in your preferred Java IDE
2. Make sure all required Processing libraries are installed
3. Run the `App.java` file to start the game

## Project Structure

- `src/App.java`: Main game class
- `src/Spaceship.java`: Player spaceship implementation
- `src/Asteroid.java`: Asteroid objects and behavior
- `src/Bullet.java`: Bullet mechanics
- `src/Particle.java`: Visual effects system
- `src/Star.java`: Background starfield implementation

## Sound Credits

Make sure to include the following sound files in a `snd/` directory:
- `thrust.wav`
- `fire.wav`
- `explode.wav`

## License

This project is open source and available under the [MIT License](LICENSE).


