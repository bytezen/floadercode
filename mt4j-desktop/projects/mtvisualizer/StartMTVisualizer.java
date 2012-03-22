package mtvisualizer;

import org.mt4j.MTApplication;
import org.mt4j.AbstractMTApplication.CurrentClassGetter;

import basic.css.menus.MenuExampleScene;

import oscP5.OscMessage;
import oscP5.OscP5;

import peasy.PeasyCam;

public class StartMTVisualizer extends MTApplication {
	private static final long serialVersionUID = 1L;
	public static final int OSC_PORT = 7400;
	
	OscP5 oscP5;
	VisualizerScene mainScene;
	
	@Override
	public void startUp() {
		oscP5 = new OscP5(this, OSC_PORT);
		mainScene = new VisualizerScene(this, "main scene");
		addScene(mainScene);
	}
	
	public void oscEvent(OscMessage msg) {
		mainScene.oscEvent(msg);
	}
	
	public static void main(String[] args) {
		initialize();
	}
	
	
	
}
