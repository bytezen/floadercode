package floader.visuals.percentages;

import java.util.Iterator;

import floader.looksgood.ani.Ani;
import floader.looksgood.ani.easing.*;
import floader.visuals.AbstractVisual;
import floader.visuals.IVisual;
import floader.visuals.VisualConstants;
import floader.visuals.colorschemes.BlackAndWhite;
import floader.visuals.colorschemes.BlueSunset;
import floader.visuals.colorschemes.ColorScheme;
import floader.visuals.colorschemes.SeaGreenSeaShell;
import floader.visuals.colorschemes.Terminal;
import wblut.geom.*;
import wblut.processing.*;
import wblut.hemesh.*;
import oscP5.*;
import peasy.*;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

@SuppressWarnings("serial")
public class PercentagesVisual extends AbstractVisual {

	// Meshes
	static final float MAXSPEED = 6;
	HE_Mesh[] meshes;
	WB_Render meshRenderer;
	int meshesCreated = 0;
	boolean readyDraw = false;
	float maxVertexDistance = 200;
	float maxPerspectiveWidth = 5000;
	float perspectiveWidth;
	float maxBendAmount = 1000;
	float bendAmount;

	// presentation
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
	float curMaxSpeed = MAXSPEED;
	boolean reset = false;
	float maxDistance = 1000;
	PApplet app;
	Ani speedAni;
	float vertexExpandAmount = 0;
	float maxVertexExpandAmount = 10;
	float sliceAmount = 0;
	float maxSliceAmount = 200;
	boolean slice = false;
	int noise;
	int maxNoise = 8;

	public PercentagesVisual(PApplet app) {
		this.app = app;
	}

	public void setup() {
		super.setup();

		meshRenderer = new WB_Render(app);
		meshes = new HE_Mesh[3];
		createMeshes();

		speedAni = new Ani(this, .4f, "speed", curMaxSpeed);
		speedAni.setEasing(Ani.QUAD_IN);
		speedAni.pause();

		reset();
	}

	void createMeshes() {
		createMesh(0, 110, 14, 14, 3);
		createMesh(1, 100, 14, 14, 3);
		// createMesh(2, 1600, 10, 10, 10);
	}

	public void draw(PGraphics g) {
		super.draw(g);
		g.noStroke();
		
		if (rotateXDir)
			rotateXAmt += speed;
		else
			rotateXAmt -= speed;

		if (rotateYDir)
			rotateYAmt += speed;
		else
			rotateYAmt -= speed;

		if (vertexExpandAmount > .1) {
			expandVertexes(0);
			expandVertexes(1);
		}

		if (noise > 0) {
			meshes[0].modify(new HEM_Noise().setDistance(noise));
			meshes[1].modify(new HEM_Noise().setDistance(noise));
		}

		g.translate(0,0,100);
		g.pushMatrix();
		g.rotateX(PApplet.radians(rotateXAmt * 1.1f));
		g.rotateY(PApplet.radians(rotateYAmt * 1.3f));
		drawMesh(0, g);
		g.popMatrix();

		g.pushMatrix();
		g.rotateX(PApplet.radians(rotateXAmt * 1.4f));
		g.rotateY(PApplet.radians(rotateYAmt * 1.2f));
		drawMesh(1, g);
		g.popMatrix();

		/*
		 * g.pushMatrix(); g.rotateX(PApplet.radians(rotateXAmt / 1.5f));
		 * g.rotateY(PApplet.radians(rotateYAmt / 1.5f)); drawMesh(2, g);
		 * g.popMatrix();
		 */
	}

	void applySlice(int meshIndex) {
		meshes[meshIndex].modify(new HEM_Slice().setCap(false).setPlane(
				new WB_Plane(new WB_Point3d(0, 0, sliceAmount),
						new WB_Vector3d(-161, 0, -161))));
	}

	void expandVertexes(int meshIndex) {
		HE_Selection selection = new HE_Selection(meshes[meshIndex]);
		Iterator<HE_Face> fItr = meshes[meshIndex].fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			if (app.random(100) < 2) {
				selection.add(f);
			}
		}

		meshes[meshIndex].modifySelected(
				new HEM_VertexExpand().setDistance(vertexExpandAmount),
				selection);
	}

	void drawMesh(int meshIndex, PGraphics g) {
		int rgb = curColorScheme.getColor(meshIndex).getRGB();
		int alpha = curColorScheme.getColor(meshIndex).getAlpha();
		g.fill(rgb, alpha);
		meshRenderer.drawFaces(meshes[meshIndex]);
	}

	void createMesh(int meshIndex, int radius, int uFacets, int vFacets,
			int latticeWidth) {
		HEC_Creator creator = new HEC_Sphere().setRadius(radius)
				.setUFacets(uFacets).setVFacets(vFacets).setCenter(0, 0, 0);
		meshes[meshIndex] = new HE_Mesh(creator);

		// Lattice
		meshes[meshIndex].modify(new HEM_Lattice().setDepth(1)
				.setWidth(latticeWidth).setThresholdAngle(PApplet.radians(45))
				.setFuse(false));
	}

	public void noteObjEvent(int index, float vel) {
		speedAni.setBegin(0);
		speedAni.setEnd(curMaxSpeed);
		speedAni.start();
	}

	@Override
	public void ctrlEvent(int index, float val) {
		// Control speed
		if (index == VisualConstants.LOCAL_EFFECT_1) {
			speed = val * MAXSPEED;
		} else if (index == VisualConstants.LOCAL_EFFECT_2) {
			vertexExpandAmount = val * maxVertexExpandAmount;
		}
	}

	@Override
	public void reset() {
		super.reset();
		noise = 0;
		speedAni.pause();
		vertexExpandAmount = 0;
		createMeshes();
		speed = 0;
		curMaxSpeed = 0;
		sliceAmount = 0;
	}
}
