package floader.visuals;

import oscP5.OscMessage;
import processing.core.PGraphics;

public interface IVisual {
	public void setup();
	public void draw(PGraphics g);
	
	public void camEvent(int camState);
	public void ctrlEvent(int index, float val);
	public void dragEvent(int eventType, float amount); //from 0-1.  must be a tap event (rather than continuous) due to how PeasyCam handles animations
	public void tapEvent(int eventType, boolean isTapDown); //SHOULD DEPRECATE THIS. Make the visuals hardware-agnostic
	public void scale(float amount);
	public void rotateX(float amount);
	public void rotateY(float amount);
	public void rotateZ(float amount);
	public void reset();

}
