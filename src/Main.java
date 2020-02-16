import camera.QueasyCam;
import linalg.Vec3;
import physical.GridSpringMassSystem;
import physical.UserControlledBall;
import processing.core.PApplet;

public class Main extends PApplet {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    //    private LiamCam liamCam;
    private QueasyCam queasyCam;
    private UserControlledBall userControlledBall;

    private GridSpringMassSystem gridSpringMassSystem;

    public void settings() {
        size(WIDTH, HEIGHT, P3D);
    }

    public void setup() {
        surface.setTitle("Processing");
//        liamCam = new LiamCam(this);
        queasyCam = new QueasyCam(this);

        gridSpringMassSystem = new GridSpringMassSystem(
                this,
                30, 30,
                10,
                2, 500, 1000f, loadImage("aladdin-s-carpet.jpeg"),
                1.5f, -50f, -50f, -100f,
                (i, j, m, n) -> ((i == 0 || i == m - 1) && (j == n - 1 || j % 5 == 0)),
                GridSpringMassSystem.Layout.ZX);
        userControlledBall = new UserControlledBall(this, 10, Vec3.of(0, 20, -80), Vec3.of(0, 128, 0));
    }

    public void draw() {
//        liamCam.Update(1.0f / frameRate);
        long start = millis();
        // ball update
        if (keyPressed) {
            switch (key) {
                case '8':
                    userControlledBall.update(Vec3.of(0, 0, -10), 0.05f);
                    break;
                case '5':
                    userControlledBall.update(Vec3.of(0, 0, 10), 0.05f);
                    break;
                case '4':
                    userControlledBall.update(Vec3.of(-10, 0, 0), 0.05f);
                    break;
                case '6':
                    userControlledBall.update(Vec3.of(10, 0, 0), 0.05f);
                    break;
                case '7':
                    userControlledBall.update(Vec3.of(0, 10, 0), 0.05f);
                    break;
                case '9':
                    userControlledBall.update(Vec3.of(0, -10, 0), 0.05f);
                    break;
            }
        }
        // cloth update
        try {
            for (int i = 0; i < 140; ++i) {
                gridSpringMassSystem.update(userControlledBall, 0.002f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        long update = millis();

        background(0);
        gridSpringMassSystem.draw();
        userControlledBall.draw();
        long draw = millis();
        surface.setTitle("Processing - FPS: " + Math.round(frameRate) + " Update: " + (update - start) + "ms Draw " + (draw - update) + "ms");
    }

    public void keyPressed() {
//        liamCam.HandleKeyPressed();
    }

    public void keyReleased() {
//        liamCam.HandleKeyReleased();
    }

    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"Main"};
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}
