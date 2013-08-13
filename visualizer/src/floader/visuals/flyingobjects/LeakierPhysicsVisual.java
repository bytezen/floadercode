package floader.visuals.flyingobjects;

import java.io.*;
import java.util.LinkedList;

import floader.visuals.AbstractVisual;
import floader.visuals.IVisual;
import floader.visuals.VisualConstants;
import floader.visuals.flyingobjects.objects.AbstractMovingObject;
import floader.visuals.flyingobjects.objects.CylinderLayer;
import floader.visuals.flyingobjects.objects.LeakySphereLayer;

import processing.core.*;
import wblut.geom.*;
import wblut.processing.*;
import processing.opengl.*;
import peasy.*;
import oscP5.*;

public class LeakierPhysicsVisual extends AbstractVisual implements IVisual {

	WB_Render render;
	MasterLayer masterLayer;
	LinkedList<AbstractMovingObject> sphereGroup;
	PApplet app;
	int maxDistance = 1000;
	int camCounter = 0;

	private float baseDuration = 100;

	public LeakierPhysicsVisual(PApplet app) {
		this.app = app;
		cam = new PeasyCam(app, 500);
		camStatePath = "data\\leakyphysics\\camState";
		app.hint(PApplet.DISABLE_DEPTH_TEST);
	}

	public void keyPressed(int keyCode) {
		masterLayer.tapEffect(0, true);
	}

	public void setup() {

		cam.setMinimumDistance(0);
		cam.setMaximumDistance(maxDistance);
		cam.setDistance(88.9);
		cam.setActive(false);
		render = new WB_Render(app);
		masterLayer = new MasterLayer();

		sphereGroup = new LinkedList<AbstractMovingObject>();
		sphereGroup.add(new LeakySphereLayer(0, baseDuration, 58, 78, app, render));
		sphereGroup.get(0).play();
		// sphereGroup.add(new LeakySphereLayer(0, baseDuration, -58, -78, app,
		// render));
		masterLayer.addGroup(sphereGroup);
	}

	public void draw(PGraphics g) {
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
	public void noteObjEvent(int note, int velocity) {
		// System.out.println("note received in leaky viz");
		if (note == 0 && velocity > 0)
			sphereGroup.get(0).noteEvent(0, 127);
	}

	@Override
	public void ctrlEvent(int num, float val, int chan) {
		// TODO Auto-generated method stub

	}

	@Override
	public void camEvent(int note) {
			loadCamState(note);
	}


	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}
