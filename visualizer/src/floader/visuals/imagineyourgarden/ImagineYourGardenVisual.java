package floader.visuals.imagineyourgarden;

import floader.looksgood.ani.Ani;
import floader.visuals.IVisual;
import peasy.PeasyCam;
import processing.core.PApplet;
import wblut.hemesh.modifiers.*;
import wblut.hemesh.creators.*;
import wblut.geom.core.WB_Line;
import wblut.geom.core.WB_Point3d;
import wblut.geom.core.WB_Vector3d;
import oscP5.*;
import wblut.hemesh.core.*;
import wblut.core.processing.*;

@SuppressWarnings("serial")
public class ImagineYourGardenVisual implements IVisual {

	// Meshes
	HE_Mesh[] meshes;
	int numMeshes = 2;
	WB_Render meshRenderer;
	PeasyCam cam;

	// Cylinder colors
	int outerAlpha = 255;
	int innerAlpha = 180;
	int outerR = 255;
	int outerG = 0;
	int outerB = 255;
	int innerR = 0;
	int innerG = 255;
	int innerB = 255;

	boolean facesOn = true; // toggle display of faces
	boolean edgesOn = true; // toggle display of edges
	float shapeHue = 57; // default hue
	float shapeSaturation = 100; // default saturation
	float shapeBrightness = 96; // default brightness
	float[] shapeTransparency; // default transparency
	int counter;
	HE_Selection selection;
	float rotateXAmt;
	float rotateYAmt;
	boolean flip = false;
	boolean rotateXDir = true;;
	boolean randomizeDir = true;
	float cylHeight = 25000;

	float rotDuration = 4;
	float origRotDuration = rotDuration;
	float maxRotDuration = 1;
	float rotateY;

	float zoomAmt = -(cylHeight - 10000) / 2;
	float origZoomDuration = 10;
	float minZoomDuration = 2;
	float maxZoomDuration = 20;

	boolean reset = false;
	boolean change = false;
	boolean twistBoost;
	float alphaMult = 1;
	HEM_Extrude extrude = new HEM_Extrude();

	int cameraY = 0;
	int maxCameraY = 360;
	int cameraX = 0;
	int maxCameraX = 360;

	float twistAmt = 0;
	float maxTwistAmt = 1f;
	int maxColor = 255;

	float totalTwistAmt = 0;

	PApplet app;
	Ani rotateAni;
	Ani zoomAni;

	public ImagineYourGardenVisual(PApplet app) {
		this.app = app;
	}

	@Override
	public void setup() {
		cam = new PeasyCam(app, 100);
		cam.setMinimumDistance(100);
		cam.setMaximumDistance(600);
		meshRenderer = new WB_Render(app);
		meshes = new HE_Mesh[numMeshes];
		app.colorMode(PApplet.RGB, 255, 255, 255, 255);
		app.noStroke();
		createMeshes();
		rotateAni = new Ani(this, rotDuration, "rotateXAmt", 360);
		rotateAni.repeat();
		rotateAni.start();
		zoomAni = new Ani(this, origZoomDuration, "zoomAmt", (cylHeight - 12000) / 2);
		zoomAni.repeat();
		zoomAni.start();
	}

	public void keyPressed(int keyCode) {
		System.out.println(keyCode);
	}

	@Override
	public void draw() {
		// app.background(255,255,255);
		cam.feed();
		//app.lights();
		// System.out.println(cam.getLookAt()[0] + ", " + cam.getLookAt()[1] +
		// ", " + cam.getLookAt()[2]);

		totalTwistAmt += twistAmt;
		if (reset) {
			//createMeshes();
			reset = false;
			twistAmt = 0;
		}

		app.translate(0, 0, zoomAmt);
		app.rotateZ(PApplet.radians(rotateXAmt));
		drawMesh(0);
		drawMesh(1);
	}

	void drawMesh(int meshIndex) {
		if (meshIndex == 0)
			app.fill(outerR, outerG, outerB, outerAlpha * alphaMult);
		else
			app.fill(innerR, innerG, innerB, innerAlpha * alphaMult);

		meshes[meshIndex].modify(new HEM_Twist().setAngleFactor((twistAmt)).setTwistAxis(new WB_Line(new WB_Point3d(0, 0, 0), new WB_Vector3d(0, 0, 1))));
		meshRenderer.drawFaces(meshes[meshIndex]);
	}

	void createMesh(int meshIndex, float radius, int steps) {
		HEC_Creator creator = new HEC_Cylinder().setRadius(radius).setHeight(cylHeight).setFacets(15).setSteps(steps);
		meshes[meshIndex] = new HE_Mesh(creator);
		// Lattice
		meshes[meshIndex].modify(new HEM_Lattice().setDepth(1).setWidth(40).setThresholdAngle(PApplet.radians(45)));
	}

	void reset() {
		reset = true;
		alphaMult = 1;
		rotateAni.setDuration(origRotDuration);
		createMeshes();
	}

	void createMeshes() {
		createMesh(0, 210, 30);
		createMesh(1, 200, 30);
	}

	@Override
	public void dragEvent(int eventType, float amount) {
		if (eventType == 0 || eventType == 2) {
			float delta = rotateY - (amount * 180);
			cam.rotateY(PApplet.radians(delta));
			rotateY -= delta;
		}
	}

	@Override
	public void tapEvent(int eventType, boolean isTapDown) {
		// TODO Auto-generated method stub
		if(isTapDown)twistAmt = maxTwistAmt;
		else if(!isTapDown){reset();twistAmt=0;}
		
	}

	@Override
	public void noteEvent(int note, int velocity, int channel) {

		// RESET
		if (note == 0 && velocity != 0) {
			reset();
		}

	}

	@Override
	public void ctrlEvent(int num, int val, int chan) {
			// Color 1
			 if (num == 2) {
				innerAlpha = (int) (val / 127.0 * 255);
			} else if (num == 3) {
				outerAlpha = (int) (val / 127.0 * 255);
			} else if (num == 6) {
				rotDuration = (val / 127.0f * (float) maxRotDuration);
			}
		
	}

}
