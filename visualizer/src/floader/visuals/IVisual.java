package floader.visuals;

import oscP5.OscMessage;

public interface IVisual {
	public void oscEvent(OscMessage msg);
	public void draw();
	public void setup();
}
