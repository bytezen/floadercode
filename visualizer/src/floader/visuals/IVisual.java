package floader.visuals;

import oscP5.OscMessage;

public interface IVisual {
	public void setup();
	public void draw();
	
	public void noteObjEvent(int note, int vel);
	public void noteCamEvent(int note, int vel);
	public void ctrlEvent(int num, int val, int chan);
	public void dragEvent(int eventType, float amount); //from 0-1.  must be a tap event (rather than continuous) due to how PeasyCam handles animations
	public void tapEvent(int eventType, boolean isTapDown); //1 for tap down, 0 for tap release
	
	//public void effect1(float amount); //from 0-1
	//public void effect2(float amount); //from 0-1
}
