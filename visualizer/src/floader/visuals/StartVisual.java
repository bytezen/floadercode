package floader.visuals;

import de.looksgood.ani.Ani;
import floader.visuals.flyingobjects.FlyingObjectsVisual;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

public class StartVisual extends PApplet {

	FlyingObjectsVisual visualizer;
	OscP5 oscP5;
	
	public static final int OSC_PORT = 7400;
	
	public void setup()
	{
		size(1024, 768, OPENGL);
		oscP5 = new OscP5(this, OSC_PORT);
		Ani.init(this);
		visualizer= new FlyingObjectsVisual(this);
		visualizer.setup();
		
	}
	
	public void draw()
	{
		background(0,0,0);
		visualizer.draw();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "floader.visuals.StartVisual" });
	}
	
	public void oscEvent(OscMessage msg) {
		
		visualizer.oscEvent(msg);
	}
	
}
