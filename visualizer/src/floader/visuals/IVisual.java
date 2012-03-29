package floader.visuals;

import oscP5.OscMessage;

public interface IVisual {
	public void oscEvent(OscMessage msg);
	public void draw();
	public void setup();
	public void keyPressed(int keyCode);
	
	
	public void camEffect(float amount); //from 0-1.  must be a tap event (rather than continuous) due to how PeasyCam handles animations
	//public void effect1(float amount); //from 0-1
	//public void effect2(float amount); //from 0-1
}
