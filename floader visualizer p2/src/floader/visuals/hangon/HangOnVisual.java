package floader.visuals.hangon;

import java.util.Iterator;

import oscP5.OscMessage;
import floader.visuals.IVisual;
import wblut.geom.*;
import wblut.processing.*;
import wblut.hemesh.*;
import peasy.*;
import processing.core.PApplet;
import processing.core.PGraphics;

@SuppressWarnings("serial")
public class HangOnVisual implements IVisual {

	float multiplier = 1;

	int numSpheres = 40;
	WB_Render meshRenderer;
	HE_Mesh[] spheres;
	int[] sphereColors;

	float[] zRates;
	float[] yRates;
	float[] xRates;
	float[] zSpins;
	float[] ySpins;
	float[] xSpins;

	int startingRadius = 500;
	int radiusIncrement = 4;
	int sphereCount = 0;
	int counter = 90;
	int sphereCounter = 0;

	float cap = -50;
	float maxCap = 10;
	float minDistance = 1;
	float maxDistance = 1700;
	float distance = 0;
	double lookX;
	double lookY;
	double lookZ;
	boolean animateColors = false;
	boolean changeColors = false;

	PeasyCam cam;
	PApplet app;

	public HangOnVisual(PApplet app) {
		this.app = app;
	}

	@Override
	public void setup() {
		meshRenderer = new WB_Render(app);
		spheres = new HE_Mesh[numSpheres];
		sphereColors = new int[numSpheres];
		zRates = new float[numSpheres];
		yRates = new float[numSpheres];
		xRates = new float[numSpheres];
		zSpins = new float[numSpheres];
		ySpins = new float[numSpheres];
		xSpins = new float[numSpheres];

		createSpheres();
		cam = new PeasyCam(app, minDistance);
		distance = minDistance;
		cam.setMinimumDistance(minDistance);
		cam.setMaximumDistance(maxDistance);
	
	}

	public void keyPressed(int keyCode) {
		System.out.println(keyCode);
	}

	void createSpheres() {
		HEC_Creator creator;
		sphereCount = 0;
		for (int i = 0; i < numSpheres; i++) {
			// Create
			creator = new HEC_Sphere().setRadius(startingRadius + sphereCount * radiusIncrement).setUFacets(7).setVFacets(7);
			spheres[i] = new HE_Mesh(creator);

			// Lattice & Cap
			if (i > 0) {
				// TODO figure out way to incorporate lattice in a performant way
				spheres[i].modify(new HEM_Lattice().setDepth(1).setWidth(5).setThresholdAngle(PApplet.radians(90)).setFuse(true));
				spheres[i].modify(new HEM_Slice().setCap(true).setPlane(new WB_Plane(new WB_Point3d(0, -25, 0), new WB_Vector3d(0, 1, 0))));
				spheres[i].modify(new HEM_Slice().setCap(true).setPlane(new WB_Plane(new WB_Point3d(0, 25, 0), new WB_Vector3d(0, -1, 0))));
			}
			sphereColors[i] = app.color(i * 5 + 10, i * 4 + app.random(20), 40 + app.random(40), 255);
			sphereCount++;
		}

	}

	@Override
	public void draw(PGraphics g) {
		app.background(0, 0, 0);
		app.lights();
		cam.feed();

		for (int i = 0; i < numSpheres; i++) {
			app.pushMatrix();
			app.noStroke();

			// Outer
			if (i == 0) {
				app.fill(app.color(255, 255, 255, 255));
			} else {
				zSpins[i] += zRates[i] * multiplier;
				ySpins[i] += yRates[i] * multiplier;
				xSpins[i] += xRates[i] * multiplier;
				app.fill(sphereColors[i]);
				app.rotateZ(PApplet.radians(zSpins[i]));
				app.rotateY(PApplet.radians(ySpins[i]));
				app.rotateX(PApplet.radians(xSpins[i]));
				drawSphere(i);
			}

			app.popMatrix();
		}

		if (animateColors) {
			int tempColor = sphereColors[numSpheres - 1];
			for (int i = numSpheres - 1; i > 0; i--) {
				sphereColors[i] = sphereColors[i - 1];

				if (changeColors)
					sphereColors[i] = sphereColors[i] + 1;
			}
			sphereColors[0] = tempColor;
		}
	}

	void drawSphere(int sphereNum) {
		meshRenderer.drawFaces(spheres[sphereNum]);
	}

	/*
	 * @Override public void keyPressed() { if (key == '1') { splode(); } else
	 * if (key == '2') { stopAnimation(); } else if (key == '3') {
	 * initSpinRates(); } else if (key == 'b') { cam.setDistance(200); }
	 * 
	 * }
	 */


	// Events
	void splode() {
		// stopAnimation();
		// cam.setDistance(minDistance);
		for (int i = 0; i < numSpheres; i++) {
			ySpins[i] = 0;
			zSpins[i] = 0;
			xSpins[i] = 0;
		}
	}

	public void reset() {
		stopAnimation();
		cam.setDistance(minDistance);
		for (int i = 0; i < numSpheres; i++) {
			ySpins[i] = 0;
			zSpins[i] = 0;
			xSpins[i] = 0;
		}
	}

	void stopAnimation() {
		for (int i = 0; i < numSpheres; i++) {
			yRates[i] = 0;
			zRates[i] = 0;
			xRates[i] = 0;
		}
	}

	void initSpinRates() {
		for (int i = 0; i < numSpheres; i++) {
			zRates[i] = app.random(4) - 2;
			yRates[i] = app.random(4) - 2;
			xRates[i] = app.random(4) - 2;
		}
	}

	@Override
	public void dragEvent(int eventType, float amount) {
		if (eventType == 0 || eventType == 2)
			cam.setDistance(amount * maxDistance);

	}

	@Override
	public void tapEvent(int eventType, boolean isTapDown) {
		// TODO Auto-generated method stub

	}

	@Override
	public void noteObjEvent(int note, float velocity) {
		//System.out.println(msg.get(0).intValue());
		if (note == 1 && velocity > 1) {
			initSpinRates();
		} else if (note == 0 && velocity > 1) {
			reset();
		} else if (note == 2 && velocity > 1) {
			splode();
		} else if (note == 74 && velocity > 1) {
			animateColors = true;
		} else if (note == 75 && velocity > 1) {
			changeColors = true;
		} else if (note == 76 && velocity > 1) {
			//Stop animation
			multiplier = 0;
		} else if (note == 40 && velocity > 1)
		{
			//Position 1
			distance = 1674.479f;
			cam.pan(-232.06978, 180);
			cam.setRotations(0.912115, 1.397302, -1.304);
		} else if (note == 41 && velocity > 1)
		{
			//Position 2
			distance = 1118.2524f;
			cam.pan(-678.7046, 195.31929);
			cam.setRotations(1.2905856, 1.1353962, -0.17412537);
		} else if (note == 42 && velocity > 1)
		{
			//Position 3
			distance = -312.00345f;
			cam.pan(2.1443694, 337.60132);
			cam.setRotations(-0.0063516945, -0.7460032, 3.0279074);
		} else if (note == 43 && velocity > 1)
		{
			//Position 4
			distance = 169.03586f;
			cam.pan(112.044716, -557.98566);
			cam.setRotations(-2.943426, 0.28871202, -1.9056232);
		} else if (note == 44 && velocity > 1)
		{
			//Position 5
			distance = 943.98868f;
			cam.pan(-799.4451, 373.15527);
			cam.setRotations(1.134086, 0.37177035, 0.57470137);
		}

	}

	@Override
	public void ctrlEvent(int num, float val) {
			if (num == 1) {
				// spin rate
				multiplier = (val * 8.0f) - 4.0f;
			} else if (num == 2) {
				// zoom distance
				distance = (val * maxDistance);
			} else if (num == 3) {
				// look X
				lookX = (val * 2400) - 1200;
			} else if (num == 4) {
				// look Y
				lookY = (val * 2400) - 1200;
			} else if (num == 5) {
				// look Z
				lookZ = (val * app.height);
			}
		
	}

	@Override
	public void camEvent(int note) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scale(float amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotateX(float amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotateY(float amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotateZ(float amount) {
		// TODO Auto-generated method stub
		
	}

}
