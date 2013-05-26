package mtphysics;

import mtvisualizer.MTVisualizerConstants;

import org.mt4j.AbstractMTApplication;
import org.mt4j.MTApplication;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.camera.MTCamera;

import oscP5.OscMessage;
import oscP5.OscP5;

import processing.core.PImage;
import processing.core.PVector;
import advanced.simpleParticles.ImageParticle;
import advanced.simpleParticles.MTParticleSystem;

import floader.looksgood.ani.Ani;
import floader.visuals.VisualConstants;

public class StartPhysicsExample extends MTApplication {
	private static final long serialVersionUID = 1L;
	PhysicsScene scene;
	OscP5 oscP5;
	
	
	
	public static void main(String[] args) {
		initialize();
		
	}
	
	@Override
	public void startUp() {
		Ani.init(this);
		oscP5 = new OscP5(this, MTVisualizerConstants.OSC_PORT);
		scene = new PhysicsScene(this, "Physics Example Scene", oscP5);
		addScene(scene);
		
	}
	
	public void keyPressed()
	{
		scene.keyPressed(this.keyCode);
	}
	
	public void oscEvent(OscMessage msg) {
		if (msg.checkAddrPattern(VisualConstants.OSC_CTRL_PATH)) {
			//viz.ctrlEvent(msg.get(0).intValue(), msg.get(1).intValue(), msg.get(2).intValue());
		} else if (msg.checkAddrPattern(VisualConstants.OSC_NOTE_PATH)) {
			scene.noteObjEvent(msg.get(0).intValue(), msg.get(1).intValue());
		}
	}
	
	
}


