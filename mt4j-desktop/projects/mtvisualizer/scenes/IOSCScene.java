package mtvisualizer.scenes;

import oscP5.OscMessage;

public interface IOSCScene {
	public void oscEvent(OscMessage msg);
}
