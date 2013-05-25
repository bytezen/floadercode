package mtvisualizer.components;

import mtvisualizer.MTVisualizerConstants;
import mtvisualizer.StartMTVisualizer;
import netP5.NetAddress;

import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.AbstractVisibleComponent;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import floader.visuals.IVisual;
import floader.visuals.VisualConstants;

import oscP5.OscMessage;
import oscP5.OscP5;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

public class EffectBoxUIComponent extends AbstractVisibleComponent {

	PApplet app;
	OscP5 oscP5;
	NetAddress remoteAddress;
	MTRectangle effectBox;
	float effectBoxOuterWidth;
	float effectBoxOuterHeight;
	float effectBoxInnerWidth;
	float effectBoxInnerHeight;
	float effectBoxPadding = 100;
	float effectBoxXPos;
	float effectBoxYPos;
	float buttonOuterWidth;
	float buttonInnerWidth;
	float buttonPadding = 30;
	int numButtons = 6;

	IVisual viz;
	OscMessage msg;

	public EffectBoxUIComponent(PApplet app, OscP5 oscP5, NetAddress remoteAddress, IVisual viz) {
		super(app);
		this.app = app;
		this.viz = viz;
		this.oscP5 = oscP5;
		this.remoteAddress = remoteAddress;
		effectBoxOuterWidth = app.width / 2;
		effectBoxOuterHeight = app.height - 200; // Leave room for buttons
		effectBoxInnerWidth = effectBoxOuterWidth - effectBoxPadding;
		effectBoxInnerHeight = effectBoxOuterHeight - effectBoxPadding;
		effectBoxXPos = effectBoxPadding / 2;
		effectBoxYPos = effectBoxPadding / 2;

		
		buttonOuterWidth = app.width / numButtons;
		buttonInnerWidth = buttonOuterWidth - buttonPadding;

		addEffectBox(0);
		addEffectBox(1);
		addButtons();
	}

	@Override
	public void drawComponent(PGraphics g) {

	}

	public void addButtons() {

		float buttonHeight = 130;

		for (int i = 0; i < numButtons; i++) {
			addButton(i, (i * buttonOuterWidth) + buttonPadding / 2, effectBoxOuterHeight + buttonPadding, buttonInnerWidth, buttonHeight);
		}
	}

	public void addButton(final int index, float x, float y, float width, float height) {
		PImage buttonImg = app.createImage((int) width, (int) height, PConstants.RGB);
		buttonImg.loadPixels();
		for (int i = 0; i < buttonImg.pixels.length; i++) {
			buttonImg.pixels[i] = app.color(0, 90, 60,100);
		}

		MTImageButton b = new MTImageButton(app, buttonImg);
		b.setStrokeColor(new MTColor(255, 255, 255, 200));
		b.translate(new Vector3D(x, y, 0));
		addChild(b);
		b.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			@Override
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent) ge;
				buttonEvent(index, te);
				return true;
			}
		});
	}

	public void addEffectBox(final int index) {
		effectBox = new MTRectangle(app, effectBoxInnerWidth, effectBoxInnerHeight);
		effectBox.translate(new Vector3D(effectBoxXPos + (index * effectBoxOuterWidth), effectBoxYPos, 0));
		effectBox.setStrokeColor(new MTColor(255, 255, 255, 100));
		effectBox.setStrokeWeight(4);
		effectBox.setFillColor(new MTColor(0, 0, 0, 0));
		effectBox.setDepthBufferDisabled(true);

		effectBox.unregisterAllInputProcessors();
		effectBox.setGestureAllowance(DragProcessor.class, true);
		// effectBox.setGe
		effectBox.registerInputProcessor(new DragProcessor(app));
		effectBox.registerInputProcessor(new TapProcessor(app));
		effectBox.removeAllGestureEventListeners();
		this.addChild(effectBox);
		effectBox.addGestureListener(DragProcessor.class, new IGestureEventListener() {

			@Override
			public boolean processGestureEvent(MTGestureEvent ge) {
				DragEvent de = (DragEvent) ge;


				MTRectangle target = (MTRectangle) de.getTarget();
				float width = target.getWidthXY(TransformSpace.RELATIVE_TO_PARENT);
				float height = target.getHeightXY(TransformSpace.RELATIVE_TO_PARENT);
				float posX = target.getPosition(TransformSpace.RELATIVE_TO_PARENT).x - width / 2;
				float posY = target.getPosition(TransformSpace.RELATIVE_TO_PARENT).y - height / 2;
				float absCursorX = de.getDragCursor().getCurrentEvtPosX();
				float absCursorY = de.getDragCursor().getCurrentEvtPosY();
				float relX = absCursorX - posX;
				float relY = absCursorY - posY;
				float scaledX = relX / width;
				float scaledY = relY / height;

				/*
				 * System.out.println("Scaled X: " + scaledX);
				 * System.out.println("Scaled Y: " + scaledY);
				 */

				EffectBoxUIComponent uiComponent = (EffectBoxUIComponent) target.getParent();
				uiComponent.effectBoxEvent(index, scaledX, scaledY);

				return true;
			}
		});
		
		effectBox.addGestureListener(TapProcessor.class, new IGestureEventListener() {
			@Override
			public boolean processGestureEvent(MTGestureEvent ge) {
				TapEvent te = (TapEvent) ge;
				viz.tapEvent(index, te.isTapDown());
				return true;
			}
		});
	}

	public void effectBoxEvent(int index, float x, float y) {

	
		if (x < 0)
			x = 0;
		if (x > 1)
			x = 1;
		msg = new OscMessage(VisualConstants.OSC_CTRL_PATH);
		msg.add((index * 2));
		msg.add(x);
		
		oscP5.send(msg, remoteAddress);
		viz.dragEvent(index * 2,x);
		
		if (y < 0)
			y = 0;
		if (y > 1)
			y = 1;
		msg = new OscMessage(VisualConstants.OSC_CTRL_PATH);
		msg.add((index * 2) + 1);
		msg.add(y);
		oscP5.send(msg, remoteAddress);
		viz.dragEvent(((index * 2) + 1), y);

	}

	public void buttonEvent(int index, TapEvent te) {
		if (te.isTapDown()) {
			msg = new OscMessage(VisualConstants.OSC_NOTE_PATH);
			msg.add(index);
			msg.add(1);
			oscP5.send(msg, remoteAddress);
		} else if (te.isTapped()) {
			msg = new OscMessage(VisualConstants.OSC_NOTE_PATH);
			msg.add(index);
			msg.add(0);
			oscP5.send(msg, remoteAddress);
		}
	}

}
