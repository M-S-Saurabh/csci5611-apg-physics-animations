package demos;

import camera.QueasyCam;
import math.Vec3;
import physical.WaterColumn;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.util.ArrayList;

public class ShallowWater2D extends PApplet {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    private QueasyCam queasyCam;

    PImage waterTexture;
    ArrayList<ArrayList<WaterColumn>> waterColumns = new ArrayList<>();
    private int numColumns = 60;
    float lengthX = 200, lengthY = 60, lengthZ = 200;
    private float dz;
    private boolean isPlaying = false;
	private boolean textureOn = false;

    public void settings() {
        size(WIDTH, HEIGHT, P3D);
    }

    public void setup() {
        surface.setTitle("Processing");
        queasyCam = new QueasyCam(this);
        queasyCam.sensitivity = 2f;

        ambientLight(255,255,255);
        waterTexture = loadImage("water.jpg");
        float columnHeight = 20;
        float momentum = 0;
        dz = lengthZ / numColumns;
        for (int i = 0; i < numColumns; i++) {
        	waterColumns.add(new ArrayList<WaterColumn>());
            for(int j=0; j<numColumns; j++) {
            	if(i > 24 && i < 36 && j < 36 && j > 24) {
            		waterColumns.get(i).add(new WaterColumn(this, 2*columnHeight, momentum));
            	}else {
            		waterColumns.get(i).add(new WaterColumn(this, columnHeight, momentum));
            	}
            }
        }
    }

    void drawWaterTank() {
        pushMatrix();
        stroke(255);
        translate(0, lengthY, 0);
        fill(0, 0);
        box(lengthX, lengthY, lengthZ);
        popMatrix();
    }

    void drawWater() {
        float left = -numColumns / 2 * dz;
        float base = 1.5f * lengthY;
        float front = -numColumns / 2 * dz;
        
        
        for (int i = 0; i < numColumns - 1; i++) {
            beginShape(TRIANGLE_STRIP);
            if(textureOn) {
            	noFill();
                noStroke();
            	textureMode(PConstants.NORMAL);
            	texture(waterTexture);
            }else {
            	fill(0, 80, 255);
            	stroke(255);
            }
            for(int j=0; j <numColumns - 1; j++) {
            	WaterColumn column1 = waterColumns.get(i).get(j);
                WaterColumn columnZ = waterColumns.get(i + 1).get(j);
                
                float x1 = front + j * dz;
                float xZ = front + j * dz;
                
                float y1 = base - column1.height;
                float yZ = base - columnZ.height;
                
                float z1 = left + i * dz;
                float zZ = left + (i + 1) * dz;
                if(textureOn) {
	                float u = PApplet.map(j, 0, numColumns - 1, 0, 1);
	                float v1 = PApplet.map(i, 0, numColumns - 1, 0, 1);
	                float v2 = PApplet.map(i+1, 0, numColumns - 1, 0, 1);
	                vertex(x1, y1, z1, u, v1);
	                vertex(xZ, yZ, zZ, u, v2);
                }else {
                	vertex(x1, y1, z1);
	                vertex(xZ, yZ, zZ);
                }
            }
            endShape();
        }
    }

    void updateLoop() {
        float dt = 0.001f;
        float g = 1f;
        float damp = 0.1f;

        // Half step
        for (int i = 0; i < numColumns; i++) {
        	for(int j=0; j< numColumns; j++) {
	            WaterColumn w1 = waterColumns.get(i).get(j);
	            if(i < numColumns-1) {
		            WaterColumn w2 = waterColumns.get(i + 1).get(j);
		
		            w1.midpointHeightZ = (w1.height + w2.height) / 2f;
		            w1.midpointHeightZ += -(dt / 2f) * (w1.momentumZ - w2.momentumZ) / dz;
		
		            w1.midpointZMomentum_alongZ = (w1.momentumZ + w2.momentumZ) / 2f;
		            w1.midpointZMomentum_alongZ += -(dt / 2f) * ( sq(w2.momentumZ) / w2.height
									                            - sq(w1.momentumZ) / w1.height
									                            + 0.5f * g * sq(w2.height)
									                            - 0.5f * g * sq(w1.height)
						            							) / dz;
		            w1.midpointZMomentum_alongX = (w1.momentumZ + w2.momentumZ) / 2f;
		            w1.midpointZMomentum_alongX += -(dt / 2f) * ( sq(w2.momentumZ) / w2.height
									                            - sq(w1.momentumZ) / w1.height
									                            + 0.5f * g * sq(w2.height)
									                            - 0.5f * g * sq(w1.height)
						            							) / dz;
        		}
	            
	            if(j < numColumns-1) {
		            WaterColumn w3 = waterColumns.get(i).get(j+1);
		            
		            w1.midpointHeightX = (w1.height + w3.height) / 2f;
		            w1.midpointHeightX += -(dt / 2f) * (w1.momentumZ - w3.momentumZ) / dz;
		            
		            w1.midpointXMomentum_alongZ = (w1.momentumX + w3.momentumX) / 2f;
		            w1.midpointXMomentum_alongZ += -(dt / 2f) * ( sq(w3.momentumX) / w3.height
									                            - sq(w1.momentumX) / w1.height
									                            + 0.5f * g * sq(w3.height)
									                            - 0.5f * g * sq(w1.height)
									            				) / dz;
		            w1.midpointXMomentum_alongX = (w1.momentumX + w3.momentumX) / 2f;
		            w1.midpointXMomentum_alongX += -(dt / 2f) * ( sq(w3.momentumX) / w3.height
									                            - sq(w1.momentumX) / w1.height
									                            + 0.5f * g * sq(w3.height)
									                            - 0.5f * g * sq(w1.height)
									            				) / dz;
	            }
        	}
        }

        // Full step
        for (int i=0; i < numColumns-2; i++) {
        	for(int j=0; j<numColumns-2; j++) {
	            WaterColumn w1 = waterColumns.get(i + 1).get(j + 1);
	            WaterColumn w2 = waterColumns.get(i).get(j + 1);
	            WaterColumn w3 = waterColumns.get(i + 1).get(j);
	
	            w1.height += -dt * (w1.midpointZMomentum_alongZ - w2.midpointZMomentum_alongZ) / dz;
	            w1.height += -dt * (w1.midpointXMomentum_alongX - w3.midpointXMomentum_alongX) / dz;
	            
	            w1.momentumZ += -dt*( sq(w1.midpointZMomentum_alongZ) / w1.midpointHeightZ
		                            - sq(w2.midpointZMomentum_alongZ) / w2.midpointHeightZ
		                            + 0.5 * g * sq(w1.midpointHeightZ)
		                            - 0.5 * g * sq(w2.midpointHeightZ)
            					  	) / dz;
	            w1.momentumZ += -dt*( w1.midpointXMomentum_alongX * w1.midpointZMomentum_alongX / w1.midpointHeightX
	            					- w3.midpointXMomentum_alongX * w3.midpointZMomentum_alongX / w3.midpointHeightX
	            					) / dz;
	            
	            w1.momentumX += -dt*( sq(w1.midpointXMomentum_alongX) / w1.midpointHeightX
			                        - sq(w3.midpointXMomentum_alongX) / w3.midpointHeightX
			                        + 0.5 * g * sq(w1.midpointHeightX)
			                        - 0.5 * g * sq(w3.midpointHeightX)
								  	) / dz;
	            w1.momentumX += -dt*( w1.midpointXMomentum_alongZ * w1.midpointZMomentum_alongZ / w1.midpointHeightZ
			    					- w2.midpointXMomentum_alongZ * w2.midpointZMomentum_alongZ / w2.midpointHeightZ
			    					) / dz;
	
	            // Damping
	            w1.momentumZ += -dt * damp * w1.momentumZ;
	            w1.momentumX += -dt * damp * w1.momentumX;
        	}
        }

        boundaryConditions();
    }

    private void boundaryConditions(){
    	// Reflecting at walls
    	for(int i=0;i<numColumns;i++){
    		WaterColumn w_i0 = waterColumns.get(i).get(0);
            WaterColumn w_i1 = waterColumns.get(i).get(1);
            WaterColumn w_is = waterColumns.get(i).get(numColumns - 1);
            WaterColumn w_is_1 = waterColumns.get(i).get(numColumns - 2);
            
            WaterColumn w_0i = waterColumns.get(0).get(i);
            WaterColumn w_1i = waterColumns.get(1).get(i);
            WaterColumn w_si = waterColumns.get(numColumns - 1).get(i);
            WaterColumn w_s_1i = waterColumns.get(numColumns - 2).get(i);
            
    		w_i0.height = w_i1.height;
    		w_is.height = w_is_1.height;
    		w_0i.height = w_1i.height;
    		w_si.height = w_s_1i.height;
    		
    		w_i0.momentumZ = w_i1.momentumZ;
    		w_is.momentumZ = w_is_1.momentumZ;
    		w_0i.momentumZ = -w_1i.momentumZ;
    		w_si.momentumZ = -w_s_1i.momentumZ;
    		
    		w_i0.momentumX = -w_i1.momentumX;
    		w_is.momentumX = -w_is_1.momentumX;
    		w_0i.momentumX = w_1i.momentumX;
    		w_si.momentumX = w_s_1i.momentumX;
    	}
    }

    public void draw() {
        long start = millis();
        // update
        try {
            if (isPlaying) {
                for (int i = 0; i < 50; i++) {
                    updateLoop();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        long update = millis();
        // draw
        background(0);
        drawWater();
        if(textureOn) {
        	drawWaterTank();
        }
        long draw = millis();

        surface.setTitle("Processing - FPS: " + Math.round(frameRate) + " Update: " + (update - start) + "ms Draw " + (draw - update) + "ms");
    }

    public void keyPressed() {
        if (key == 'u') {
            isPlaying = !isPlaying;
        }
        if (key == 't') {
            textureOn = !textureOn;
        }
    }

    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"demos.ShallowWater2D"};
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}