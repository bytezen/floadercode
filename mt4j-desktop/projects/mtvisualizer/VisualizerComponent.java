package mtvisualizer;

import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.AbstractVisibleComponent;
import org.mt4j.components.visibleComponents.widgets.MTClipRectangle;
import org.mt4j.components.visibleComponents.widgets.MTWindow;

import oscP5.OscMessage;

import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PGraphics;
import wblut.hemesh.*;
import wblut.hemesh.core.*;
import wblut.hemesh.creators.*;
import wblut.core.*;
import wblut.core.processing.*;
import mtn.visualizer.*;


public class VisualizerComponent extends AbstractVisibleComponent{

	Visualizer viz;
	PApplet app;
	
	public VisualizerComponent(PApplet app) {
		super(app);
		this.app = app;
		viz = new Visualizer(app);
		viz.setup();
	}

	public void drawComponent(PGraphics g){
		//g.perspective((float)Math.PI/3.0f, g.width/g.height, 0, 100000);
		viz.draw();	
	}
	
	public void oscEvent(OscMessage msg){
		viz.oscEvent(msg);
	}
}
