package mtvisualizer;

import mtvisualizer.components.EffectBoxUIComponent;
import mtvisualizer.components.NanoUIComponent;
import mtvisualizer.components.VisualComponent;
import netP5.NetAddress;

import org.mt4j.AbstractMTApplication;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.sceneManagement.transition.SlideTransition;
import org.mt4j.util.math.Vector3D;
import oscP5.OscMessage;
import oscP5.OscP5;

public class VisualScene extends AbstractScene {

	AbstractMTApplication app;
	VisualComponent vizComp;
	//NanoUIComponent uiComp;
	EffectBoxUIComponent uiComp;
	OscP5 oscP5;
	NetAddress remoteAddress;
	SlideTransition slideLeftTransition;
	
	public VisualScene(AbstractMTApplication app, OscP5 oscP5, NetAddress remoteAddress, String visualName) {
		super(app, visualName);
		this.app = app;
		this.oscP5 = oscP5;
		this.remoteAddress = remoteAddress;
		this.registerGlobalInputProcessor(new CursorTracer(app, this));
		this.setTransition(new SlideTransition(app, 800, true));
		
		vizComp = new VisualComponent(app, visualName);
		vizComp.translate(new Vector3D(app.width/2, app.height/2, 0, 1));
		//uiComp = new NanoUIComponent(app, oscP5, remoteAddress);
		uiComp = new EffectBoxUIComponent(app, oscP5, remoteAddress);
	}

	public void oscEvent(OscMessage msg) {
		vizComp.oscEvent(msg);
	}

	@Override
	public void onEnter() {
		this.getCanvas().addChild(vizComp);
		this.getCanvas().addChild(uiComp);
	}

	@Override
	public void onLeave() {
		vizComp.destroy();
		uiComp.destroy();
	}
}
