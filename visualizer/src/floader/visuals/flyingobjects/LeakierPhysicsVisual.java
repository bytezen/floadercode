package floader.visuals.flyingobjects;

import java.io.*;
import java.util.LinkedList;

import floader.visuals.IVisual;
import floader.visuals.VisualConstants;
import floader.visuals.flyingobjects.objects.AbstractMovingObject;
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
	MasterLayer masterLayer;
	LinkedList<AbstractMovingObject> sphereGroup;
	PApplet app;
	int maxDistance = 1000;

	private float baseDuration = 100;

	public LeakierPhysicsVisual(PApplet app) {
		this.app = app;
		cam = new PeasyCam(app, 500);
	}
	public void keyPressed(int keyCode)
	{
		masterLayer.tapEffect(0, true);
	}
	
	public void setup() {
		
		cam.setMinimumDistance(0);
		cam.setMaximumDistance(maxDistance);
		cam.setDistance(88.9);
		render = new WB_Render(app);
		masterLayer = new MasterLayer();
		
		sphereGroup = new LinkedList<AbstractMovingObject>();
		sphereGroup.add(new LeakySphereLayer(0, baseDuration, 58, 78, app, render));
		sphereGroup.get(0).play();
		//sphereGroup.add(new LeakySphereLayer(0, baseDuration, -58, -78, app, render));
		masterLayer.addGroup(sphereGroup);
	}

	public void draw() {
		cam.feed();
		masterLayer.drawPlayingLayers();
		
	}

	@Override
	public void dragEvent(int eventType, float amount) {
		masterLayer.dragEffect(eventType, amount);
	}
	
	@Override
	public void tapEvent(int eventType, boolean isTapDown) {
		masterLayer.tapEffect(eventType, isTapDown);
	}
	@Override
	public void noteEvent(int note, int velocity, int channel) {
		//System.out.println("note received in leaky viz");
		sphereGroup.get(0).noteEvent(0, 127, VisualConstants.OBJECT_EVENT_CHANNEL);
	}
	@Override
	public void ctrlEvent(int num, int val, int chan) {
		// TODO Auto-generated method stub
		
	}

}
