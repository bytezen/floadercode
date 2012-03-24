package mtvisualizer.components;

import java.beans.PropertyChangeEvent;

import java.beans.PropertyChangeListener;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.AbstractVisibleComponent;
import org.mt4j.components.visibleComponents.widgets.MTSlider;
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class NanoUIComponent extends AbstractVisibleComponent {

	PApplet app;
	OscP5 oscP5;
	NetAddress remoteAddress;
	NanoUIComponent thisComponent;
	private String imagePath =  "data"+  AbstractMTApplication.separator;
	
	public NanoUIComponent(PApplet app, OscP5 oscP5, NetAddress remoteAddress) {
		super(app);
		this.app = app;
		this.oscP5 = oscP5;
		this.remoteAddress = remoteAddress;
		thisComponent = this;
		
		//TODO make these relative to screen width
		addFilters(700, 150, 190, 80);
		addStretchers(220, 350, 190, 80);
		addDelayButtons(140,260);
		addGoButton(140, 170);
	}

	@Override
	public void drawComponent(PGraphics g) {
		
	}
	
	public void addDelayButtons(float x, float y)
	{
		addButton(x, y, "button.jpg").addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent) ge;
				buttonEvent(6,te);
				return true;
			}
		});
		
		addButton(x + 90, y, "button.jpg").addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent) ge;
				buttonEvent(7,te);
				return true;
			}
		});
	}
	
	public void addGoButton(float x, float y)
	{
		addButton(x, y, "button2.jpg").addGestureListener(TapProcessor.class, new IGestureEventListener() {
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent) ge;
				buttonEvent(8,te);
				return true;
			}
		});
	}
	
	public MTImageButton addButton(float x, float y, String imageName)
	{
		PImage eraser = app.loadImage(imagePath + imageName);
		MTImageButton b = new MTImageButton(app, eraser);
		b.setFillColor(new MTColor(200,100,200,180));
		b.setStrokeColor(new MTColor(255,255,255,200));
		b.translate(new Vector3D(x, y, 0));
		
		addChild(b);
		return b;
	}
	
	public void addStretchers(float x, float y, float width, float height) {
		addSlider(x, y, width, height, 0).addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent p) {
				thisComponent.sliderEvent(4, (Float) p.getNewValue() * -1 + 1);
			}
		});
		addSlider(x + 90, y, width, height, 0).addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent p) {
				thisComponent.sliderEvent(5, (Float) p.getNewValue() * -1 + 1);
			}
		});
	}

	public void addFilters(float x, float y, float width, float height) {
		addSlider(x, y, width, height, 0).addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent p) {
				thisComponent.sliderEvent(0, (Float) p.getNewValue() * -1 + 1);
			}
		});
		addSlider(x + 90, y, width, height, 0).addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent p) {
				thisComponent.sliderEvent(1, (Float) p.getNewValue() * -1 + 1);
			}
		});
		addSlider(x, y + 200, width, height, 1).addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent p) {
				thisComponent.sliderEvent(2, (Float) p.getNewValue() * -1 + 1);
			}
		});
		addSlider(x + 90, y + 200, width, height, 1).addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent p) {
				thisComponent.sliderEvent(3, (Float) p.getNewValue() * -1 + 1);
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
		addChild(slider);
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

}
