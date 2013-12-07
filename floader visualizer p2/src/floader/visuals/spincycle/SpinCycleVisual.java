package floader.visuals.spincycle;

import floader.looksgood.ani.Ani;
import floader.visuals.*;
import floader.visuals.colorschemes.AccentedTerminal;
import floader.visuals.colorschemes.BlueSunset;
import floader.visuals.colorschemes.ColorScheme;
import floader.visuals.colorschemes.SeaGreenSeaShell;
import floader.visuals.colorschemes.SpinCyclz;
import floader.visuals.colorschemes.Terminal;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PGraphics;
import wblut.geom.*;
import wblut.processing.*;
import wblut.hemesh.*;

@SuppressWarnings("serial")
public class SpinCycleVisual extends AbstractVisual implements IVisual {

	private Object lock = new Object();
	// Meshes
	HE_Mesh[] meshes;
	int numMeshes = 2;
	WB_Render meshRenderer;

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

	float rotDuration = 7;
	float minRotDuration = 2;
	float maxRotDuration = 7;
	float rotateY;
	float rotateZDegrees;
	float rotateZAmount;
	float maxRotateZAmount = 10;

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

	float twistAmount = 0;
	float maxTwistAmount = 1f;
	int maxColor = 255;

	float totalTwistAmt = 0;

	PApplet app;
	Ani rotateAni;
	Ani zoomAni;

	public SpinCycleVisual(PApplet app) {
		this.app = app;
	}

	@Override
	public void setup() {
		super.setup();
		
		meshRenderer = new WB_Render(app);
		meshes = new HE_Mesh[numMeshes];
		createMeshes();
		/*rotateAni = new Ani(this, rotDuration, "rotateXAmt", 360);
		rotateAni.repeat();
		rotateAni.start();*/
		zoomAni = new Ani(this, origZoomDuration, "zoomAmt", (cylHeight - 12000) / 2);
		zoomAni.repeat();
		zoomAni.start();
	}

	@Override
	public void draw(PGraphics g) {
		super.draw(g);
		g.noStroke();

		totalTwistAmt += twistAmount;
		
		g.translate(0, 0, zoomAmt);
		rotateZDegrees = (rotateZDegrees + rotateZAmount) % 360;
		g.rotateZ(PApplet.radians(rotateZDegrees));
		synchronized (lock) {
			drawMesh(0, g);
			drawMesh(1,g);
		}
	}

	void drawMesh(int meshIndex, PGraphics g) {
		if (meshIndex == 0)
			g.fill(curColorScheme.getColor(0).getRGB());
		else
			g.fill(curColorScheme.getColor(1).getRGB());

		meshes[meshIndex].modify(new HEM_Twist().setAngleFactor((twistAmount)).setTwistAxis(new WB_Line(new WB_Point3d(0, 0, 0), new WB_Vector3d(0, 0, 1))));
		meshRenderer.drawFaces(meshes[meshIndex]);
	}

	void createMesh(int meshIndex, float radius, int steps) {
		HEC_Creator creator = new HEC_Cylinder().setRadius(radius).setHeight(cylHeight).setFacets(15).setSteps(steps);
		meshes[meshIndex] = new HE_Mesh(creator);
		// Lattice
		meshes[meshIndex].modify(new HEM_Lattice().setDepth(1).setWidth(40).setThresholdAngle(PApplet.radians(45)));
	}

	void createMeshes() {
		createMesh(0, 210, 30);
		createMesh(1, 200, 30);
	}

	@Override
	public void ctrlEvent(int index, float val) {
		if (index == 0){
			rotateZAmount = val * maxRotateZAmount;
		} if(index == 1){
			twistAmount = PApplet.map(val, 0, 1, 0, maxTwistAmount);
		}
	}

	public void reset() {
		createMeshes();
		alphaMult = 1;
		rotateZAmount = 0;
		rotDuration = maxRotDuration;
		twistAmount = 0;
	}

}
