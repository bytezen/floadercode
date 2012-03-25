package floader.visuals;

import de.looksgood.ani.Ani;
import floader.visuals.flyingobjects.FlyingObjectsVisual;
import floader.visuals.hangon.HangOnVisual;
import floader.visuals.imagineyourgarden.ImagineYourGardenVisual;
import floader.visuals.tearsfordears.TearsForDearsVisual;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PGraphics;

public class StartVisual extends PApplet {

	ImagineYourGardenVisual viz;
	OscP5 oscP5;
	
	public static final int OSC_PORT = 7400;
	
	public void setup()
	{
		size(1024, 768, OPENGL);
		oscP5 = new OscP5(this, OSC_PORT);
		Ani.init(this);
		Ani.setDefaultEasing(Ani.LINEAR);
		viz= new ImagineYourGardenVisual(this);
		viz.setup();
		
	}
	
	public void draw()
	{
		background(0,0,0);
		viz.draw();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "floader.visuals.StartVisual" });
	}
	
	public void oscEvent(OscMessage msg) {
		
		viz.oscEvent(msg);
	}
	
}
