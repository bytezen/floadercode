package floader.visuals.percentages;

import java.util.Iterator;

import floader.looksgood.ani.Ani;
import floader.looksgood.ani.easing.*;
import floader.visuals.AbstractVisual;
import floader.visuals.IVisual;

import wblut.hemesh.modifiers.*;
import wblut.hemesh.creators.*;
import wblut.hemesh.*;
import wblut.geom.*;
import wblut.hemesh.core.*;
import wblut.core.processing.*;
import oscP5.*;
import peasy.*;
import processing.core.PApplet;

@SuppressWarnings("serial")
public class Percentages extends AbstractVisual implements IVisual {

	// Meshes
	HE_Mesh[] meshes;
	WB_Render meshRenderer;
	int numMeshes = 10;
	int meshesCreated = 0;
	boolean readyDraw = false;
	float maxVertexDistance = 200;
	float maxPerspectiveWidth = 5000;
	float perspectiveWidth;
	float maxBendAmount = 1000;
	float bendAmount;

	// presentation
	int shapecolor; // shape color
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
	boolean rotateXDir;
	boolean rotateYDir;
	boolean randomizeDir = true;
	float zoom;
	float speed;
	boolean reset = false;
	float maxDistance = 1000;
	PApplet app;
	Ani speedAni;
	boolean vertexExpand = false;

	public Percentages(PApplet app) {
		this.app = app;
	}

	public void setup() {
		meshRenderer = new WB_Render(app);
		meshes = new HE_Mesh[numMeshes];
		speedAni = new Ani(this, .4f, "speed", 2);
		speedAni.pause();
		camStatePath = "data\\percentages\\camState";
		
		
		
		
		createMeshes();
		cam = new PeasyCam(app, 0);
		cam.setDistance(0);
		//cam.setActive(false);
		cam.setMaximumDistance(maxDistance);
	}

	void createMeshes() {
		createMesh(0, 200, 16, 16, true);
		createMesh(1, 190, 16, 16, true);
		createMesh(2, 180, 16, 16, true);
		createMesh(3, 1800, 10, 10, false);
	}
	
	public void draw() {
		//app.background(255,255,255);
		app.noStroke();
		
		cam.feed();
		
		if (reset) {
			createMeshes();
			speed = 2;
			reset = false;
		}
		
		if (rotateXDir)
			rotateXAmt += speed;
		else
			rotateXAmt -= speed;

		if (rotateYDir)
			rotateYAmt += speed;
		else
			rotateYAmt -= speed;

		app.lights();
		/*app.beginCamera();
		app.camera();
		app.translate(-app.width / 2, -app.height / 2, (zoom) * 20 - 750);
		app.endCamera();*/
		
		if(vertexExpand)
		{
			HE_Selection selection = new HE_Selection(meshes[0]);
			Iterator<HE_Face> fItr = meshes[0].fItr();
			HE_Face f;
			while (fItr.hasNext()) {
				f = fItr.next();
				if (app.random(100) < 2) {
					selection.add(f);
				}
			}
			meshes[0].modifySelected(new HEM_VertexExpand().setDistance(app.random(10, 400)), selection);
			vertexExpand = false;
		}

		app.pushMatrix();
		app.rotateX(PApplet.radians(rotateXAmt));
		app.rotateY(PApplet.radians(rotateYAmt));
		drawMesh(0);
		app.popMatrix();

		app.pushMatrix();
		app.rotateX(PApplet.radians(rotateXAmt * 1.1f));
		app.rotateY(PApplet.radians(rotateYAmt * 1.3f));
		drawMesh(1);
		app.popMatrix();

		app.pushMatrix();
		app.rotateX(PApplet.radians(rotateXAmt * 1.4f));
		app.rotateY(PApplet.radians(rotateYAmt * 1.2f));
		// drawMesh(2);
		app.popMatrix();

		app.pushMatrix();
		app.rotateX(PApplet.radians(rotateXAmt / 1.5f));
		app.rotateY(PApplet.radians(rotateYAmt / 1.5f));
		drawMesh(3);
		app.popMatrix();

	}

	void drawMesh(int meshIndex) {
		shapecolor = app.color(0, 200, 200, 255);
		app.fill(shapecolor);
		meshRenderer.drawFaces(meshes[meshIndex]);
	}

	void createMesh(int meshIndex, int radius, int uFacets, int vFacets, boolean slice) {
		HEC_Creator creator = new HEC_Sphere().setRadius(radius).setUFacets(uFacets).setVFacets(vFacets).setCenter(0, 0, 0);
		meshes[meshIndex] = new HE_Mesh(creator);

		// Slice
		// if(slice)
		// meshes[meshIndex].modify(new HEM_Slice().setCap(false).setPlane(new
		// WB_Plane(new WB_Point(0,0,140), new WB_Vector(-161,0,-161))));

		// Lattice
		meshes[meshIndex].modify(new HEM_Lattice().setDepth(1).setWidth(4).setThresholdAngle(PApplet.radians(45)).setFuse(true));

	}


	@Override
	public void dragEvent(int eventType, float amount) {

	}

	@Override
	public void tapEvent(int eventType, boolean isTapDown) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noteObjEvent(int note, int vel) {
			
		if(note == 0 && vel > 0)
		{
			cam.setDistance(maxDistance, 20000);
		} else if(note == 1 && vel > 0)
		{
			cam.setDistance(0);
			speedAni.setEnd(2);
			speedAni.start();
		} else if(note == 2 && vel > 0)
		{
			speedAni.setEnd(10);
			speedAni.start();
		} else if(note == 3 && vel > 0)
		{
			vertexExpand = true;
		} else if(note == 4 && vel > 0)
		{
			reset = true;
		}
	}

	@Override
	public void ctrlEvent(int num, int val, int chan) {
		
	}

	@Override
	public void noteCamEvent(int note, int vel) {
		if (vel > 0)
			loadCamState(note);
		
	}
}
