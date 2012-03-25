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
public class HangOnVisual implements IVisual {

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
	
	public HangOnVisual(PApplet app)
	{
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

			//Lattice & Cap
			if (i > 0) {
				//TODO figure out way to incorporate lattice in a performant way
				//spheres[i].modify(new HEM_Lattice().setDepth(1).setWidth(5).setThresholdAngle(PApplet.radians(90)).setFuse(true));
				spheres[i].modify(new HEM_Slice().setCap(true).setPlane(new WB_Plane(new WB_Point3d(0, -25, 0), new WB_Vector3d(0, 1, 0))));
				spheres[i].modify(new HEM_Slice().setCap(true).setPlane(new WB_Plane(new WB_Point3d(0, 25, 0), new WB_Vector3d(0, -1, 0))));
			}
			sphereColors[i] = app.color(i * 5 + 10, i * 4 + app.random(20), 40 + app.random(40), 255);
			sphereCount++;
		}
		

	}

	@Override
	public void draw() {
		app.lights();
		cam.feed();
		
		for (int i = 0; i < numSpheres; i++) {
			app.pushMatrix();
			app.noStroke();
			
			//Outer
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
		
		if(animateColors)
		{
			int tempColor = sphereColors[numSpheres-1];
			for (int i = numSpheres-1; i >0; i--) {
				sphereColors[i]=sphereColors[i-1]; 
				
				if(changeColors)sphereColors[i] = sphereColors[i] +1;
			}
			sphereColors[0] = tempColor;
		}
	}

	void drawSphere(int sphereNum) {
		meshRenderer.drawFaces(spheres[sphereNum]);
	}

/*	@Override
	public void keyPressed() {
		if (key == '1') {
			splode();
		} else if (key == '2') {
			stopAnimation();
		} else if (key == '3') {
			initSpinRates();
		} else if (key == 'b') {
			cam.setDistance(200);
		} 
			
	}*/

	public void oscEvent(OscMessage msg) {
		if (msg.checkAddrPattern("/mtn/note")) {
			//System.out.println(msg.get(0).intValue());
			if (msg.get(0).intValue() == 1 && msg.get(1).intValue() > 1) {
				initSpinRates();
			} else if (msg.get(0).intValue() == 0 && msg.get(1).intValue() > 1) {
				reset();
			} else if (msg.get(0).intValue() == 2 && msg.get(1).intValue() > 1) {
				splode();
			} else if (msg.get(0).intValue() == 74 && msg.get(1).intValue() > 1) {
				animateColors = true;
			} else if (msg.get(0).intValue() == 75 && msg.get(1).intValue() > 1) {
				changeColors = true;
			} else if (msg.get(0).intValue() == 76 && msg.get(1).intValue() > 1) {
				//Stop animation
				multiplier = 0;
			} else if (msg.get(0).intValue() == 40 && msg.get(1).intValue() > 1)
			{
				//Position 1
				distance = 1674.479f;
				cam.pan(-232.06978, 180);
				cam.setRotations(0.912115, 1.397302, -1.304);
			} else if (msg.get(0).intValue() == 41 && msg.get(1).intValue() > 1)
			{
				//Position 2
				distance = 1118.2524f;
				cam.pan(-678.7046, 195.31929);
				cam.setRotations(1.2905856, 1.1353962, -0.17412537);
			} else if (msg.get(0).intValue() == 42 && msg.get(1).intValue() > 1)
			{
				//Position 3
				distance = -312.00345f;
				cam.pan(2.1443694, 337.60132);
				cam.setRotations(-0.0063516945, -0.7460032, 3.0279074);
			} else if (msg.get(0).intValue() == 43 && msg.get(1).intValue() > 1)
			{
				//Position 4
				distance = 169.03586f;
				cam.pan(112.044716, -557.98566);
				cam.setRotations(-2.943426, 0.28871202, -1.9056232);
			} else if (msg.get(0).intValue() == 44 && msg.get(1).intValue() > 1)
			{
				//Position 5
				distance = 943.98868f;
				cam.pan(-799.4451, 373.15527);
				cam.setRotations(1.134086, 0.37177035, 0.57470137);
			}
		} else if (msg.checkAddrPattern("/mtn/ctrl")) {
			if (msg.get(0).intValue() == 1) {
				//spin rate
				multiplier = ((float) msg.get(1).intValue() / 127.0f * 8.0f) - 4.0f;
			} else if(msg.get(0).intValue() == 2) {
				//zoom distance
				distance = ((float) msg.get(1).intValue() / 127.0f * maxDistance);
			} else if(msg.get(0).intValue() == 3) {
				//look X
				lookX = ((double) msg.get(1).intValue() / 127.0 * 2400) - 1200;
			} else if(msg.get(0).intValue() == 4) {
				//look Y
				lookY = ((double) msg.get(1).intValue() / 127.0 * 2400) - 1200;
			} else if(msg.get(0).intValue() == 5) {
				//look Z
				lookZ = ((double) msg.get(1).intValue() / 127.0 * app.height);
			}
		}
	}

	// Events
	void splode() {
		//stopAnimation();
		//cam.setDistance(minDistance);
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
}
