package floader.visuals;

import floader.looksgood.ani.Ani;
import floader.visuals.flyingobjects.*;
import floader.visuals.hangon.AvanteHangOnVisual;
import floader.visuals.hangon.HangOnVisual;
import floader.visuals.imagineyourgarden.ImagineYourGardenVisual;
import floader.visuals.kalimba.KalimbaVisual;
import floader.visuals.percentages.Percentages;
import floader.visuals.tearsfordears.TearsForDearsVisual;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PGraphics;

public class StartVisual extends PApplet {

	IVisual viz;
	OscP5 oscP5;
	
	public static final int OSC_PORT = 7400;
	
	public void setup()
	{
		size(1024, 768, OPENGL);
		oscP5 = new OscP5(this, OSC_PORT);
		Ani.init(this);
		Ani.setDefaultEasing(Ani.LINEAR);
		viz= new Percentages(this);
		//viz = new TearsForDearsVisual(this);
		//viz = new ImagineYourGardenVisual(this);
		//viz = new HangOnVisual(this);
		//viz = new AvanteHangOnVisual(this);
		//viz = new LeakierPhysicsVisual(this);
		//viz = new KalimbaVisual(this);
		viz.setup();
		
	}
	
	public void draw()
	{
		background(0,0,0);
		viz.draw();
	}
	
	public void keyPressed()
	{
		viz.keyPressed(this.keyCode);
		//viz.dragEvent((float)mouseX/(float)width);
		viz.tapEvent(0,true);
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "floader.visuals.StartVisual" });
	}
	
	public void oscEvent(OscMessage msg) {
		
		viz.oscEvent(msg);
	}
	
}
