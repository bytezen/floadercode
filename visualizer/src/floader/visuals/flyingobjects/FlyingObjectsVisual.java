package floader.visuals.flyingobjects;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;

import floader.looksgood.ani.Ani;
import floader.visuals.AbstractVisual;
import floader.visuals.IVisual;
import floader.visuals.VisualConstants;
import floader.visuals.flyingobjects.objects.AbstractMovingObject;
import floader.visuals.flyingobjects.objects.ConeLayer;
import floader.visuals.flyingobjects.objects.CylinderLayer;
import floader.visuals.flyingobjects.objects.SphereLayer;

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

public class FlyingObjectsVisual extends AbstractVisual implements IVisual {

	WB_Render render;
	MasterLayer masterLayer;
	LinkedList<AbstractMovingObject> cylinderGroup;
	LinkedList<AbstractMovingObject> sphereGroup;
	LinkedList<AbstractMovingObject> coneGroup;
	PApplet app;
	float scaleAmt;
	Ani scaleAni;

	private float baseDuration = 10;

	public FlyingObjectsVisual(PApplet app) {
		this.app = app;
		cam = new PeasyCam(app, 500);
		camStatePath = "data\\flyingobjects\\camState";

	}

	public void setup() {

		scaleAmt = 1;
		scaleAni = new Ani(this, .4f, "scaleAmt", 0);
		scaleAni.pause();
		scaleAmt = 0;

		cam.setMinimumDistance(100);
		cam.setMaximumDistance(500);
		cam.setDistance(400);
		cam.setActive(false);
		render = new WB_Render(app);
		masterLayer = new MasterLayer();

		cylinderGroup = new LinkedList<AbstractMovingObject>();
		cylinderGroup.add(new CylinderLayer(4, baseDuration, 0, 0, app, render));
		cylinderGroup.add(new CylinderLayer(4, baseDuration, 0, 0, app, render));
		cylinderGroup.add(new CylinderLayer(4, baseDuration, 0, 0, app, render));
		cylinderGroup.add(new CylinderLayer(4, baseDuration, 0, 0, app, render));
		cylinderGroup.add(new CylinderLayer(4, baseDuration, 0, 0, app, render));
		cylinderGroup.add(new CylinderLayer(4, baseDuration, 0, 0, app, render));
		masterLayer.addGroup(cylinderGroup);

		sphereGroup = new LinkedList<AbstractMovingObject>();
		sphereGroup.add(new SphereLayer(2, baseDuration, -100, 0, app, render));
		sphereGroup.add(new SphereLayer(2, baseDuration, -100, 0, app, render));
		sphereGroup.add(new SphereLayer(2, baseDuration, -100, 0, app, render));
		sphereGroup.add(new SphereLayer(2, baseDuration, -100, 0, app, render));
		sphereGroup.add(new SphereLayer(2, baseDuration, -100, 0, app, render));
		sphereGroup.add(new SphereLayer(2, baseDuration, -100, 0, app, render));
		masterLayer.addGroup(sphereGroup);

		coneGroup = new LinkedList<AbstractMovingObject>();
		coneGroup.add(new ConeLayer(6, baseDuration, -200, -200, app, render));
		coneGroup.add(new ConeLayer(6, baseDuration, -200, -100, app, render));
		coneGroup.add(new ConeLayer(6, baseDuration, -200, 0, app, render));
		coneGroup.add(new ConeLayer(6, baseDuration, -200, 100, app, render));
		coneGroup.add(new ConeLayer(6, baseDuration, -200, 200, app, render));
		masterLayer.addGroup(coneGroup);
	}

	private Object lock = new Object();

	public void draw() {
		app.lights();
		cam.feed();

		synchronized (lock) {
			masterLayer.drawPlayingLayers();
			masterLayer.effectEnableScale(scaleAmt);
		}
	}

	

	@Override
	public void dragEvent(int eventType, float amount) {
		// Twist
		if (eventType == 2 || eventType == 0) {
			masterLayer.effectEnableTwistX(1-amount);
		}
	}

	@Override
	public void tapEvent(int eventType, boolean isTapDown) {

		if (isTapDown) {
			scaleAmt = 1;
			scaleAni.start();
		}
	}

	@Override
	public void noteObjEvent(int note, int vel) {
		if (note == 0 && vel > 0) {
				// Remove the last element from the list, put it first and play
				// it
				synchronized (lock) {
					cylinderGroup.push(cylinderGroup.removeLast());
					cylinderGroup.peekFirst().play();
				}
			} else if (note == 1 && vel > 0) {
				synchronized (lock) {
					sphereGroup.push(sphereGroup.removeLast());
					sphereGroup.peekFirst().play();
				}
			} else if (note == 2 && vel > 0) {
				synchronized (lock) {
					coneGroup.push(coneGroup.removeLast());
					coneGroup.peekFirst().play();
				}
			}
	}

	@Override
	public void ctrlEvent(int num, int val, int chan) {

	}

	@Override
	public void noteCamEvent(int note, int vel) {
		if (vel > 0)
			loadCamState(note);
	}

}
