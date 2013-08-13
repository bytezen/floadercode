package floader.visuals;

import codeanticode.glgraphics.GLGraphicsOffScreen;
import floader.looksgood.ani.Ani;
import floader.visuals.colorschemes.AccentedTerminal;
import floader.visuals.colorschemes.BlueSunset;
import floader.visuals.colorschemes.ColorScheme;
import floader.visuals.colorschemes.SeaGreenSeaShell;
import floader.visuals.colorschemes.SpinCyclz;
import floader.visuals.colorschemes.Terminal;
import floader.visuals.flyingobjects.*;
import floader.visuals.hangon.AvanteHangOnVisual;
import floader.visuals.hangon.HangOnVisual;
import floader.visuals.kalimba.KalimbaVisual;
import floader.visuals.particles.*;
import floader.visuals.percentages.PercentagesVisual;
import floader.visuals.rectanglearmy.RectangleArmyVisual;
import floader.visuals.spincycle.SpinCycleVisual;
import floader.visuals.turingfractal.TuringFractalVisual;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.opengl.PShader;
import themidibus.*;
import wblut.processing.WB_Render;
import processing.opengl.*;
import remixlab.proscene.*;

public class StartVisual extends PApplet {

	AbstractVisual viz;
	OscP5 oscP5;
	MidiBus midiBus;
	// PShader blur;
	PShader sepblur;
	PShader edges;
	PGraphics pass1, pass2;
	PApplet offlineApp;
	PImage bgImage;
	Scene scene;
	boolean applyEdges = false;
	boolean applyCube = false;
	int blurSize = 10;
	int maxBlurSize = 25;
	float cubeRotate;
	
	float perspectiveAmount = 0;
	boolean applyMirror;
	int bgAlpha = 0;
	boolean applyBackground = true;
	//Color schemes
	int curColorSchemeIndex;
	ColorScheme colorSchemes[];
	
	Ani cameraDistanceAni;
	float maxCameraDistance = 4000;
	float curCameraDistance = maxCameraDistance;

	public static final int OSC_PORT = 7400;

	public void setup() {
		size(VisualConstants.WIDTH, VisualConstants.HEIGHT, OPENGL);
		
		//Color
		colorSchemes = new ColorScheme[5];
		colorSchemes[0] = new Terminal();
		colorSchemes[1] = new AccentedTerminal();
		colorSchemes[2] = new BlueSunset();
		colorSchemes[3] = new SeaGreenSeaShell();
		colorSchemes[4] = new SpinCyclz();
		
		//Ani
		Ani.init(this);
		Ani.setDefaultEasing(Ani.LINEAR);
		cameraDistanceAni = new Ani(this, .5f, "curCameraDistance", maxCameraDistance);
		cameraDistanceAni.setEasing(Ani.EXPO_OUT);
		cameraDistanceAni.pause();

		// Offline drawing
		offlineApp = new PApplet();
		offlineApp.g = createGraphics(VisualConstants.WIDTH,
				VisualConstants.HEIGHT, PApplet.OPENGL);
		pass1 = createGraphics(VisualConstants.WIDTH, VisualConstants.HEIGHT,
				OPENGL);
		pass1.noSmooth();
		pass2 = createGraphics(VisualConstants.WIDTH, VisualConstants.HEIGHT,
				OPENGL);
		pass2.noSmooth();

		// Proscene
		scene = new Scene(this, (PGraphics3D) offlineApp.g);
		scene.disableKeyboardHandling();
		scene.setGridIsDrawn(VisualConstants.PROSCENE_GUIDES_ENABLED);
		scene.setAxisIsDrawn(VisualConstants.PROSCENE_GUIDES_ENABLED);
		//scene.camera().setSceneRadius(maxCameraDistance);
		//scene.camera().setFocusDistance(2000);
		
		// Blur
		sepblur = loadShader("sepblur.glsl");
		sepblur.set("blurSize", 0);
		sepblur.set("sigma", 4f);

		edges = loadShader("edges.glsl");

		oscP5 = new OscP5(this, OSC_PORT);
		
		midiBus = new MidiBus(this, "nanoKONTROL", "");
		

		// Load the viz - complete
		viz = new RectangleArmyVisual(offlineApp);
		//viz = new Percentages(offlineApp);
		//viz = new SpinCycleVisual(offlineApp);
		
		// Load the viz - todo
		// viz = new FlyingObjectsVisual(this);
		// viz = new HangOnVisual(this);
		// viz = new AvanteHangOnVisual(this);
		// viz = new LeakierPhysicsVisual(this); //Doesn't seem to work
		// viz = new KalimbaVisual(this);
		//viz = new ParticleVisual(offlineApp);

		viz.setup();
		reset();
		textureMode(NORMAL);
	}
	
	void reset()
	{
		background(0);
		blurSize=0;
		applyBackground = true;
		perspectiveAmount = 0;
		scene.camera().setPosition(new PVector(0, 0, maxCameraDistance));
		bgImage = null;
		applyEdges = false;
		viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
	}

	public void draw() {
		background(0);
		
		//Set camera zoom
		scene.camera().setPosition(
				new PVector(scene.camera().at().x, scene.camera().at().y,
						curCameraDistance));

		//Set background image
		if (bgImage != null)
			image(bgImage, 0, 0);

		//offline buffer
		offlineApp.g.beginDraw();
		scene.beginDraw();
		if(applyBackground)offlineApp.g.background(0, 0);
		
		applyPerspective(offlineApp);
		viz.draw(offlineApp.g);
		
		scene.endDraw();
		offlineApp.g.endDraw();

		// Applying the blur shader along the vertical direction
		sepblur.set("horizontalPass", 0);sepblur.set("blurSize", blurSize);sepblur.set("sigma", 4f);
		
		pass1.beginDraw();
		if(applyBackground)pass1.background(0, 0);
		
		pass1.shader(sepblur);
		
		pass1.image(offlineApp.g, 0, 0);
		pass1.endDraw();
		// Applying the blur shader along the horizontal direction
		sepblur.set("horizontalPass", 1);sepblur.set("blurSize", blurSize);sepblur.set("sigma", 4f);
		pass2.beginDraw();
		if(applyBackground)pass2.background(0,0);
		
		pass2.shader(sepblur);
		
		pass2.image(pass1, 0, 0);
		pass2.endDraw();
		
		

		if (applyMirror) {
			/*PImage topLeftCorner = pass2.get(0, 0, VisualConstants.WIDTH / 2,VisualConstants.HEIGHT / 2);
			
			pushMatrix();
			scale(1f, 1f);
			image(topLeftCorner, 0, 0);
			popMatrix();

			pushMatrix();
			translate(VisualConstants.WIDTH, VisualConstants.HEIGHT);
			scale(-1f, -1f);
			image(topLeftCorner, 0, 0);
			popMatrix();

			pushMatrix();
			translate(0, VisualConstants.HEIGHT);
			scale(1f, -1f);
			image(topLeftCorner, 0, 0);
			popMatrix();

			pushMatrix();
			translate(VisualConstants.WIDTH, 0);
			scale(-1f, 1f);
			image(topLeftCorner, 0, 0);
			popMatrix();*/
			
			PImage leftHalf = pass2.get(0, 0, VisualConstants.WIDTH / 2,VisualConstants.HEIGHT);
			
			pushMatrix();
			scale(1f, 1f);
			image(leftHalf, 0, 0);
			popMatrix();

			pushMatrix();
			translate(VisualConstants.WIDTH, 0);
			scale(-1f, 1f);
			image(leftHalf, 0, 0);
			popMatrix();
			
		} else
			image(pass2, 0, 0);
		
		if (applyEdges)
			filter(edges);

		if (applyCube) {
			this.pushMatrix();
			this.translate(VisualConstants.WIDTH / 2,
					VisualConstants.HEIGHT / 2 + 10);
			this.scale(238);
			this.rotateX(10);
			this.rotateY(PApplet.radians(cubeRotate));
			TexturedCube(this.g, this.g.get());
			this.popMatrix();
		}

		cubeRotate += .5f;
		cubeRotate = cubeRotate % 360;
	}

	void applyPerspective(PApplet p) {

		float fov = PI / 3.0f;
		float cameraZ = (float) ((VisualConstants.HEIGHT / 2.0f) / Math
				.tan(fov / 2.0f));
		p.perspective(fov, (float) VisualConstants.WIDTH
				/ (float) VisualConstants.HEIGHT * (1 - perspectiveAmount),

		cameraZ / 10.0f, cameraZ * 10.0f);
	}

	void TexturedCube(PGraphics g, PImage tex) {
		g.beginShape(QUADS);
		g.texture(tex);

		// Given one texture and six faces, we can easily set up the uv
		// coordinates
		// such that four of the faces tile "perfectly" along either u or v, but
		// the other
		// two faces cannot be so aligned. This code tiles "along" u, "around"
		// the X/Z faces
		// and fudges the Y faces - the Y faces are arbitrarily aligned such
		// that a
		// rotation along the X axis will put the "top" of either texture at the
		// "top"
		// of the screen, but is not otherwised aligned with the X/Z faces.
		// (This
		// just affects what type of symmetry is required if you need seamless
		// tiling all the way around the cube)

		// +Z "front" face
		g.vertex(-1, -1, 1, 0, 0);
		g.vertex(1, -1, 1, 1, 0);
		g.vertex(1, 1, 1, 1, 1);
		g.vertex(-1, 1, 1, 0, 1);

		// -Z "back" face
		g.vertex(1, -1, -1, 0, 0);
		g.vertex(-1, -1, -1, 1, 0);
		g.vertex(-1, 1, -1, 1, 1);
		g.vertex(1, 1, -1, 0, 1);

		// +Y "bottom" face
		g.vertex(-1, 1, 1, 0, 0);
		g.vertex(1, 1, 1, 1, 0);
		g.vertex(1, 1, -1, 1, 1);
		g.vertex(-1, 1, -1, 0, 1);

		// -Y "top" face
		g.vertex(-1, -1, -1, 0, 0);
		g.vertex(1, -1, -1, 1, 0);
		g.vertex(1, -1, 1, 1, 1);
		g.vertex(-1, -1, 1, 0, 1);

		// +X "right" face
		g.vertex(1, -1, 1, 0, 0);
		g.vertex(1, -1, -1, 1, 0);
		g.vertex(1, 1, -1, 1, 1);
		g.vertex(1, 1, 1, 0, 1);

		// -X "left" face
		g.vertex(-1, -1, -1, 0, 0);
		g.vertex(-1, -1, 1, 1, 0);
		g.vertex(-1, 1, 1, 1, 1);
		g.vertex(-1, 1, -1, 0, 1);

		g.endShape();
	}

	public void keyPressed() {
		if (this.key == 'x') {
			applyCube = !applyCube;
		} else if (this.key == 'r') {
			reset();
			viz.reset();

		} else if (this.key == 'c') {
			if (bgImage != null)
				bgImage.blend(this.g, 0, 0, VisualConstants.WIDTH,
						VisualConstants.HEIGHT, 0, 0, VisualConstants.WIDTH,
						VisualConstants.HEIGHT, PImage.BLEND);
			else
				bgImage = this.g.get(0, 0, VisualConstants.WIDTH,
						VisualConstants.HEIGHT);
		} else if (this.key == 'e')
			applyEdges = !applyEdges;
		else if (this.key == 's')
			{
				curColorSchemeIndex = ++curColorSchemeIndex % colorSchemes.length;
				System.out.println(curColorSchemeIndex);
				viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
			}
		else if (this.key == 'm')
			applyMirror = !applyMirror;
		else if (this.key == 'b')
			applyBackground = !applyBackground;
		else if(this.key == '1')
		{
			viz = new RectangleArmyVisual(offlineApp);
			viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
			viz.setup();
		} else if(this.key == '2')
		{
			viz = new SpinCycleVisual(offlineApp);
			viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
			viz.setup();
		} else if(this.key == '3')
		{
			viz = new PercentagesVisual(offlineApp);
			viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
			viz.setup();
		} else if(this.key == ' ')
			{
				this.save(frameCount + " image.tif");
			}

	}
	
	public boolean sketchFullScreen() {
		  return VisualConstants.FULLSCREEN;
		}

	public static void main(String args[]) {
		PApplet.main( "floader.visuals.StartVisual", args);
	}

	public void oscEvent(OscMessage msg) {
		if (msg.checkAddrPattern(VisualConstants.OSC_CTRL_PATH)) {
			viz.ctrlEvent(msg.get(0).intValue(), msg.get(1).intValue(), msg
					.get(2).intValue());
		} else if (msg.checkAddrPattern(VisualConstants.OSC_NOTE_PATH)) {
			if (msg.get(2).intValue() == VisualConstants.OBJECT_EVENT_CHANNEL) {
				// Check if the vel of the incoming note > 0
				if (msg.get(1).intValue() > 0)
					viz.noteObjEvent(msg.get(0).intValue(), msg.get(1)
							.intValue());
			} else if (msg.get(2).intValue() == VisualConstants.CAM_EVENT_CHANNEL) {
				// Check if the vel of the incoming note > 0
				if (msg.get(1).intValue() > 0)
					viz.camEvent(msg.get(0).intValue());
			}
		}
	}

	public void noteOn(int channel, int pitch, int vel) {
		viz.noteObjEvent(hardwarePitchToVisPitch(pitch), vel);
	}

	// Translate the incoming pitch value from the hardware to a range of 0-9
	private int hardwarePitchToVisPitch(int pitch) {
		int convertedPitch = 0;
		switch (pitch) {
		case VisualConstants.NOTE_1:
			convertedPitch = 0;
			break;
		case VisualConstants.NOTE_2:
			convertedPitch = 1;
			break;
		case VisualConstants.NOTE_3:
			convertedPitch = 2;
			break;
		case VisualConstants.NOTE_4:
			convertedPitch = 3;
			break;
		case VisualConstants.NOTE_5:
			convertedPitch = 4;
			break;
		case VisualConstants.NOTE_6:
			convertedPitch = 5;
			break;
		case VisualConstants.NOTE_7:
			convertedPitch = 6;
			break;
		case VisualConstants.NOTE_8:
			convertedPitch = 7;
			break;
		case VisualConstants.NOTE_9:
			convertedPitch = 8;
			break;
		case VisualConstants.NOTE_10:
			convertedPitch = 9;
			break;
		default:
			System.err.println("Error: unidentified pitch: " + pitch
					+ " sent to funtion: hardwarePitchToVisPitch");
			break;
		}
		return convertedPitch;
	}

	public void noteOff(int channel, int pitch, int vel) {
		viz.noteObjEvent(hardwarePitchToVisPitch(pitch), vel);
	}

	private int hardwareCtrlToVisCtrl(int ctrl) {
		int convertedCtrl = -1;
		switch (ctrl) {
		case VisualConstants.KNOB_1_CTRL_NUM:
			convertedCtrl = 0;
			break;
		case VisualConstants.KNOB_2_CTRL_NUM:
			convertedCtrl = 1;
			break;
		case VisualConstants.KNOB_3_CTRL_NUM:
			convertedCtrl = 2;
			break;
		case VisualConstants.KNOB_4_CTRL_NUM:
			convertedCtrl = 3;
			break;
		case VisualConstants.KNOB_5_CTRL_NUM:
			convertedCtrl = 4;
			break;
		case VisualConstants.KNOB_6_CTRL_NUM:
			convertedCtrl = 5;
			break;

		case VisualConstants.SLDR_1_CTRL_NUM:
			convertedCtrl = 6;
			break;
		case VisualConstants.SLDR_2_CTRL_NUM:
			convertedCtrl = 7;
			break;
		case VisualConstants.SLDR_3_CTRL_NUM:
			convertedCtrl = 8;
			break;
		case VisualConstants.SLDR_4_CTRL_NUM:
			convertedCtrl = 9;
			break;
		case VisualConstants.SLDR_5_CTRL_NUM:
			convertedCtrl = 10;
			break;
		default:
			System.err.println("Error: unidentified ctrl num: " + ctrl
					+ " sent to funtion: hardwareCtrlToVisCtrl");
			break;
		}
		return convertedCtrl;
	}

	public void controllerChange(int chan, int num, int val) {
		float newVal = PApplet.map(val, 0, 127, 0, 1);

		// Viz controls
		viz.ctrlEvent(hardwareCtrlToVisCtrl(num), newVal, chan);

		// Global controls
		switch (num) {
		// Change bg alpha
		case VisualConstants.SLDR_1_CTRL_NUM:
			blurSize = (int)(newVal * maxBlurSize);
			break;
		case VisualConstants.SLDR_2_CTRL_NUM:
			float pDistance = Math.abs(curCameraDistance - newVal * maxCameraDistance); 
			
			cameraDistanceAni.setBegin(curCameraDistance);
			cameraDistanceAni.setEnd(newVal * maxCameraDistance);
			cameraDistanceAni.setDuration(.5f * ( 1 / (pDistance / maxCameraDistance)));
			System.out.println(pDistance / maxCameraDistance);
			cameraDistanceAni.start();
			
			//curCameraDistance = maxCameraDistance * newVal;
			break;
		case VisualConstants.SLDR_3_CTRL_NUM:
			if (newVal < 1)
				perspectiveAmount = newVal;
			else
				newVal = .999f;
			break;
		case VisualConstants.SLDR_4_CTRL_NUM:
			break;
		case VisualConstants.SLDR_5_CTRL_NUM:
			break;
		}

	}

}
