package floader.visuals.rectanglearmy;

import java.awt.Color;
import java.util.Iterator;

import floader.visuals.AbstractVisual;
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
public class RectangleArmyVisual extends AbstractVisual {
	// Meshes
	WB_Render meshRenderer;
	HEC_Creator creator;
	
	int numRows = 25;
	int numCols = 25;
	int RESET = 0;


	boolean fillBackground = true;
	float maxVertexDistance = 400;
	float vertexDistance = 10;
	float maxPerspectiveWidth = 5000;
	float perspectiveWidth;
	float maxBendAmount = 1000;
	float bendAmount;
	float explodeAmount;
	
	// presentation
	int bgcolor; // background color
	int maxBgColor = 255;
	boolean facesOn = true; // toggle display of faces
	boolean edgesOn = true; // toggle display of edges
	float ax, ay;
	boolean clock = false;
	boolean eighth = true;
	int maxCounter = 11;
	HE_Selection selection;
	Iterator<HE_Face> fItr;
	HE_Face f;
	CameraState camState;
	float minDistance = 400;
	float initDistance = 700;
	float maxDistance = 2000;

	float individualRotateAmount;
	float maxIndividualRotateAmount = .02f;
	float individualRotateDegrees = 0;
	
	float globalRotateAmount;
	float maxGlobalRotateAmount = 8;
	float globalRotateDegrees = 0;
	
	float rotateZ = 0;
	float maxRotateZ = .4f;
	float rotateIndividual = 0;
	float rotateForward = 0;
	float maxRotateForward = 1;
	float rotateCombine = 0;
	float maxRotateCombine = .4f;
	float rotateCrazy = 0;
	float maxRotateCrazy = 1;
	float rotateY;

	PApplet app;
	
	Ani localSpinAni;
	float localSpinAniSpeed = 1;
	float localSpinAniSpeedMin = .5f;
	float localSpinAniSpeedMax = 10;
	
	Ani globalSpinAni;
	float globalSpinAniSpeed = 1;
	float globalSpinAniSpeedMin = .5f;
	float globalSpinAniSpeedMax = 10;
	
	Ani noiseAni;
	static final int MAXNOISE = 8;
	float noise = 0;
	float maxNoise = 0;
	float initNoiseDuration = .2f;
	
	Ani explodeEaseAni;
	float explodeEaseAniDuration = 2;
	
	float rectScale = 1;
	float maxRectScale = 3.67f;


	public RectangleArmyVisual(PApplet app) {
		this.app = app;
	}

	public void setup() {
		super.setup();
		meshRenderer = new WB_Render(app);
		
		//Ani's
		localSpinAni = new Ani(this, localSpinAniSpeed, "rotateIndividual", 360);
		localSpinAni.repeat();
		globalSpinAni = new Ani(this, globalSpinAniSpeed, "rotateZ", 360);
		globalSpinAni.repeat();
		explodeEaseAni = new Ani(this, explodeEaseAniDuration, "explodeAmount", 1);
		explodeEaseAni.repeat();
		explodeEaseAni.setPlayMode(Ani.YOYO);
		explodeEaseAni.setEasing(Ani.EXPO_IN_OUT);
		noiseAni = new Ani(this, initNoiseDuration, "noise", 0);
		noiseAni.setEasing(Ani.EXPO_IN);
		noiseAni.pause();
		
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

	public void draw(PGraphics g) {
		
		super.draw(g);
		g.noStroke();
		g.lights();
		//creator = new HEC_Grid().setUSize(110).setVSize(110).setU(2).setV(2);
		creator = new HEC_Cube().setRadius(20);
		HE_Mesh rect = new HE_Mesh(creator);
		rect.modify(new HEM_Noise().setDistance(noise));
		
		g.pushMatrix();
			g.translate(0, 0, 0);
			globalRotateDegrees = (globalRotateDegrees + globalRotateAmount) % 360;
			g.rotateZ(PApplet.radians(globalRotateDegrees));
		
			for (int j = 0; j < numCols; j++){
				
				g.fill(curColorScheme.getColor(j % curColorScheme.getLength()).getRGB());
				for (int k = 0; k < numRows; k++) {
					
		
					g.pushMatrix();
						g.translate(j * 150 - (numCols * 150 / 2), k * 150 - (numRows * 150 / 2));
						individualRotateDegrees = (individualRotateDegrees + individualRotateAmount) % 360;
						g.rotateZ(PApplet.radians(-individualRotateDegrees));
						
						//Explode!
						rect.rotateAboutAxis(PApplet.radians(explodeAmount), numCols / 2 * 150, numRows / 2 * 150, 0, numCols / 2 * 100, numRows / 2 * 150, 0);
						
						// Crazy flying rotate
						//rect.rotateAboutAxis(rotateCrazy, -j / 4 * 150 + numCols / 2 * 150, k / 4 * 150 + numRows / 2 * 150, 0, j / 4 * 150 + numCols / 2 * 150, k / 4 * 150 + numRows / 2 * 150, 1);
						// Rotate forward
						//rect.rotateAboutAxis(rotateForward, meshes[j][k].getVerticesAsPoint()[0], meshes[j][k].getVerticesAsPoint()[1]);
						g.scale(rectScale);
						
						meshRenderer.drawFaces(rect);
					g.popMatrix();
				}
			}

		g.popMatrix();

	}

	public void reset() {
		super.reset();
		noise = 0;
		individualRotateAmount = 0;
		globalRotateAmount = 0;
		rectScale = 1;
		rotateZ = 0;
		rotateIndividual = 0;
		rotateForward = 0;
		rotateCrazy = 0;
		rotateCombine = 0;
		explodeAmount = 0;
		rotate = false;
		localSpinAni.pause();
		globalSpinAni.pause();
		explodeEaseAni.pause();
		explodeEaseAniDuration = 2;
	}

	@Override
	public void dragEvent(int eventType, float amount) {
		
	}

	@Override
	public void tapEvent(int eventType, boolean isTapDown) {

	}

	@Override
	public void noteObjEvent(int note, int vel) {
			
		
		noiseAni.setBegin(maxNoise);
			noiseAni.setEnd(0);
			noiseAni.start();
	}

	@Override
	public void ctrlEvent(int num, float val, int chan) {
		//Rotate individual amount
		if (num == 0) {
			individualRotateAmount = val * maxIndividualRotateAmount;
		} else
		//Rotate cam Z amount
		if (num == 1) {
			globalRotateAmount = val * maxGlobalRotateAmount;
		} else
		//Explode
		if (num == 2) {
			explodeEaseAni.setBegin(explodeAmount);
			explodeEaseAni.setEnd(val * 3);
			explodeEaseAni.start();
			
			//explodeAmount = val * 360 - (360 / 2);
		} else
		//Rotate combine amount
		if (num == 3) {
			explodeEaseAniDuration = 10 - (val * 10) + .2f;

			explodeEaseAni.setDuration(explodeEaseAniDuration);
		} else if(num == 4)
		{
			rectScale = 1 + val * maxRectScale;
		} else if(num == 5)
		{	
			noise = maxNoise = val * MAXNOISE;	
		}
	}

	@Override
	public void camEvent(int note) {
			/*switch (note) {
			case 0:
				cam.setDistance(minDistance);
				break;
			case 1:
				cam.setDistance(maxDistance);
				break;
			case 2:
				cam.setDistance((minDistance + maxDistance) / 2);
				break;
			}*/
	}



	

}
