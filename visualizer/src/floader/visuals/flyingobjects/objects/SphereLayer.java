package floader.visuals.flyingobjects.objects;

import java.util.Iterator;



import processing.core.*;
import wblut.geom.*;
import wblut.processing.*;
import wblut.hemesh.*;
import processing.opengl.*;
import peasy.*;

public class SphereLayer extends AbstractMovingObject {

	HE_Mesh sphere;
	HEC_Sphere sphCreator;
	
	public SphereLayer(int _distanceScale, float _duration, int _yOffset, int _xOffset, PApplet _sketch, WB_Render _render) {
		super(_distanceScale, _duration, _yOffset, _xOffset, _sketch, _render);

		//TODO retween
		//tp.addChild(new Tween("distance", 0, -1000 * distanceMult, duration));
		//rotationTween = new Tween("rotation", 0f, 2 * PConstants.PI, 213);
		effectExtrudeDistanceBase = 0;
		effectTwistXBase = 10f;
		effectScaleBase = 2;
		
	}

	public void play() {
		super.play();
	}

	public void stop() {
		super.stop();
	}

	public void draw() {
		sphCreator = new HEC_Sphere();
		sphCreator.setRadius(30).setUFacets(4).setVFacets(5);
		sphere = new HE_Mesh(sphCreator);

		// Rotate to timeline
		WB_Point3d p = sphere.getCenter();
		WB_Point3d q = new WB_Point3d(p.x + 1, p.y, p.z);
		//TODO retween
		//sphere.rotateAboutAxis(rotationTween.getPosition(), p, q);

		// Extrude
		HEM_Extrude extrude = new HEM_Extrude();
		extrude.setDistance(20);
		//TODO retween extrude
		
		sphere.modifySelected(extrude, getRandomSelection(0, 101, sphere));

		applyGlobalMovement(sphere);
		applyGlobalEffects(sphere);
		
		app.noStroke();
		app.fill(160,240,20, 200);
		
		app.pushMatrix();
			app.translate(200, 0, 0);
			render.drawFaces(sphere);
		app.popMatrix();
		app.pushMatrix();
			app.translate(-200, 0, 0);
			render.drawFaces(sphere);
		app.popMatrix();
	}

	@Override
	public void tapEvent(int eventType, boolean isTapDown) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragEvent(int eventType, float amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noteEvent(int note, int vel) {
		// TODO Auto-generated method stub
		
	}
}
