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
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
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
import processing.core.PImage;

public class VisualizerScene extends AbstractScene {

	AbstractMTApplication app;
	VisualizerComponent vizComp;
	PeasyCam cam;
	OscP5 oscP5;
	private VisualizerScene thisScene;
	NetAddress remoteAddress;
	private String imagePath =  "data"+  AbstractMTApplication.separator;
	

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
		addFilters(700, 150, 190, 40);
		addStretchers(220, 310, 220, 80);
		addButtons(130,170);
	}

	public void addButtons(float x, float y)
	{
		addButton(x, y).addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent) ge;
				buttonEvent(6,te);
				return true;
			}
		});
		
		addButton(x + 150, y).addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent) ge;
				buttonEvent(7,te);
				return true;
			}
		});
	}
	
	public MTImageButton addButton(float x, float y)
	{
		PImage eraser = app.loadImage(imagePath + "button.jpg");
		MTImageButton b = new MTImageButton(app, eraser);
		b.setFillColor(new MTColor(200,100,200,180));
		b.setStrokeColor(new MTColor(255,255,255,200));
		b.translate(new Vector3D(x, y, 0));
		
		this.getCanvas().addChild(b);
		return b;
	}
	
	public void addStretchers(float x, float y, float width, float height) {
		addSlider(x, y, width, height, 0).addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent p) {
				thisScene.sliderEvent(4, (Float) p.getNewValue());
			}
		});
		addSlider(x + 150, y, width, height, 0).addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent p) {
				thisScene.sliderEvent(5, (Float) p.getNewValue());
			}
		});
	}

	public void addFilters(float x, float y, float width, float height) {
		addSlider(x, y, width, height, 0).addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent p) {
				thisScene.sliderEvent(0, (Float) p.getNewValue() * -1 + 1);
			}
		});
		addSlider(x + 70, y, width, height, 0).addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent p) {
				thisScene.sliderEvent(1, (Float) p.getNewValue() * -1 + 1);
			}
		});
		addSlider(x, y + 200, width, height, 1).addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent p) {
				thisScene.sliderEvent(2, (Float) p.getNewValue() * -1 + 1);
			}
		});
		addSlider(x + 70, y + 200, width, height, 1).addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent p) {
				thisScene.sliderEvent(3, (Float) p.getNewValue() * -1 + 1);
			}
		});
	}

	public MTSlider addSlider(float x, float y, float width, float height, float startValue) {
		MTSlider slider = new MTSlider(app, x, y, width, height, 0.00f, 1.0f);
		slider.rotateZ(new Vector3D(x, y), 90, TransformSpace.LOCAL);
		slider.setValue(startValue);
		slider.setStrokeColor(new MTColor(255, 255, 255, 180));
		slider.setFillColor(new MTColor(0, 0, 0, 0));
		slider.getKnob().setFillColor(new MTColor(70, 70, 70, 190));
		slider.getKnob().setStrokeColor(new MTColor(70, 70, 70, 190));
		this.getCanvas().addChild(slider);
		slider.getKnob().sendToFront();
		return slider;
	}

	public void sliderEvent(int index, float value) {
		sendCtrlMsg(index, value);
	}
	
	public void buttonEvent(int index, TapEvent te)
	{
		if(te.isTapDown())
		{
			sendCtrlMsg(index, 1);
		}else if(te.isTapped())
		{
			sendCtrlMsg(index, 0);
		}
	}
	
	public void sendCtrlMsg(int ctrlNum, float ctrlVal)
	{
		OscMessage msg = new OscMessage("/mtn/ctrl");
		msg.add(ctrlNum);
		msg.add(ctrlVal);
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
