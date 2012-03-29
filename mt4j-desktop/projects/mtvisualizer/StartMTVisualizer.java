package mtvisualizer;

import netP5.NetAddress;

import org.mt4j.MTApplication;
import oscP5.OscMessage;
import oscP5.OscP5;

public class StartMTVisualizer extends MTApplication {
	private static final long serialVersionUID = 1L;

	
	NetAddress remoteAddress;
	VisualScene currentScene;
	VisualScene newScene;
	boolean changeScene;
	OscP5 oscP5;
	String[] sceneList = { floader.visuals.flyingobjects.FlyingObjectsVisual.class.getName(), 
			floader.visuals.tearsfordears.TearsForDearsVisual.class.getName(),
			floader.visuals.hangon.AvanteHangOnVisual.class.getName(),
			floader.visuals.imagineyourgarden.ImagineYourGardenVisual.class.getName()
	};
	int currentSceneIndex;
	
	@Override
	public void startUp() {
		//this.frameRate = 30;
		oscP5 = new OscP5(this, MTVisualizerConstants.OSC_PORT);
		remoteAddress = new NetAddress(MTVisualizerConstants.OSC_REMOTE_ADDR, MTVisualizerConstants.OSC_REMOTE_PORT);

		floader.looksgood.ani.Ani.init(this);
		floader.looksgood.ani.Ani.setDefaultEasing(floader.looksgood.ani.AniConstants.LINEAR);
		currentScene = new VisualScene(this, oscP5, remoteAddress, sceneList[2]);
		addScene(currentScene);
		
	}

	public void oscEvent(OscMessage msg) {
		if (msg.checkAddrPattern("/mtn/note") && msg.get(2).intValue() == MTVisualizerConstants.SCENE_CHANGE) {
			int note = msg.get(0).intValue();
			int vel = msg.get(1).intValue();
				if (vel > 0 && note < sceneList.length) {
					System.out.println(note);
					newScene = new VisualScene(this, oscP5, remoteAddress, sceneList[note]);
					currentScene = newScene;
					this.changeScene(newScene);
			} else {
				
				currentScene.oscEvent(msg);
			}
		} else {
			currentScene.oscEvent(msg);
		}
	}
	
	@Override
	public void keyPressed()
	{
		VisualScene newScene;
		if(keyCode == 50)
		{
			System.out.println("change scene to: " + sceneList[0]);
			newScene = new VisualScene(this,oscP5, remoteAddress, sceneList[0]);
			currentScene = newScene;
			this.changeScene(newScene);
		} else if(keyCode == 51)
		{
			System.out.println("change scene to: " + sceneList[1]);
			newScene = new VisualScene(this,oscP5, remoteAddress, sceneList[1]);
			currentScene = newScene;
			this.changeScene(newScene);
		}
	}
	
	public static void main(String[] args) {
		initialize();
	}

}
