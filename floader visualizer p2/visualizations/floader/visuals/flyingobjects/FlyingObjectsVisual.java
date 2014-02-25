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
import wblut.geom.*;
import wblut.processing.*;
import wblut.hemesh.*;
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
	float scale;
	float maxScale = 5;
	float rotateX;
	int maxDistance;
	int baseDistance;
	int distance;
	Ani scaleAni;

	private float baseDuration;
	private float maxDuration;
	private float duration;

	public FlyingObjectsVisual(PApplet app) {
		this.app = app;
	}

	public void setup() {
		rotateX = 0;
		this.scale = 1;
		 maxDistance = -4000;
		 baseDistance = maxDistance;
		 distance = maxDistance;
		 maxDuration = 5;
		 baseDuration = maxDuration;
		 duration = maxDuration;
		
		render = new WB_Render(app);
		masterLayer = new MasterLayer();

		cylinderGroup = new LinkedList<AbstractMovingObject>();
		cylinderGroup.add(new CylinderLayer(baseDistance, baseDuration, 0, 0, render));
		cylinderGroup.add(new CylinderLayer(baseDistance, baseDuration, 0, 0, render));
		cylinderGroup.add(new CylinderLayer(baseDistance, baseDuration, 0, 0, render));
		cylinderGroup.add(new CylinderLayer(baseDistance, baseDuration, 0, 0, render));
		cylinderGroup.add(new CylinderLayer(baseDistance, baseDuration, 0, 0, render));
		cylinderGroup.add(new CylinderLayer(baseDistance, baseDuration, 0, 0, render));
		cylinderGroup.add(new CylinderLayer(baseDistance, baseDuration, 0, 0, render));
		cylinderGroup.add(new CylinderLayer(baseDistance, baseDuration, 0, 0, render));
		cylinderGroup.add(new CylinderLayer(baseDistance, baseDuration, 0, 0, render));
		cylinderGroup.add(new CylinderLayer(baseDistance, baseDuration, 0, 0, render));
		cylinderGroup.add(new CylinderLayer(baseDistance, baseDuration, 0, 0, render));
		cylinderGroup.add(new CylinderLayer(baseDistance, baseDuration, 0, 0, render));
		cylinderGroup.add(new CylinderLayer(baseDistance, baseDuration, 0, 0, render));
		cylinderGroup.add(new CylinderLayer(baseDistance, baseDuration, 0, 0, render));
		
		masterLayer.addGroup(cylinderGroup);

		
		sphereGroup = new LinkedList<AbstractMovingObject>();
		sphereGroup.add(new SphereLayer(baseDistance, baseDuration, -100, 0,  render));
		sphereGroup.add(new SphereLayer(baseDistance, baseDuration, -100, 0,  render));
		sphereGroup.add(new SphereLayer(baseDistance, baseDuration, -100, 0,  render));
		sphereGroup.add(new SphereLayer(baseDistance, baseDuration, -100, 0,  render));
		sphereGroup.add(new SphereLayer(baseDistance, baseDuration, -100, 0,  render));
		sphereGroup.add(new SphereLayer(baseDistance, baseDuration, -100, 0,  render));
		sphereGroup.add(new SphereLayer(baseDistance, baseDuration, -100, 0,  render));
		sphereGroup.add(new SphereLayer(baseDistance, baseDuration, -100, 0,  render));
		
		masterLayer.addGroup(sphereGroup);

		
		coneGroup = new LinkedList<AbstractMovingObject>();
		coneGroup.add(new ConeLayer(baseDistance, baseDuration, -200, -600, render));
		coneGroup.add(new ConeLayer(baseDistance, baseDuration, -200, -500, render));
		coneGroup.add(new ConeLayer(baseDistance, baseDuration, -200, -400, render));
		coneGroup.add(new ConeLayer(baseDistance, baseDuration, -200, -300, render));
		coneGroup.add(new ConeLayer(baseDistance, baseDuration, -200, -200, render));
		coneGroup.add(new ConeLayer(baseDistance, baseDuration, -200, -100, render));
		coneGroup.add(new ConeLayer(baseDistance, baseDuration, -200, 0, render));
		coneGroup.add(new ConeLayer(baseDistance, baseDuration, -200, 100,render));
		coneGroup.add(new ConeLayer(baseDistance, baseDuration, -200, 200, render));
		coneGroup.add(new ConeLayer(baseDistance, baseDuration, -200, 300, render));
		coneGroup.add(new ConeLayer(baseDistance, baseDuration, -200, 400, render));
		coneGroup.add(new ConeLayer(baseDistance, baseDuration, -200, -500, render));
		coneGroup.add(new ConeLayer(baseDistance, baseDuration, -200, -600, render));


		masterLayer.addGroup(coneGroup);
	}

	private Object lock = new Object();

	public void draw(PGraphics g) {
		super.draw(g);
		g.noStroke();
		g.lights();
		g.rotateX(rotateX);
		g.translate(0,0,2000);
		
		synchronized (lock) {
			masterLayer.drawPlayingLayers(g, curColorScheme);
			masterLayer.effectEnableScale(scale);
		}
	}

	public void noteObjEvent(int note, float vel) {
		
	}

	@Override
	public void ctrlEvent(int index, float val) {
		if (index == VisualConstants.LOCAL_EFFECT_1 ) {
			masterLayer.effectEnableTwistX(1-val);
		} else if (index == VisualConstants.LOCAL_EFFECT_2)
		{
			rotateX = PApplet.radians(val  * 180);
		} else if(index == VisualConstants.LOCAL_EFFECT_3)
		{
			duration = 1 + (val * maxDuration);
			masterLayer.effectEnableDuration(duration);
		} else if (index == VisualConstants.LOCAL_EFFECT_4 && val > 0) {
			// Remove the last element from the list, put it first and play
			// it
			synchronized (lock) {
				cylinderGroup.push(cylinderGroup.removeLast());
				cylinderGroup.peekFirst().play();
			}
		} else if (index == VisualConstants.LOCAL_EFFECT_5 && val > 0) {
			synchronized (lock) {
				sphereGroup.push(sphereGroup.removeLast());
				sphereGroup.peekFirst().play();
			}
		} else if (index == VisualConstants.LOCAL_EFFECT_6 && val > 0) {
			synchronized (lock) {
				coneGroup.push(coneGroup.removeLast());
				coneGroup.peekFirst().play();
			}
		}
	}

	@Override
	public void reset() {
		setup();
	}
	
	public void scale(float val)
	{
		this.scale = 1 + (val * maxScale);
	}

}
