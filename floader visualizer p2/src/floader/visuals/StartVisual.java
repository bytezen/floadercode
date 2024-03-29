package floader.visuals;

import codeanticode.glgraphics.GLGraphicsOffScreen;
import floader.looksgood.ani.Ani;
import floader.visuals.alba_francesca_battista.Battista;
import floader.visuals.clay_shirky_density_variant.Density;
import floader.visuals.colorschemes.BlackAndWhite;
import floader.visuals.colorschemes.BlueSunset;
import floader.visuals.colorschemes.ColorScheme;
import floader.visuals.colorschemes.SeaGreenSeaShell;
import floader.visuals.colorschemes.SpinCyclz;
import floader.visuals.colorschemes.Terminal;
import floader.visuals.flyingobjects.*;
import floader.visuals.hangon.HangOnVisual;
import floader.visuals.hardwarecontrollers.AbletonOscCtrlClip;
import floader.visuals.hardwarecontrollers.AbletonOscNoteClip;
import floader.visuals.hardwarecontrollers.ComputerKeyboard;
import floader.visuals.hardwarecontrollers.MonomeMidi;
import floader.visuals.hardwarecontrollers.NanoKontrol2Midi;
import floader.visuals.hardwarecontrollers.NanoKontrol2Osc;
import floader.visuals.kinect.KinectVisual;
import floader.visuals.neveling_untitled_2.Neveling;
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

	int ctrl0;
	int ctrl1;
	int ctrl2;
	int clipX;
	int clipY;

	boolean midiReady = false;
	boolean applyReset = false;
	AbstractVisual viz;
	OscP5 oscP5;
	MidiBus midiBus;
	// PShader blur;
	PShader barrelblur;
	PShader sepblur;
	PShader edges;
	PGraphics pass1, pass2;
	PApplet offlineApp;
	PImage bgImage;
	Scene scene;
	boolean mirrorTriggered = false;
	boolean applyEdges = false;
	boolean applyCube = false;
	boolean applyBgCapture = false;
	int blurSize = 10;
	int maxBlurSize = 25;
	float cubeRotate;

	boolean applyMirror = false;
	int bgAlpha = 0;
	boolean applyBackground = true;
	// Color schemes
	int curColorSchemeIndex;
	ColorScheme colorSchemes[];

	Ani cameraDistanceAni;
	float maxCameraDistance = 1200;//2300;
	float minCameraDistance = 200;
	float curCameraDistance = maxCameraDistance;
	
	float lightFallOffAmt = 0;
	float dimAmt = 0;

	Ani perspectiveAni;
	float perspective = 0;
	float maxPerspective = .999f;

	public static final int OSC_PORT = 7400;

	public void setup() {
		size(VisualConstants.WIDTH, VisualConstants.HEIGHT, OPENGL);

		// Color
		colorSchemes = new ColorScheme[5];
		colorSchemes[0] = new Terminal();
		colorSchemes[1] = new BlackAndWhite();
		colorSchemes[2] = new BlueSunset();
		colorSchemes[3] = new SeaGreenSeaShell();
		colorSchemes[4] = new SpinCyclz();

		Ani.init(this);
		Ani.setDefaultEasing(Ani.LINEAR);
		// Camera Distance Ani
		cameraDistanceAni = new Ani(this, .5f, "curCameraDistance",
				maxCameraDistance);
		cameraDistanceAni.setEasing(Ani.EXPO_OUT);
		cameraDistanceAni.pause();

		// Perspective Ani
		perspectiveAni = new Ani(this, 0, "perspective", maxPerspective);
		perspectiveAni.setEasing(Ani.EXPO_OUT);
		perspectiveAni.pause();

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
		// scene.camera().setSceneRadius(maxCameraDistance);
		// scene.camera().setFocusDistance(2000);

		// Blur
		sepblur = loadShader("sepblur.glsl");
		sepblur.set("blurSize", 0);
		sepblur.set("sigma", 4f);
		
		//Barrel Blur
		barrelblur = loadShader("barrelblur.glsl");

		edges = loadShader("edges.glsl");

		oscP5 = new OscP5(this, OSC_PORT);

		midiBus = new MidiBus(this, VisualConstants.MIDI_DEVICE, "");
		// MidiBus.list();

		// Load the viz - complete
		//viz = new RectangleArmyVisual(offlineApp);
		// viz = new Percentages(offlineApp);
		// viz = new SpinCycleVisual(offlineApp);
		//viz = new HangOnVisual(offlineApp);
		//viz = new Neveling(offlineApp);
		//viz = new Density(offlineApp);
		viz = new Battista(offlineApp);
		
		// todo
		// viz = new FlyingObjectsVisual(this);
		// viz = new LeakierPhysicsVisual(this); //Doesn't seem to work

		offlineApp.g.beginDraw();
		viz.setup();
		offlineApp.g.endDraw();
		reset();
		textureMode(NORMAL);
		midiReady = true;
		
	}

	void reset() {
		perspectiveAni.pause();
		cameraDistanceAni.pause();
		background(0);
		blurSize = 0;
		applyBackground = true;
		perspective = 0;
		scene.camera().setPosition(new PVector(0, 0, maxCameraDistance));
		bgImage = null;
		applyEdges = false;
		viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
		applyMirror = false;
		clipX = VisualConstants.WIDTH;
		clipY = VisualConstants.HEIGHT;
	}

	public void draw() {
		if (applyReset) {
			reset();
			viz.reset();
			applyReset = false;
		}
		
		background(0);
		
		// Set camera zoom
		scene.camera().setPosition(
				new PVector(scene.camera().at().x, scene.camera().at().y,
						curCameraDistance));

		// Set background image
		if (bgImage != null)
			image(bgImage, 0, 0);

		// TODO figure out how to use clipping to handle mirror effect
		/*
		 * // offline buffer if(applyMirror){ offlineApp.setBounds(0, 0,
		 * VisualConstants.WIDTH/2, VisualConstants.HEIGHT); offlineApp.clip(0,
		 * 0, VisualConstants.WIDTH / 2, VisualConstants.HEIGHT); } else {
		 * offlineApp.setBounds(0, 0, VisualConstants.WIDTH,
		 * VisualConstants.HEIGHT); offlineApp.noClip(); }
		 */

		offlineApp.g.beginDraw();
		
		offlineApp.g.lightFalloff(1 - lightFallOffAmt,0, 0);
		offlineApp.g.ambientLight(150 - (dimAmt * 128), 150 - (dimAmt * 128), 150 - (dimAmt * 128));
		offlineApp.g.directionalLight(128 - (dimAmt * 128), 128 - (dimAmt * 128), 128 - (dimAmt * 128), 0, 0, -1);
		offlineApp.g.lightSpecular(0, 0, 0);
		offlineApp.g.lightFalloff(1.5f,0, 0);
		offlineApp.g.pointLight(150, 150, 150, 0, 0, 50);

		
		if (applyBackground)
			offlineApp.g.background(0, 0);

		
		scene.beginDraw();
		
		applyPerspective(offlineApp);
		
		if (!applyBackground)
		{
			int rectOuterSize = 40;
			int rectInnserSize = 30;
			int numRects = 50;
			offlineApp.pushMatrix();
			offlineApp.fill(0);
			offlineApp.translate(-(rectOuterSize * numRects)/2, -(rectOuterSize * numRects)/2);
			for(int i = 0; i < numRects;i++)
				for(int k = 0; k<numRects;k++)
					offlineApp.rect(i * rectOuterSize ,k * rectOuterSize, rectInnserSize,rectInnserSize);
			offlineApp.popMatrix();
		}
		
		viz.draw(offlineApp.g);

		scene.endDraw();
		offlineApp.g.endDraw();

		// Clipping
		offlineApp.g.clip(0, 0, clipX, clipY);

		// Applying the blur shader along the vertical direction
		sepblur.set("horizontalPass", 0);
		sepblur.set("blurSize", blurSize);
		sepblur.set("sigma", 4f);

		pass1.beginDraw();
		if (applyBackground)
			pass1.background(0, 0);

		pass1.shader(sepblur);
		pass1.image(offlineApp.g, 0, 0);
		pass1.endDraw();
		// Applying the blur shader along the horizontal direction
		sepblur.set("horizontalPass", 1);
		sepblur.set("blurSize", blurSize);
		sepblur.set("sigma", 4f);

		pass2.beginDraw();
		if (applyBackground)
			pass2.background(0, 0);
		pass2.shader(sepblur);

		pass2.image(pass1, 0, 0);
		pass2.endDraw();

		if (applyMirror) {
			image(pass2, 0, 0);
			pushMatrix();
			translate(VisualConstants.WIDTH, 0);
			scale(-1f, 1f);
			image(pass2, 0, 0);
			popMatrix();
		} else {
			image(pass2, 0, 0);
		}

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

		if (applyBgCapture) {
			if (bgImage != null)
				bgImage.blend(this.g, 0, 0, VisualConstants.WIDTH,
						VisualConstants.HEIGHT, 0, 0, VisualConstants.WIDTH,
						VisualConstants.HEIGHT, PImage.BLEND);
			else
				bgImage = this.g.get(0, 0, VisualConstants.WIDTH,
						VisualConstants.HEIGHT);
			applyBgCapture = false;
		}
	}

	void applyPerspective(PApplet p) {

		float fov = PI / 3.0f;
		float cameraZ = (float) ((VisualConstants.HEIGHT / 2.0f) / Math
				.tan(fov / 2.0f));
		p.perspective(fov, (float) VisualConstants.WIDTH
				/ (float) VisualConstants.HEIGHT * (1 - perspective),

		cameraZ / 10.0f, cameraZ * 50.0f);
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

	// TODO make keyboard just another hardware controller
	public void keyPressed() {
		if (VisualConstants.COMPUTERKEYBOARD_ENABLED) {
			globalEffectChange(ComputerKeyboard.convertKeyPress(this.key), 1);
		}
	}

	public boolean sketchFullScreen() {
		return VisualConstants.FULLSCREEN;
	}

	public static void main(String args[]) {
		PApplet.main("floader.visuals.StartVisual", args);
	}

	/*
	 * public void oscEvent(OscMessage msg) { if
	 * (msg.checkAddrPattern(VisualConstants.OSC_CTRL_PATH)) {
	 * viz.ctrlEvent(msg.get(0).intValue(), msg.get(1).intValue(), msg
	 * .get(2).intValue()); } else if
	 * (msg.checkAddrPattern(VisualConstants.OSC_NOTE_PATH)) { if
	 * (msg.get(2).intValue() == VisualConstants.OBJECT_EVENT_CHANNEL) { //
	 * Check if the vel of the incoming note > 0 if (msg.get(1).intValue() > 0)
	 * viz.noteObjEvent(msg.get(0).intValue(), msg.get(1) .intValue()); } else
	 * if (msg.get(2).intValue() == VisualConstants.CAM_EVENT_CHANNEL) { //
	 * Check if the vel of the incoming note > 0 if (msg.get(1).intValue() > 0)
	 * viz.camEvent(msg.get(0).intValue()); } } }
	 */

	public void noteOn(int chan, int note, int vel) {
		if (midiReady) {
			// System.out.println("Channel: " + chan + ", Note: " + note
			// + ", Vel: " + vel);

			int effect;
			float amount = PApplet.map(vel, 0, 127, 0, 1);
			if (VisualConstants.MONOMEMIDI_ENABLED) {
				effect = MonomeMidi.convertNote(chan, note);
				if (VisualConstants.isGlobalEffect(effect))
					globalEffectChange(effect, amount);
				else
					vizEffectChange(effect, amount);
			}
		}
	}

	public void noteOff(int chan, int note, int vel) {
		if (midiReady) {
			// System.out.println("Channel: " + chan + ", Note: " + note
			// + ", Vel: " + vel);

			int effect = MonomeMidi.convertNote(chan, note);
			if (VisualConstants.MONOMEMIDI_ENABLED && effect != -1) {
				if (VisualConstants.isGlobalEffect(effect))
					globalEffectChange(effect, 0);
				else
					vizEffectChange(effect, 0);
			}
		}
	}

	// TODO create hardware class for this
	/*
	 * // Translate the incoming pitch value from the hardware to a range of 0-9
	 * private int hardwareNoteToVisPitch(int pitch) { int convertedPitch = 0;
	 * switch (pitch) { case VisualConstants.NOTE_1: convertedPitch = 0; break;
	 * case VisualConstants.NOTE_2: convertedPitch = 1; break; case
	 * VisualConstants.NOTE_3: convertedPitch = 2; break; case
	 * VisualConstants.NOTE_4: convertedPitch = 3; break; case
	 * VisualConstants.NOTE_5: convertedPitch = 4; break; case
	 * VisualConstants.NOTE_6: convertedPitch = 5; break; case
	 * VisualConstants.NOTE_7: convertedPitch = 6; break; case
	 * VisualConstants.NOTE_8: convertedPitch = 7; break; case
	 * VisualConstants.NOTE_9: convertedPitch = 8; break; case
	 * VisualConstants.NOTE_10: convertedPitch = 9; break; default:
	 * System.err.println("Error: unidentified pitch: " + pitch +
	 * " sent to funtion: hardwarePitchToVisPitch"); break; } return
	 * convertedPitch; }
	 */

	public void controllerChange(int chan, int num, int val) {
		// Some junk MIDI is being spewed out every time the port is opened by
		// midiBus
		if (midiReady) {
			// System.out.println("Chan: " + chan + ", Ctrl Num: " + num +
			// ", Val: " + val);
			float amount = PApplet.map(val, 0, 127, 0, 1);
			int effect;

			if (VisualConstants.NANOKONTROL2MIDI_ENABLED) {
				effect = NanoKontrol2Midi.convertInputToIndex(chan, num);

				if (VisualConstants.isGlobalEffect(effect))
					globalEffectChange(effect, amount);
				else
					vizEffectChange(effect, amount);
			}

			if (VisualConstants.MONOMEMIDI_ENABLED) {
				effect = MonomeMidi.convertController(chan, num);
				if (VisualConstants.isGlobalEffect(effect))
					globalEffectChange(effect, amount);
				else
					vizEffectChange(effect, amount);
			}
		}
	}

	private void vizEffectChange(int index, float amount) {
		viz.ctrlEvent(index, amount);
	}

	private void globalEffectChange(int index, float amount) {
		switch (index) {
		case VisualConstants.GLOBAL_EFFECT_BLUR:
			blurSize = (int) (amount * maxBlurSize);
			break;
		case VisualConstants.GLOBAL_EFFECT_CAMDISTANCE:
			float pDistance = Math.abs(curCameraDistance - amount
					* maxCameraDistance);
			cameraDistanceAni.setBegin(curCameraDistance);
			cameraDistanceAni.setEnd((amount * maxCameraDistance)
					+ minCameraDistance);
			cameraDistanceAni
					.setDuration(.5f * (1 / ((pDistance / maxCameraDistance) + .1f)));
			cameraDistanceAni.start();
			break;
		case VisualConstants.GLOBAL_EFFECT_PERSPECTIVE:
			float perspectiveDelta = Math.abs(perspective
					- (amount * maxPerspective));
			perspectiveAni.setBegin(perspective);
			perspectiveAni.setEnd(amount * maxPerspective);
			perspectiveAni
					.setDuration(.5f * (1 / ((perspectiveDelta / maxPerspective) + .1f)));
			perspectiveAni.start();
			break;
		case VisualConstants.GLOBAL_EFFECT_SCALE:
			viz.scale(amount);
			break;
		case VisualConstants.GLOBAL_EFFECT_ROTATEX:
			viz.rotateX(amount);
			break;
		case VisualConstants.GLOBAL_EFFECT_ROTATEY:
			viz.rotateY(amount);
			break;
		case VisualConstants.GLOBAL_EFFECT_ROTATEZ:
			viz.rotateZ(amount);
			break;
		case VisualConstants.GLOBAL_TRIGGER_CUBE:
			if (amount > 0)
				applyCube = !applyCube;
			break;
		case VisualConstants.GLOBAL_TRIGGER_CAPTUREBG:
			if (amount > 0) {
				applyBgCapture = true;
			}
			break;
		case VisualConstants.GLOBAL_TRIGGER_EDGEDETECTION:
			if (amount > 0)
				applyEdges = !applyEdges;
			break;
		case VisualConstants.GLOBAL_TRIGGER_CYCLECOLORSCHEME:
			if (amount > 0) {
				curColorSchemeIndex = ++curColorSchemeIndex
						% colorSchemes.length;
				viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
			}
			break;
		case VisualConstants.GLOBAL_TRIGGER_RESET:
			if (amount > 0) {
				applyReset = true;
			}
			break;
		case VisualConstants.GLOBAL_TRIGGER_MIRROR:
			mirrorTriggered = true;
			if (amount > 0)
				applyMirror = !applyMirror;

			break;
		case VisualConstants.GLOBAL_TRIGGER_TOGGLEBGFILL:
			if (amount > 0)
				applyBackground = !applyBackground;
			break;
		case VisualConstants.GLOBAL_SCENE_RECTANGLES:
			if (amount > 0) {
				viz = new RectangleArmyVisual(offlineApp);
				viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
				viz.setup();
			}
			break;
		case VisualConstants.GLOBAL_SCENE_PERCENTAGES:
			if (amount > 0) {
				viz = new SpinCycleVisual(offlineApp);
				viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
				viz.setup();
			}
			break;
		case VisualConstants.GLOBAL_SCENE_SPINCYCLE:
			if (amount > 0) {
				viz = new PercentagesVisual(offlineApp);
				viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
				viz.setup();
			}
			break;
		case VisualConstants.GLOBAL_SCENE_FLYINGOBJECTS:
			if (amount > 0) {
				viz = new FlyingObjectsVisual(offlineApp);
				viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
				viz.setup();
			}
			break;
		case VisualConstants.GLOBAL_SCENE_KINECT:
			if (amount > 0) {
				viz = new KinectVisual(offlineApp);
				viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
				viz.setup();
			}
			break;
		case VisualConstants.GLOBAL_SCENE_HANGON:
			if (amount > 0) {
				viz = new HangOnVisual(offlineApp);
				viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
				viz.setup();
			}
			break;
		case VisualConstants.GLOBAL_SCENE_NEVELING:
			if (amount > 0) {
				viz = new Neveling(offlineApp);
				viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
				viz.setup();
			}
			break;
		case VisualConstants.GLOBAL_SCENE_DENSITY:
			if (amount > 0) {
				viz = new Density(offlineApp);
				viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
				viz.setup();
			}
			break;
		case VisualConstants.GLOBAL_SCENE_BATTISTA:
			if (amount > 0) {
				viz = new Battista(offlineApp);
				viz.setColorScheme(colorSchemes[curColorSchemeIndex]);
				viz.setup();
			}
			break;
		case VisualConstants.GLOBAL_EFFECT_CLIPX:
			if (amount < .01)
				clipX = 0;
			else if (amount > .98)
				clipX = VisualConstants.WIDTH;
			else
				clipX = (int) (amount * VisualConstants.WIDTH);
			break;
		case VisualConstants.GLOBAL_EFFECT_CLIPY:
			if (amount < .01)
				clipY = 0;
			else if (amount > .98)
				clipY = VisualConstants.HEIGHT;
			else
				clipY = (int) (amount * VisualConstants.HEIGHT);
			break;
		case VisualConstants.GLOBAL_EFFECT_LIGHTFALLOFF:
			lightFallOffAmt = amount;
			if(lightFallOffAmt > .95)lightFallOffAmt = .95f; //Ensure the falloff never drops to 0
			break;
		case VisualConstants.GLOBAL_EFFECT_LIGHTDIM:
			dimAmt = amount;
			break;
		}
	}

	void oscEvent(OscMessage msg) {
		int effect = -1;
		//System.out.println(msg.get(0).intValue() + ", " + msg.get(1).intValue() + ", " + msg.get(2).intValue());
		
		if(msg.checkAddrPattern("/mtn/ctrl") && msg.get(VisualConstants.OSC_CHANNEL_INDEX).intValue() == VisualConstants.ABLETON_OSC_NANOKONTROL_CHANNEL)
			effect = NanoKontrol2Osc.convertInputToIndex(msg);
		else if(msg.checkAddrPattern("/mtn/ctrl") && msg.get(VisualConstants.OSC_CHANNEL_INDEX).intValue() == VisualConstants.ABLETON_OSC_CTRL_CHANNEL)
			effect = AbletonOscCtrlClip.convertInputToIndex(msg);
		else if(msg.checkAddrPattern("/mtn/note") && msg.get(VisualConstants.OSC_CHANNEL_INDEX).intValue() == VisualConstants.ABLETON_OSC_NOTE_CHANNEL)
			effect = AbletonOscNoteClip.convertInputToIndex(msg);
		
		if (effect != -1) {
			float value = PApplet.map(msg.get(VisualConstants.OSC_VALUE_INDEX).intValue(), 0, 127, 0, 1);
			if (VisualConstants.isGlobalEffect(effect))
				globalEffectChange(effect, value);
			else
				vizEffectChange(effect, value);
		} else
			System.err.println("Error converting OSC event in oscEvent(OscMessage msg)");
			

	}
}
