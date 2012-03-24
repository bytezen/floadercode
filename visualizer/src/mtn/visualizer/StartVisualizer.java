package mtn.visualizer;

import oscP5.OscMessage;
import oscP5.OscP5;
import peasy.PeasyCam;
import processing.core.PApplet;

public class StartVisualizer extends PApplet {

	Visualizer visualizer;
	OscP5 oscP5;
	public static final int OSC_PORT = 7400;
	
	public void setup()
	{
		size(1024, 768, OPENGL);
		oscP5 = new OscP5(this, OSC_PORT);
		visualizer= new Visualizer(this);
		visualizer.setup();
	}
	
	public void draw()
	{
		background(0,0,0);
		visualizer.draw();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "mtn.visualizer.StartVisualizer" });
	}
	
	public void oscEvent(OscMessage msg) {
		visualizer.oscEvent(msg);
	}
	
}
