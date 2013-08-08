package floader.visuals.hangon;

import java.util.Iterator;

import oscP5.OscMessage;

import floader.looksgood.ani.Ani;
import floader.visuals.AbstractVisual;
import floader.visuals.IVisual;
import floader.visuals.VisualConstants;

import wblut.geom.*;
import wblut.processing.*;
import wblut.hemesh.*;
import peasy.*;
import processing.core.PApplet;

@SuppressWarnings("serial")
public class AvanteHangOnVisual extends AbstractVisual implements IVisual {

	float multiplier = 1;

	int numSpheres = 10;
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
	int radiusIncrement = 10;
	int sphereCount = 0;
	int counter = 90;
	int sphereCounter = 0;

	float cap = -50;
	float maxCap = 10;
	float minDistance = 1;
	float maxDistance = 2000;
	float distance = 0;
	double lookX;
	double lookY;
	double lookZ;
	boolean animateColors = false;
	boolean changeColors = false;
	boolean isTapped = false;
	float r;
	float g;
	float b;
	Object lock;

	Ani noiseAni;
	float noise = 0;
	float maxNoise = 100;
	float initNoiseDuration = .4f;
	Ani skewAni;
	float skew = 0;
	float maxSkew = -2;
	
	boolean lights = false;

	PApplet app;
	
	
	public AvanteHangOnVisual(PApplet app) {
		this.app = app;
		lock = new Object();
		camStatePath = "data\\avantehangon\\camState";
	}

	@Override
	public void setup() {
		app.lights();
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

		// TODO put back to min distance
		cam = new PeasyCam(app, minDistance);
		distance = minDistance;
		cam.setMinimumDistance(minDistance);
		cam.setMaximumDistance(maxDistance);
		cam.setActive(false);

		noise = maxNoise;
		noiseAni = new Ani(this, initNoiseDuration, "noise", 0);
		noiseAni.pause();
		noise = 0;
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
			// spheres[i].modify(new
			// HEM_Lattice().setDepth(1).setWidth(5).setThresholdAngle(PApplet.radians(90)).setFuse(true));
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

	@Override
	public void draw() {
		app.background(r, g, b);
		app.noStroke();
		if (lights)
			app.lights();
		cam.feed();

		synchronized (lock) {
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
	}

	void drawSphere(int index) {
		spheres[index].modify(new HEM_Noise().setDistance(noise));
		meshRenderer.drawFaces(spheres[index]);
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

		/*
		 * for (int i = 0; i < numSpheres; i++) spheres[i].modify(new
		 * HEM_Skew().setSkewDirection(1, 0, 0).setGroundPlane(new WB_Plane(1,
		 * 0, 0, 0, 0, 200)).setSkewFactor(amount * -20)); isTapped = false;
		 */

		if (eventType == 0 || eventType == 2) {
			r = (1 - amount) * 110;
			g = (1 - amount) * 150;
			b = (1 - amount) * 132;
		}

	}

	@Override
	public void tapEvent(int eventType, boolean isTapDown) {

		/*if (isTapDown) {
			cam.setDistance(minDistance, 0);
			lights = false;
		} else {
			cam.setDistance(maxDistance, 0);
			lights = true;
		}*/

	}

	@Override
	public void noteObjEvent(int note, int vel) {

		if(lights)
		{
			if (note == 0 && vel >0) {
				synchronized (lock) {
					createSpheres();
				}
				noiseAni.setDuration(initNoiseDuration);
				noiseAni.start();
			} else if(note == 1 && vel >1)
			{
				synchronized (lock) {
					createSpheres();
				}
				noiseAni.setDuration(2.2f);
				noiseAni.start();
			}
		}
	}

	@Override
	public void ctrlEvent(int num, float val, int chan) {
		// TODO Auto-generated method stub

	}

	@Override
	public void camEvent(int note) {
		if(note>0)lights=true;
		loadCamState(note);
		//writeCamState();
	}

	@Override
	public void toggleBackgroundFill() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cycleColorScheme() {
		// TODO Auto-generated method stub
		
	}
}
