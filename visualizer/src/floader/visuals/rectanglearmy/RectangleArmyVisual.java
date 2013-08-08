package floader.visuals.rectanglearmy;

import java.awt.Color;
import java.util.Iterator;

import floader.visuals.IVisual;
import floader.visuals.VisualConstants;
import wblut.hemesh.*;
import wblut.processing.*;
import oscP5.*;
import peasy.*;
import processing.core.*;
import floader.visuals.colorschemes.*;
import floader.looksgood.ani.Ani;

@SuppressWarnings("serial")
public class RectangleArmyVisual implements IVisual {
	// Meshes
	WB_Render meshRenderer;
	HEC_Creator creator;
	
	int numRows = 25;
	int numCols = 25;
	int RESET = 0;
	
	ColorScheme curColorScheme;

	boolean fillBackground = true;
	boolean readyDraw = false;
	float maxVertexDistance = 400;
	float vertexDistance = 10;
	float maxPerspectiveWidth = 5000;
	float perspectiveWidth;
	float maxBendAmount = 1000;
	float bendAmount;
	float explodeAmount;
	
	// presentation
	int bgcolor; // background color
	Color shapecolor; // shape color
	boolean facesOn = true; // toggle display of faces
	boolean edgesOn = true; // toggle display of edges
	float shapeHue = 57; // default hue
	float shapeSaturation = 100; // default saturation
	float shapeBrightness = 96; // default brightness
	float[] shapeMult;
	int[][] counter;
	float ax, ay;
	boolean clock = false;
	boolean eighth = true;
	int maxCounter = 11;
	HE_Selection selection;
	Iterator<HE_Face> fItr;
	HE_Face f;
	PeasyCam cam;
	CameraState camState;
	float minDistance = 400;
	float initDistance = 700;
	float maxDistance = 2000;

	float rotateZ = 0;
	float maxRotateZ = .4f;
	float rotateIndividual = 0;
	float maxRotateIndividual = .4f;
	float rotateForward = 0;
	float maxRotateForward = 1;
	float rotateCombine = 0;
	float maxRotateCombine = .4f;
	float rotateCrazy = 0;
	float maxRotateCrazy = 1;
	float rotateY;

	PApplet app;

	public RectangleArmyVisual(PApplet app) {
		this.app = app;
	}

	public void setup() {
		curColorScheme = new MyColorScheme();
		Ani.init(app);
		
		meshRenderer = new WB_Render(app);
		
		app.noStroke();
		cam = new PeasyCam(app, initDistance);
		cam.setMinimumDistance(minDistance);
		cam.setMaximumDistance(maxDistance);
		cam.setActive(VisualConstants.CAM_ENABLED);
		reset();
	}
	
	int panYAmount = 5;
	int totalYPan = 0;
	int panXAmount = 5;
	int totalXPan = 0;
	boolean scroll = false;
	boolean rotate = false;
	boolean enableMouse = true;
	boolean addLattice = false;

	public void draw() {
		if(fillBackground)
			app.background(curColorScheme.getBgColor().getRGB());
		
		app.lights();
		cam.feed();

		//need to redo
		if (scroll) {
			if (totalYPan >= 300) {
				totalYPan = 0;
				app.translate(0, -300);
			} else {
				totalYPan += panYAmount;
				app.translate(0, totalYPan);
			}
		}
		
		
		creator = new HEC_Grid().setUSize(110).setVSize(110).setU(2).setV(2);
		HE_Mesh rect = new HE_Mesh(creator);
		
		app.pushMatrix();
			app.rotateZ(PApplet.radians(rotateZ));
		
			for (int j = 0; j < numCols; j++)
				for (int k = 0; k < numRows; k++) {
					app.fill(curColorScheme.getColor(j % curColorScheme.getLength()).getRGB());
		
					app.pushMatrix();
						app.translate(j * 150 - (numCols * 150 / 2), k * 150 - (numRows * 150 / 2));
						app.rotateZ(PApplet.radians(rotateIndividual));
						
						//Explode!
						rect.rotateAboutAxis(PApplet.radians(explodeAmount), numCols / 2 * 150, numRows / 2 * 150, 0, numCols / 2 * 100, numRows / 2 * 150, 0);
						
						// Crazy flying rotate
						//rect.rotateAboutAxis(rotateCrazy, -j / 4 * 150 + numCols / 2 * 150, k / 4 * 150 + numRows / 2 * 150, 0, j / 4 * 150 + numCols / 2 * 150, k / 4 * 150 + numRows / 2 * 150, 1);
						// Rotate forward
						//rect.rotateAboutAxis(rotateForward, meshes[j][k].getVerticesAsPoint()[0], meshes[j][k].getVerticesAsPoint()[1]);
						
						meshRenderer.drawFaces(rect);
					app.popMatrix();
				}

		app.popMatrix();
	}

	public void reset() {

		cam.reset(0);
		cam.pan(0, 0);
		rotateZ = 0;
		rotateIndividual = 0;
		rotateForward = 0;
		rotateCrazy = 0;
		rotateCombine = 0;
		explodeAmount = 0;
		rotate = false;
		scroll = false;
	}

	/*
	 * @Override public void keyPressed() { if(key=='1') { popMesh(50); } else
	 * if(key=='2') { popMesh(-50); } else if(key=='3') { addLattice = true; }
	 * else if(key == 's') { scroll = !scroll; } else if(key == 'r') { rotate =
	 * !rotate; } else if(key == 'x') { reset(); } else if(key == '9') {
	 * cam.rotateZ(radians(90)); } }
	 */

	@Override
	public void dragEvent(int eventType, float amount) {
		switch (eventType) {
		case 2:
			rotateIndividual = (1 - amount) * maxRotateIndividual - (maxRotateIndividual / 2);
			break;
		// Right box Y
		case 3:
			rotateZ = (1 - amount) * maxRotateZ - (maxRotateZ / 2);
			break;
		}
	}

	@Override
	public void tapEvent(int eventType, boolean isTapDown) {
/*		if(eventType == 0 && isTapDown)
			scroll = !scroll;*/
	}

	@Override
	public void noteObjEvent(int note, int vel) {
		
			if (note == 0 && vel != 0) {
				rotateForward = .03f;
			} else if(note==0 && vel == 0)
				rotateForward = -.03f;
	}

	@Override
	public void ctrlEvent(int num, float val, int chan) {
		//Rotate individual amount
		if (num == 0) {
			rotateIndividual = val * 360 - (360 / 2);
		} else
		//Rotate cam Z amount
		if (num == 1) {
			rotateZ = val * 360 - (360 / 2);
		} else
		//Explode
		if (num == 2) {
			explodeAmount = val * 360 - (360 / 2);
		} else
		//Rotate combine amount
		if (num == 3) {
			//rotateCombine = val * maxRotateCombine - (maxRotateCombine / 2);
		} else
		//Rotate crazy amount
		if (num == 4) {
			//rotateCrazy = val * maxRotateCrazy - (maxRotateCrazy / 2);
		}

	}

	@Override
	public void camEvent(int note) {
			switch (note) {
			case 0:
				cam.setDistance(minDistance);
				break;
			case 1:
				cam.setDistance(maxDistance);
				break;
			case 2:
				cam.setDistance((minDistance + maxDistance) / 2);
				break;
			}
	}

	@Override
	public void toggleBackgroundFill() {
		fillBackground = !fillBackground;	
	}

	@Override
	public void cycleColorScheme() {
		// TODO Auto-generated method stub
		
	}

}
