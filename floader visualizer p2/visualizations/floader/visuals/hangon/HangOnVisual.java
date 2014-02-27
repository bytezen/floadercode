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
import processing.core.PGraphics;

@SuppressWarnings("serial")
public class HangOnVisual extends AbstractVisual {

	float multiplier = 1;

	int numSpheres = 35;
	WB_Render meshRenderer;
	HE_Mesh[] spheres;
	int[] sphereColors;
	
	int noise;
	int maxNoise = 3;

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
	int colorOffset = 0;

	
	boolean changeColors = false;
	
	PApplet app;

	public HangOnVisual(PApplet app) {
		this.app = app;
	}

	@Override
	public void setup() {
		super.setup();
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
		initSpinRates();

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
				
		for (int i = 0; i < numSpheres; i++) {
			g.pushMatrix();
			g.noStroke();

			// Outer
			if (i == 0) {
				g.fill(g.color(255, 255, 255, 255));
			} else {
				zSpins[i] += zRates[i] * multiplier;
				ySpins[i] += yRates[i] * multiplier;
				xSpins[i] += xRates[i] * multiplier;
				g.fill(curColorScheme.getColor((i + colorOffset) % curColorScheme.getLength())
						.getRGB(), curColorScheme.getColor(i % curColorScheme.getLength())
						.getAlpha());
				g.rotateZ(PApplet.radians(zSpins[i]));
				g.rotateY(PApplet.radians(ySpins[i]));
				g.rotateX(PApplet.radians(xSpins[i]));
				
				if(noise>0)spheres[i].modify(new HEM_Noise().setDistance(noise));
				drawSphere(i);
			}

			g.popMatrix();
		}

		/*if (animateColors) {
			int tempColor = sphereColors[numSpheres - 1];
			for (int i = numSpheres - 1; i > 0; i--) {
				sphereColors[i] = sphereColors[i - 1];

				if (changeColors)
					sphereColors[i] = sphereColors[i] + 1;
			}
			sphereColors[0] = tempColor;
		}*/
	}

	void drawSphere(int sphereNum) {
		meshRenderer.drawFaces(spheres[sphereNum]);
	}

	// Events
	void splode() {
		for (int i = 0; i < numSpheres; i++) {
			ySpins[i] = 0;
			zSpins[i] = 0;
			xSpins[i] = 0;
		}
	}

	public void reset() {
		createSpheres();
		noise = 0;
		stopAnimation();
		for (int i = 0; i < numSpheres; i++) {
			ySpins[i] = 0;
			zSpins[i] = 0;
			xSpins[i] = 0;
		}
		initSpinRates();
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
		}

	}

	public void ctrlEvent(int index, float val) {
		
			switch(index)
			{
			case VisualConstants.LOCAL_EFFECT_1:
				// spin rate
				multiplier = (val * 8.0f) - 4.0f;
				break;
			case VisualConstants.LOCAL_EFFECT_2:
				noise = (int)(val * maxNoise);
				break;
			case VisualConstants.LOCAL_EFFECT_3:
				for (int i = 0; i < numSpheres; i++) {
					ySpins[i] = 0;
					zSpins[i] = 0;
					xSpins[i] = 0;
				}
				initSpinRates();
				break;
			case VisualConstants.LOCAL_EFFECT_4:
	
				break;
			default:
				System.err.println("Unrecognized effect " + index + " sent to HangOnVisuals ctrlEvent");
				break;
			}
	}

}
