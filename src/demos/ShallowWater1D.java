package demos;

import camera.QueasyCam;
import math.Vec3;
import physical.WaterColumn;
import processing.core.PApplet;
import processing.core.PShape;

import java.util.ArrayList;

public class ShallowWater1D extends PApplet {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    private QueasyCam queasyCam;

    ArrayList<WaterColumn> waterColumns;
    private int numColumns = 60;
    float lengthX = 80, lengthY = 60, lengthZ = 200;
    private float dz;
    private boolean isPlaying = true;
    PShape boat;

    public void settings() {
        size(WIDTH, HEIGHT, P3D);
    }

    public void setup() {
        surface.setTitle("Processing");
        queasyCam = new QueasyCam(this);
        queasyCam.sensitivity = 2f;
        boat = loadShape("LifeBoat_obj/LifeBoat.obj");
        resetSystem();
    }

    private void resetSystem() {
        waterColumns = new ArrayList<>();
        float columnHeight = 20;
        float momentum = 0;
        dz = lengthZ / numColumns;
        for (int i = 0; i < numColumns; i++) {
            waterColumns.add(new WaterColumn(this, columnHeight, momentum));
            if (i < 10) {
                columnHeight += 2;
            }
            if (i > 10 && i < 20) {
                columnHeight -= 2;
            }
            if (i > 20) {
                columnHeight = 20;
            }
        }
    }

    void drawWaterTank() {
        pushMatrix();
        stroke(255, 255);
        sphere(2);
        translate(0, lengthY, 0);
        fill(0, 0);
        box(lengthX, lengthY, lengthZ);
        popMatrix();
    }

    void drawWater() {
        float left = -numColumns / 2 * dz;
        float base = 1.5f * lengthY;
        for (int i = 0; i < numColumns - 1; i++) {
            WaterColumn column1 = waterColumns.get(i);
            WaterColumn column2 = waterColumns.get(i + 1);

            float x1 = -lengthX / 2;
            float x2 = lengthX / 2;
            float y1 = base - column1.height;
            float y2 = base - column2.height;
            float z1 = left + i * dz;
            float z2 = left + (i + 1) * dz;

            Vec3 v1 = Vec3.of(x1, column2.height, z2);
            v1.minus(x1, column1.height, z1);
            Vec3 v2 = Vec3.of(x2, column1.height, z1);
            v2.minus(x1, column1.height, z1);
            Vec3 normal = v1.cross(v2);

            // Top surface of the column
            fill(0, 80, 255);
            stroke(0, 80, 255);
            beginShape(QUADS);
            normal(normal.x, normal.y, normal.z);
            vertex(x1, y1, z1);
            vertex(x1, y2, z2);
            vertex(x2, y2, z2);
            vertex(x2, y1, z1);
            endShape();


            fill(0, 100, 255);
            stroke(0, 100, 255);
            // Side surfaces of the column
            beginShape(QUAD_STRIP);
            vertex(x1, y1, z1);
            vertex(x1, y2, z2);
            vertex(x1, base, z1);
            vertex(x1, base, z2);
            vertex(x2, base, z1);
            vertex(x2, base, z2);
            vertex(x2, y1, z1);
            vertex(x2, y2, z2);
            endShape();
        }
    }

    void updateLoop() {
        float dt = 0.01f;
        float g = 1f;
        float damp = 0.03f;

        // Half step
        for (int i = 0; i < numColumns - 1; i++) {
            WaterColumn w1 = waterColumns.get(i);
            WaterColumn w2 = waterColumns.get(i + 1);

            w1.midpointHeightZ = (w1.height + w2.height) / 2f;
            w1.midpointHeightZ += -(dt / 2f) * (w1.momentumZ - w2.momentumZ) / dz;

            w1.midpointZMomentum_alongZ = (w1.momentumZ + w2.momentumZ) / 2f;
            w1.midpointZMomentum_alongZ += -(dt / 2f) * (
                    sq(w2.momentumZ) / w2.height
                            - sq(w1.momentumZ) / w1.height
                            + 0.5f * g * sq(w2.height)
                            - 0.5f * g * sq(w1.height)
            ) / dz;
        }

        // Full step
        for (int i = 0; i < numColumns - 2; i++) {
            WaterColumn w1 = waterColumns.get(i);
            WaterColumn w2 = waterColumns.get(i + 1);

            w2.height += -dt * (w2.midpointZMomentum_alongZ - w1.midpointZMomentum_alongZ) / dz;
            w2.momentumZ += -dt * (
                    +sq(w2.midpointZMomentum_alongZ) / w2.midpointHeightZ
                            - sq(w1.midpointZMomentum_alongZ) / w1.midpointHeightZ
                            + 0.5 * g * sq(w2.midpointHeightZ)
                            - 0.5 * g * sq(w1.midpointHeightZ)
            ) / dz;

            // Damping
            w2.momentumZ += -dt * damp * w2.momentumZ;
        }

        // Boundary columns
        WaterColumn w0 = waterColumns.get(0);
        WaterColumn w1 = waterColumns.get(1);
        WaterColumn w_1 = waterColumns.get(numColumns - 1);
        WaterColumn w_2 = waterColumns.get(numColumns - 2);

        // Boundary conditions (reflective)
        w0.height = w1.height;
        w_1.height = w_2.height;
        w0.momentumZ = -w1.momentumZ;
        w_1.momentumZ = -w_2.momentumZ;
    }

    public void draw() {
        long start = millis();
        // update
        try {
            if (isPlaying) {
                for (int i = 0; i < 40; i++) {
                    updateLoop();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        long update = millis();
        // draw
        background(0);
        directionalLight(255, 255, 255, 0, 1, 2);
        pushMatrix();
        translate(-125, 140 - waterColumns.get(numColumns / 2).height, -150);
        rotateX(PI / 2);
        scale(0.5f);
        shape(boat);
        popMatrix();
        drawWater();
        drawWaterTank();
        long draw = millis();

        surface.setTitle("Processing - FPS: " + Math.round(frameRate) + " Update: " + (update - start) + "ms Draw " + (draw - update) + "ms");
    }

    public void keyPressed() {
        if (key == 'u') {
            isPlaying = !isPlaying;
        }
        if (key == 'r') {
            resetSystem();
        }
    }

    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"demos.ShallowWater1D"};
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}