package floader.visuals;

import floader.looksgood.ani.Ani;
import floader.visuals.flyingobjects.*;
import floader.visuals.hangon.AvanteHangOnVisual;
import floader.visuals.hangon.HangOnVisual;
import floader.visuals.kalimba.KalimbaVisual;
import floader.visuals.particles.*;
import floader.visuals.percentages.Percentages;
import floader.visuals.rectanglearmy.RectangleArmyVisual;
import floader.visuals.spincycle.SpinCycleVisual;
import floader.visuals.turingfractal.TuringFractalVisual;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;
import themidibus.*;

public class StartVisual extends PApplet {

	IVisual viz;
	OscP5 oscP5;
	MidiBus midiBus;
	//PShader blur;
	PShader sepblur;
	PGraphics src;
	PGraphics pass1, pass2;

	public static final int OSC_PORT = 7400;

	public void setup() {
		size(VisualConstants.WIDTH, VisualConstants.HEIGHT, OPENGL);
		src = createGraphics(VisualConstants.WIDTH, VisualConstants.HEIGHT, OPENGL); 
		pass1 = createGraphics(VisualConstants.WIDTH, VisualConstants.HEIGHT, OPENGL);
		pass1.noSmooth();  
		pass2 = createGraphics(VisualConstants.WIDTH, VisualConstants.HEIGHT, OPENGL);
		pass2.noSmooth();
		
		//blur = loadShader("blur.glsl"); 
		sepblur = loadShader("sepblur.glsl");
		sepblur.set("blurSize", 9);
		sepblur.set("sigma", 5.0f);  

		oscP5 = new OscP5(this, OSC_PORT);
		Ani.init(this);
		Ani.setDefaultEasing(Ani.LINEAR);
		midiBus = new MidiBus(this, "nanoKONTROL", "");

		//viz = new FlyingObjectsVisual(this);
		// viz = new Percentages(this);
		viz = new RectangleArmyVisual(this);
		//viz = new SpinCycle(this);
		//viz = new HangOnVisual(this);
		//viz = new AvanteHangOnVisual(this);
		//viz = new LeakierPhysicsVisual(this); //Doesn't seem to work
		//viz = new KalimbaVisual(this);
		//viz = new ParticleMirrorVisual(this);
		viz.setup();
		background(0);

	}

	public void draw() {
		//filter(blur);  
		viz.draw();
		
		
	}

	//B = toggle background
	//R = reset vis
	public void keyPressed() {
		if(this.key == 'b')
		{
			viz.toggleBackgroundFill();
		} else if(this.key == 'r')
		{
			viz.reset();
		}

	}

	public void keyReleased() {

	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "floader.visuals.StartVisual" });
	}

	public void oscEvent(OscMessage msg) {
		if (msg.checkAddrPattern(VisualConstants.OSC_CTRL_PATH)) {
			viz.ctrlEvent(msg.get(0).intValue(), msg.get(1).intValue(), msg.get(2).intValue());
		} else if (msg.checkAddrPattern(VisualConstants.OSC_NOTE_PATH)) {
			if (msg.get(2).intValue() == VisualConstants.OBJECT_EVENT_CHANNEL)
				viz.noteObjEvent(msg.get(0).intValue(), msg.get(1).intValue());
			else if (msg.get(2).intValue() == VisualConstants.CAM_EVENT_CHANNEL)
			{
				//Check if the vel of the incoming note > 0
				if(msg.get(1).intValue() > 0)
					viz.camEvent(msg.get(0).intValue());
			}
		}
	}

	public void noteOn(int channel, int pitch, int vel) {
		viz.noteObjEvent(hardwarePitchToVisPitch(pitch), vel);
	}

	//Translate the incoming pitch value from the hardware to a range of 0-9
	private int hardwarePitchToVisPitch(int pitch)
	{
		int convertedPitch = 0;
		switch(pitch)
		{
		case VisualConstants.NOTE_1: convertedPitch = 0;break;
		case VisualConstants.NOTE_2: convertedPitch = 1;break;
		case VisualConstants.NOTE_3: convertedPitch = 2;break;
		case VisualConstants.NOTE_4: convertedPitch = 3;break;
		case VisualConstants.NOTE_5: convertedPitch = 4;break;
		case VisualConstants.NOTE_6: convertedPitch = 5;break;
		case VisualConstants.NOTE_7: convertedPitch = 6;break;
		case VisualConstants.NOTE_8: convertedPitch = 7;break;
		case VisualConstants.NOTE_9: convertedPitch = 8;break;
		case VisualConstants.NOTE_10: convertedPitch = 9;break;
		default: System.err.println("Error: unidentified pitch: " + pitch + " sent to funtion: hardwarePitchToVisPitch");break;
		}
		return convertedPitch;
	}

	public void noteOff(int channel, int pitch, int vel) {
		viz.noteObjEvent(hardwarePitchToVisPitch(pitch), vel);
	}

	private int hardwareCtrlToVisCtrl(int ctrl)
	{
		int convertedCtrl = 0;
		switch(ctrl)
		{
		case VisualConstants.KNOB_1_CTRL_NUM: convertedCtrl = 0;break;
		case VisualConstants.KNOB_2_CTRL_NUM: convertedCtrl = 1;break;
		case VisualConstants.KNOB_3_CTRL_NUM: convertedCtrl = 2;break;
		case VisualConstants.KNOB_4_CTRL_NUM: convertedCtrl = 3;break;
		case VisualConstants.KNOB_5_CTRL_NUM: convertedCtrl = 4;break;

		case VisualConstants.SLDR_1_CTRL_NUM: convertedCtrl = 5;break;
		case VisualConstants.SLDR_2_CTRL_NUM: convertedCtrl = 6;break;
		case VisualConstants.SLDR_3_CTRL_NUM: convertedCtrl = 7;break;
		case VisualConstants.SLDR_4_CTRL_NUM: convertedCtrl = 8;break;
		case VisualConstants.SLDR_5_CTRL_NUM: convertedCtrl = 9;break;
		default: System.err.println("Error: unidentified ctrl num: " + ctrl + " sent to funtion: hardwareCtrlToVisCtrl");break;
		}
		return convertedCtrl;
	}

	public void controllerChange(int chan, int num, int val) {

		viz.ctrlEvent(hardwareCtrlToVisCtrl(num), PApplet.map(val, 0, 127, 0, 1), chan);
	}

}
