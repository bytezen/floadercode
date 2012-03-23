package mtvisualizer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import netP5.NetAddress;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.clipping.Clip;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.widgets.MTSlider;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.camera.MTCamera;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import oscP5.OscMessage;
import oscP5.OscP5;
import peasy.PeasyCam;

public class VisualizerScene extends AbstractScene {

	AbstractMTApplication app;
	VisualizerComponent vizComp;
	PeasyCam cam;
	OscP5 oscP5;
	private VisualizerScene thisScene;
	NetAddress remoteAddress;

	public VisualizerScene(AbstractMTApplication app, String name, OscP5 oscP5, NetAddress remoteAddress) {
		super(app, name);
		this.app = app;
		this.oscP5 = oscP5;
		this.remoteAddress = remoteAddress;
		thisScene = this;
		this.registerGlobalInputProcessor(new CursorTracer(app, this));

		vizComp = new VisualizerComponent(app);
		vizComp.translate(new Vector3D(400, 400, 0, 1));
		this.getCanvas().addChild(vizComp);
		addSlider();
		// this.getCanvas().addChild(new SimpleComponent(app));
	}

	public void addSlider() {
		MTSlider slider = new MTSlider(app, 100, 100, 200, 38, 0.00f, 1.0f);
		slider.setValue(1.0f);
		// slider.rotateZ(new Vector3D(), 90, TransformSpace.LOCAL);
		// slider.translate(new Vector3D(-7, 325));
		slider.setStrokeColor(new MTColor(255, 255, 255));
		slider.setFillColor(new MTColor(220, 220, 220));
		slider.getKnob().setFillColor(new MTColor(70, 70, 70));
		slider.getKnob().setStrokeColor(new MTColor(70, 70, 70));
		slider.addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent p) {
				thisScene.sliderEvent(1, (Float) p.getNewValue());
			}
		});
		this.getCanvas().addChild(slider);
		slider.getKnob().sendToFront();
	}

	public void sliderEvent(int sliderIndex, float value) {
		OscMessage msg = new OscMessage("/mtn/ctrl");
		msg.add(sliderIndex);
		msg.add(value);
		oscP5.send(msg, remoteAddress);
	}

	public void onEnter() {
	}

	public void onLeave() {
	}

	public void oscEvent(OscMessage msg) {
		vizComp.oscEvent(msg);
	}
}
