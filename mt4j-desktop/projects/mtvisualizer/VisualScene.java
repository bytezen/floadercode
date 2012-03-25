package mtvisualizer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import mtvisualizer.components.NanoUIComponent;
import mtvisualizer.components.TestHEMeshComponent;
import mtvisualizer.components.VisualComponent;
import netP5.NetAddress;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.clipping.Clip;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.widgets.MTSlider;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.sceneManagement.transition.SlideTransition;
import org.mt4j.util.MTColor;
import org.mt4j.util.camera.MTCamera;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import oscP5.OscMessage;
import oscP5.OscP5;

public class VisualScene extends AbstractScene {

	AbstractMTApplication app;
	VisualComponent vizComp;
	NanoUIComponent uiComp;
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
		//TODO make this relative to screen width
		vizComp.translate(new Vector3D(app.width/2, app.height/2, 0, 1));
		uiComp = new NanoUIComponent(app, oscP5, remoteAddress);
	}

	public void oscEvent(OscMessage msg) {
		vizComp.oscEvent(msg);
	}

	public void onEnter() {
		this.getCanvas().addChild(vizComp);
		this.getCanvas().addChild(uiComp);
	}

	public void onLeave() {
		vizComp.destroy();
		uiComp.destroy();
	}
}
