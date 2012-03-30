package floader.visuals.flyingobjects;

import java.io.*;

import floader.visuals.IVisual;
import floader.visuals.VisualConstants;
import floader.visuals.flyingobjects.objects.CylinderLayer;
import floader.visuals.flyingobjects.objects.LeakySphereLayer;



import processing.core.*;
import wblut.geom.core.*;
import wblut.hemesh.creators.*;
import wblut.hemesh.tools.*;
import wblut.geom.grid.*;
import wblut.geom.nurbs.*;
import wblut.core.math.*;
import wblut.hemesh.subdividors.*;
import wblut.core.processing.*;
import wblut.hemesh.composite.*;
import wblut.core.random.*;
import wblut.hemesh.core.*;
import wblut.geom.frame.*;
import wblut.core.structures.*;
import wblut.hemesh.modifiers.*;
import wblut.hemesh.simplifiers.*;
import wblut.geom.triangulate.*;
import wblut.geom.tree.*;
import processing.opengl.*;
import peasy.*;
import oscP5.*;

public class LeakierPhysicsVisual implements IVisual {

	WB_Render render;
	PeasyCam cam;
	LayerGroup layerGroup;
	PApplet app;
	int maxDistance = 1000;

	
	public static final int LAYER_SIZE = 2;
	private float baseDuration = 100;

	public LeakierPhysicsVisual(PApplet app) {
		this.app = app;
		cam = new PeasyCam(app, 500);
	}
	public void keyPressed(int keyCode)
	{
		layerGroup.tapEffect(0, true);
	}
	
	public void setup() {
		
		cam.setMinimumDistance(0);
		cam.setMaximumDistance(maxDistance);
		cam.setDistance(88.9);
		render = new WB_Render(app);
		layerGroup = new LayerGroup(LAYER_SIZE);
		layerGroup.addLayer(new LeakySphereLayer(0, baseDuration, 58, 78, app, render), 0);
		layerGroup.addLayer(new LeakySphereLayer(0, baseDuration, -58, -78, app, render), 1);
		layerGroup.getLayer(0).play();
		//layerGroup.getLayer(1).play();
	}

	public void draw() {
		cam.feed();
		for (int i = 0; i < layerGroup.getLayerCount(); i++) {
			if (layerGroup.getLayer(i).isPlaying())
				layerGroup.getLayer(i).draw();
		}
		
	}

	public void oscEvent(OscMessage msg) {
		
	}
	@Override
	public void dragEvent(int eventType, float amount) {
		layerGroup.dragEffect(eventType, amount);
	}
	
	@Override
	public void tapEvent(int eventType, boolean isTapDown) {
		layerGroup.tapEffect(eventType, isTapDown);
	}

}
