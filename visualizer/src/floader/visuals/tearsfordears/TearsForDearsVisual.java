package floader.visuals.tearsfordears;

import java.util.Iterator;

import floader.visuals.IVisual;
import floader.visuals.VisualConstants;
import wblut.hemesh.modifiers.*;
import wblut.hemesh.creators.*;
import wblut.hemesh.*;
import wblut.geom.*;
import oscP5.*;
import peasy.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import wblut.hemesh.core.*;
import wblut.core.processing.*;

@SuppressWarnings("serial")
public class TearsForDearsVisual implements IVisual{
	// Meshes
	HE_Mesh[][] meshes;
	WB_Render meshRenderer;
	int curMesh = 0;
	int numRows = 20;
	int numCols = 20;
	int RESET = 0;

	boolean readyDraw = false;
	float maxVertexDistance = 400;
	float vertexDistance = 10;
	float maxPerspectiveWidth = 5000;
	float perspectiveWidth;
	float maxBendAmount = 1000;
	float bendAmount;
	int[][] buttons;

	// presentation
	int bgcolor; // background color
	int shapecolor; // shape color
	boolean facesOn = true; // toggle display of faces
	boolean edgesOn = true; // toggle display of edges
	float shapeHue = 57; // default hue
	float shapeSaturation = 100; // default saturation
	float shapeBrightness = 96; // default brightness
	float[][] shapeTransparency; // default transparency
	float[] shapeR;
	float[] shapeG;
	float[] shapeB;
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
	float maxDistance = 1700;
	
	float rotateZ = 0;
	float maxRotateZ = 1;
	float rotateIndividual = 0;
	float maxRotateIndividual = 1;
	float rotateForward = 0;
	float maxRotateForward = 1;
	float rotateCombine = 0;
	float maxRotateCombine = .4f;
	float rotateCrazy = 0;
	float maxRotateCrazy = 1;
	float rotateY;

	PApplet app;
	
	public TearsForDearsVisual(PApplet app)
	{
		this.app = app;
		bgcolor = app.color(0,0,0);
	}

	public void setup() {
		meshRenderer = new WB_Render(app);
		meshes = new HE_Mesh[numCols][numRows];
		counter = new int[numCols][numRows];
		shapeTransparency = new float[numCols][numRows];
		shapeR = new float[30];
		shapeG = new float[30];
		shapeB = new float[30];
		
		shapeR[0] = 238; shapeR[1] = 169;shapeR[2] = 251;shapeR[3] = 66;shapeR[4] = 1;shapeR[5] = 242;shapeR[6] = 148;shapeR[7] = 166;shapeR[8] = 238;	shapeR[9] = 169;shapeR[10] = 251;shapeR[11] = 66;shapeR[12] = 1;shapeR[13] = 242;shapeR[14] = 148;shapeR[15] = 238;	shapeR[16] = 169;shapeR[17] = 251;shapeR[18] = 66;shapeR[19] = 1;shapeR[20] = 242;shapeR[21] = 148;
		shapeG[0] = 222; shapeG[1] = 211;shapeG[2] = 173;shapeG[3] = 207;shapeG[4] = 122;shapeG[5] = 172;shapeG[6] = 151;shapeG[7] = 204;shapeG[8] = 222; shapeG[9] = 211;shapeG[10] = 173;shapeG[11] = 207;shapeG[12] = 122;shapeG[13] = 172;shapeG[14] = 151;shapeG[15] = 222; shapeG[16] = 211;shapeG[17] = 173;shapeG[18] = 207;shapeG[19] = 122;shapeG[20] = 172;shapeG[21] = 151;
		shapeB[0] = 111; shapeB[1] = 217;shapeB[2] = 115;shapeB[3] = 180;shapeB[4] = 124;shapeB[5] = 191;shapeB[6] = 195;shapeB[7] = 221;shapeB[8] = 111;shapeB[9] = 217;shapeB[10] = 115;shapeB[11] = 180;shapeB[12] = 124;shapeB[13] = 191;shapeB[14] = 195;shapeB[15] = 111;shapeB[16] = 217;shapeB[17] = 115;shapeB[18] = 180;shapeB[19] = 124;shapeB[20] = 191;shapeB[21] = 195;
		
		buttons = new int[numCols][numRows];

		app.noStroke();
		cam = new PeasyCam(app, initDistance);
		cam.setMinimumDistance(minDistance);
		cam.setMaximumDistance(maxDistance);

		reset();
	}
	public void keyPressed(int keyCode)
	{
		System.out.println(keyCode);
	}

	void createDots() {
		for (int j = 0; j < numCols; j++)
			for (int k = 0; k < numRows; k++) {
				createDot(j, k);
			}
	}

	void createDot(int col, int row) {
		HEC_Creator creator;
		float x;
		float y;
		x = col * 150;
		y = row * 150;
		creator = new HEC_Grid().setUSize(110).setVSize(110).setU(2).setV(2).setCenter(x, y, 0);
		meshes[col][row] = new HE_Mesh(creator);
		shapeTransparency[col][row] = 190;
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
		app.lights();
		cam.feed();
		
		if(addLattice)
		{
			addLattice = false;
			lattice();
		}
		
		cam.rotateZ(rotateZ);
		
		//pushMatrix();
			if(scroll)
			{
				if(totalYPan >=300)
				{
					totalYPan=0;
					app.translate(0,-300);
				}
				else
				{
					totalYPan +=panYAmount;
					app.translate(0, totalYPan);
				}
			}
			
			for (int j = 0; j < numCols; j++)
				for (int k = 0; k < numRows; k++) {
					app.colorMode(PConstants.RGB, 255, 255, 255, 255);
					shapecolor = app.color(shapeR[j], shapeG[j], shapeB[j], shapeTransparency[j][k]);
					app.fill(shapecolor);
					
					//Rotate and combine
					meshes[j][k].rotateAboutAxis(rotateCombine, numCols/2 * 150, numRows/2 * 150, 0, numCols/2 * 150, numRows/2 * 150, 1);
					//Crazy flying rotate
					meshes[j][k].rotateAboutAxis(rotateCrazy, -j/4 * 150 + numCols/2 * 150, k/4 * 150 + numRows/2 * 150, 0, j/4 * 150 + numCols/2 * 150, k/4 * 150 + numRows/2 * 150, 1);
					//Rotate individually
					meshes[j][k].rotateAboutAxis(rotateIndividual, j * 150, k * 150, 0, j * 150, k * 150, 1);
					//Rotate forward
					meshes[j][k].rotateAboutAxis(rotateForward, meshes[j][k].getVerticesAsPoint()[0], meshes[j][k].getVerticesAsPoint()[1]);
					
					meshRenderer.drawFaces(meshes[j][k]);
					meshRenderer.drawEdges(meshes[j][k]);
				}
		//popMatrix();

	}
	
	
	public void lattice()
	{
		HE_Selection selection;
		for (int j = 0; j < numCols; j++)
			for (int k = 0; k < numRows; k++) {
				selection = new HE_Selection(meshes[j][k]);
				for(int i = 0;i<meshes[j][k].numberOfFaces()/2;i++) {
					selection.add(meshes[j][k].getFacesAsArray()[i]);
				}
				meshes[j][k].modifySelected(new HEM_Lattice().setDepth(1).setWidth(1).setThresholdAngle(PApplet.radians(45)).setFuse(true), selection);
			}		
	}
	
	public void reset()
	{
		//translate(-numCols/2 * 150,-numRows/2 * 150);
		createDots();
		
		cam.reset(0);
		cam.pan(numCols/2 * 150, numRows/2 * 150);
		rotateZ = 0;
		rotateIndividual = 0;
		rotateForward = 0;
		rotateCrazy = 0;
		rotateCombine = 0;
		rotate = false;
		scroll = false;
	}

	/*@Override
	public void keyPressed() {
		if(key=='1')
		{
			popMesh(50);
		}
		else if(key=='2')
		{
			popMesh(-50);
		}
		else if(key=='3')
		{
			addLattice = true;
		}
		else if(key == 's')
		{
			scroll = !scroll;
		} else if(key == 'r')
		{
			rotate = !rotate;
		} else if(key == 'x')
		{
			reset();
		} else if(key == '9')
		{
			cam.rotateZ(radians(90));
		}
	}
	*/
	public void oscEvent(OscMessage msg) {
		if (msg.checkAddrPattern("/mtn/note")) {
			
			int note = msg.get(0).intValue();
			int vel = msg.get(1).intValue();
			int chan = msg.get(2).intValue();
			
			if(chan == VisualConstants.OBJECT_EVENT_CHANNEL)
			{
			//RESET
			if(note == 0 && vel > 0)
			{
				reset();
			} else if(msg.get(0).intValue() == 3 && msg.get(1).intValue() != 0){
				addLattice = true;
			} else if(msg.get(0).intValue() == 4 && msg.get(1).intValue() != 0) {
				scroll = true;
			} else if(msg.get(0).intValue() == 5 && msg.get(1).intValue() != 0) {
				scroll = false;
			} else if(msg.get(0).intValue() == 6 && msg.get(1).intValue() != 0) {
				cam.rotateZ(PApplet.radians(90));
			}
		} else if (msg.checkAddrPattern("/mtn/ctrl")) {
			//CTRL 1 Rotate individual amount
			if (msg.get(0).intValue() == 1)
			{
				rotateIndividual = ((float) msg.get(1).intValue() / 127.0f) * maxRotateIndividual - (maxRotateIndividual/2);
			} else 
			//CTRL 2 Rotate cam Z amount	
			if (msg.get(0).intValue() == 2) {
				rotateZ = ((float) msg.get(1).intValue() / 127.0f) * maxRotateZ - (maxRotateZ/2);				
			} else 
			//CTRL 3 Rotate forward amount
			if (msg.get(0).intValue() == 3) {
				rotateForward = ((float) msg.get(1).intValue() / 127.0f) * maxRotateForward - (maxRotateForward/2);				
			} else 
			//CTRL 4 Rotate combine amount
			if (msg.get(0).intValue() == 4) {
				rotateCombine = ((float) msg.get(1).intValue() / 127.0f) * maxRotateCombine - (maxRotateCombine/2);				
			} else 
			//CTRL 5 Rotate crazy amount
			if (msg.get(0).intValue() == 5) {
				rotateCrazy = ((float) msg.get(1).intValue() / 127.0f) * maxRotateCrazy - (maxRotateCrazy/2);				
			}  
		}
		}

	}

	@Override
	public void camEffect(float amount) {
		cam.setDistance(amount * maxDistance);
	}

}
