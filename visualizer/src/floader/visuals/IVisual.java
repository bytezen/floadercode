package floader.visuals;

import oscP5.OscMessage;
import processing.core.PGraphics;

public interface IVisual {
	public void setup();
	public void draw();
	
	public void noteObjEvent(int note, int vel); //expects notes from 0-9
	public void camEvent(int camState);
	public void ctrlEvent(int num, float val, int chan); //expects ctrl nums from 0-9
	public void dragEvent(int eventType, float amount); //from 0-1.  must be a tap event (rather than continuous) due to how PeasyCam handles animations
	public void tapEvent(int eventType, boolean isTapDown); //SHOULD DEPRECATE THIS. Make the visuals hardware-agnostic
	public void toggleBackgroundFill();
	public void cycleColorScheme();
	public void reset();

}
