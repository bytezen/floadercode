package floader.visuals.kalimba;

import java.util.Iterator;

import floader.visuals.AbstractVisual;
import floader.visuals.IVisual;
import oscP5.*;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PGraphics;
import wblut.geom.*;
import wblut.processing.*;
import wblut.hemesh.*;

@SuppressWarnings("serial")
public class KalimbaVisual extends AbstractVisual implements IVisual {
	// Meshes
	HE_Mesh[][] meshes;
	WB_Render meshRenderer;
	int curMesh = 0;
	int numRows = 1;
	int numCols = 3;
	float meshPadding = 450;
	float rotateAmount = 0;
	float rotateMax = .15f;

	boolean readyDraw = false;
	float maxVertexDistance = 400;
	float vertexDistance = 0;
	float maxBendAmount = 1000;
	float bendAmount;
	int[][] buttons;

	// presentation
	int shapecolor; // shape color
	boolean facesOn = true; // toggle display of faces
	boolean edgesOn = true; // toggle display of edges
	float shapeHue = 57; // default hue
	float shapeSaturation = 100; // default saturation
	float shapeBrightness = 96; // default brightness
	int[][] counter;
	float ax, ay;
	boolean clock = false;

	PApplet app;

	public KalimbaVisual(PApplet app) {
		this.app = app;
		camStatePath = "data\\kalimba\\camState";
	}

	public void setup() {
		meshRenderer = new WB_Render(app);
		meshes = new HE_Mesh[numCols][numRows];
		counter = new int[numCols][numRows];
		buttons = new int[numCols][numRows];
		app.noStroke();
		createDots();

		cam = new PeasyCam(app, 0);
		cam.setMaximumDistance(1000);
		cam.setDistance(600);
		cam.setActive(false);
	}

	void createDots() {
		for (int j = 0; j < numRows; j++)
			for (int k = 0; k < numCols; k++) {
				createDot(k, j);
			}
	}

	void createDot(int col, int row) {
		HEC_Creator creator;
		float x = 0;
		float y = 0;
		x = col * (meshPadding) - ((col + 1) * meshPadding / 2);

		// x = (col) * (500 / (numCols+1));
		y = (row) * (500 / (numRows + 1));
		creator = new HEC_Sphere().setRadius(6).setUFacets(7).setVFacets(7).setCenter(x, y, 0);
		meshes[col][row] = new HE_Mesh(creator);

	}

	public void draw(PGraphics g) {
		app.background(0);
		cam.feed();
		app.rotateX(app.random(0, rotateAmount));
		app.rotateY(app.random(0, rotateAmount));
		app.lights();

		HE_Selection selection;
		Iterator<HE_Face> fItr;
		HE_Face f;

		for (int j = 0; j < numRows; j++)
			for (int k = 0; k < numCols; k++) {

				shapecolor = app.color(240, 20, 240, 255);
				app.fill(shapecolor);
				app.pushMatrix();
				if (buttons[k][j] == 1) {
					// Vertex Expansion

					fItr = meshes[k][j].fItr();
					selection = new HE_Selection(meshes[k][j]);
					while (fItr.hasNext()) {
						f = fItr.next();
						if (app.random(100) < 4) {
							selection.add(f);
						}
					}
					meshes[k][j].modifySelected(new HEM_VertexExpand().setDistance(app.random(0, (4) * 10)), selection);
					if (--counter[k][j] <= 0) {
						buttons[k][j] = 0;
					}
				} else {
					createDot(k, j);
					counter[k][j] = 0;
				}
				meshRenderer.drawFaces(meshes[k][j]);
				meshRenderer.drawEdges(meshes[k][j]);

				app.popMatrix();
			}

	}

	@Override
	public void dragEvent(int eventType, float amount) {
		if (eventType == 0 || eventType == 2)
			rotateAmount = rotateMax - (rotateMax * amount);
	}

	@Override
	public void tapEvent(int eventType, boolean isTapDown) {
		for (int j = 0; j < numRows; j++)
			for (int k = 0; k < numCols; k++) {
				buttons[k][j] = 1;
				counter[k][j] = 20;
			}
	}

	@Override
	public void noteObjEvent(int note, int velocity) {
		if (note == 1 && velocity > 0) {
			for (int j = 0; j < numRows; j++)
				for (int k = 0; k < numCols; k++) {
					buttons[k][j] = 1;
					counter[k][j] = 20;
				}
			rotateAmount = rotateMax;
		} else if(note == 1 && velocity == 0)
		{
			rotateAmount = 0;
		}

	}

	@Override
	public void ctrlEvent(int num, float val, int chan) {
		// TODO Auto-generated method stub

	}

	@Override
	public void camEvent(int note) {
			loadCamState(note, 100);
	}

}
