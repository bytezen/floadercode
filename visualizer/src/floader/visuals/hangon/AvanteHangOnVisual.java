package floader.visuals.hangon;

import java.util.Iterator;

import oscP5.OscMessage;

import floader.visuals.IVisual;

import wblut.hemesh.modifiers.*;
import wblut.hemesh.core.*;
import wblut.hemesh.creators.*;
import wblut.hemesh.*;
import wblut.geom.*;
import wblut.geom.core.WB_Plane;
import wblut.geom.core.WB_Point3d;
import wblut.geom.core.WB_Vector3d;
import wblut.core.processing.*;
import peasy.*;
import processing.core.PApplet;

@SuppressWarnings("serial")
public class AvanteHangOnVisual implements IVisual {

	float multiplier = 1;

	int numSpheres = 20;
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
	int radiusIncrement = 40;
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

	public AvanteHangOnVisual(PApplet app) {
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

	void createSpheres() {
		HEC_Creator creator;
		sphereCount = 0;
		for (int i = 0; i < numSpheres; i++) {
			// Create
			creator = new HEC_Sphere().setRadius(startingRadius + sphereCount * radiusIncrement).setUFacets(7).setVFacets(7);
			spheres[i] = new HE_Mesh(creator);

			// Lattice & Cap
			// TODO figure out way to incorporate lattice in a performant way
			//spheres[i].modify(new HEM_Lattice().setDepth(1).setWidth(5).setThresholdAngle(PApplet.radians(90)).setFuse(true));
			spheres[i].modify(new HEM_Slice().setCap(true).setPlane(new WB_Plane(new WB_Point3d(0, -25, 0), new WB_Vector3d(0, 1, 0))));
			spheres[i].modify(new HEM_Slice().setCap(true).setPlane(new WB_Plane(new WB_Point3d(0, 25, 0), new WB_Vector3d(0, -1, 0))));

			if ((i + 1) % 2 == 0) {
				// teal
				sphereColors[i] = app.color(110, 150, 132, 255);
			} else if ((i + 1) % 3 == 0) {
				// yellow
				sphereColors[i] = app.color(214, 212, 100, 255);
			} else {
				// gray
				sphereColors[i] = app.color(120, 120, 100, 255);
			}

			sphereCount++;
		}
		initSpinRates();

	}
	
	public void keyPressed(int keyCode)
	{
		System.out.println(keyCode);
	}

	@Override
	public void draw() {
		app.background(0,0,0);
		app.noStroke();
		app.lights();
		cam.feed();

		for (int i = 0; i < numSpheres; i++) {
			app.pushMatrix();

			zSpins[i] += zRates[i] * multiplier;
			ySpins[i] += yRates[i] * multiplier;
			xSpins[i] += xRates[i] * multiplier;
			
			app.rotateZ(PApplet.radians(zSpins[i]));
			app.rotateY(PApplet.radians(ySpins[i]));
			app.rotateX(PApplet.radians(xSpins[i]));
			
			app.fill(sphereColors[i]);
			drawSphere(i);

			app.popMatrix();
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

	void reset() {
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
		cam.setDistance(amount * maxDistance);
		
	}

	@Override
	public void tapEvent(int eventType, boolean isTapDown) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noteEvent(int note, int velocity, int channel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctrlEvent(int num, int val, int chan) {
		// TODO Auto-generated method stub
		
	}
}
