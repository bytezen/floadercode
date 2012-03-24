package mtvisualizer;

import mtvisualizer.scenes.AbstractVisualizationScene;
import mtvisualizer.scenes.FlyingObjectsScene;
import netP5.NetAddress;

import org.mt4j.AbstractMTApplication;
import org.mt4j.MTApplication;
import org.mt4j.sceneManagement.transition.SlideTransition;

import oscP5.OscMessage;
import oscP5.OscP5;

public class StartMTVisualizer extends MTApplication {
	private static final long serialVersionUID = 1L;

	public static final int SCENE_CHANGE = 0;
	public static final int OSC_PORT = 7400;
	public static final String OSC_REMOTE_ADDR = "localhost";
	public static final int OSC_REMOTE_PORT = 7500;
	NetAddress remoteAddress;
	AbstractVisualizationScene currentScene;
	AbstractVisualizationScene newScene;
	boolean changeScene;
	OscP5 oscP5;
	String[] sceneList = { FlyingObjectsScene.class.getName(), 
			FlyingObjectsScene.class.getName()
	};
	int currentSceneIndex;
	
	@Override
	public void startUp() {
		oscP5 = new OscP5(this, OSC_PORT);
		remoteAddress = new NetAddress(OSC_REMOTE_ADDR, OSC_REMOTE_PORT);
		
		currentScene = SceneFactory.getScene(this, FlyingObjectsScene.class.getName(), oscP5, remoteAddress);
		addScene(currentScene);
	}

	public void oscEvent(OscMessage msg) {
		if (msg.checkAddrPattern("/mtn/note")) {
			int note = msg.get(0).intValue();
			int vel = msg.get(1).intValue();
			int chan = msg.get(2).intValue();

			if (chan == SCENE_CHANGE) {
				if (vel > 0 && note < sceneList.length) {
					newScene = SceneFactory.getScene(this, sceneList[note], oscP5, remoteAddress);
					changeScene = true;
				}
			} else {
				currentScene.oscEvent(msg);
			}
		}
	}
	
	public void keyPressed()
	{
		if(keyCode == 50)
		{
			System.out.println("change scene to: " + sceneList[0]);
			AbstractVisualizationScene newScene = SceneFactory.getScene(this, sceneList[0], oscP5, remoteAddress);
			currentScene = newScene;
			this.changeScene(newScene);
		} else if(keyCode == 51)
		{
			System.out.println("change scene to: " + sceneList[1]);
			AbstractVisualizationScene newScene = SceneFactory.getScene(this, sceneList[1], oscP5, remoteAddress);
			currentScene = newScene;
			this.changeScene(newScene);
		}
	}
	
	public void draw()
	{
		super.draw();
		if(changeScene)
		{
			System.out.println("change scene to: " + newScene.getName());
			changeScene = false;
			currentScene = newScene;
			this.changeScene(newScene);
		}
	}

	public static void main(String[] args) {
		initialize();
	}

	public static class SceneFactory {
		public static AbstractVisualizationScene getScene(AbstractMTApplication app, String name, OscP5 oscP5, NetAddress remoteAddress) {
			if (name.equals(FlyingObjectsScene.class.getName())) {
				return new FlyingObjectsScene(app, name, oscP5, remoteAddress);
			} else {
				System.err.println("Error: undefined scene name: " + name);
				return null;
			}

		}
	}

}
