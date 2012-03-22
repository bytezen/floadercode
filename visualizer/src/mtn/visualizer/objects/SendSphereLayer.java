package mtn.visualizer.objects;

import java.util.Iterator;


import processing.core.*;
import ijeoma.motion.*;
import ijeoma.motion.tween.*;
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

public class SendSphereLayer extends AbstractMovingObject {

	HE_Mesh sphere;

	TweenSequence extrudeTween;
	Tween rotationTween;

	public SendSphereLayer(int _distanceScale, int _duration, int _yOffset, int _xOffset, PApplet _sketch, WB_Render _render) {
		super(_distanceScale, _duration, _yOffset, _xOffset, _sketch, _render);

		tp = new TweenParallel();
		tp.addChild(new Tween("distance", 0, -1000 * distanceMult, duration));

		extrudeTween = new TweenSequence();
		extrudeTween.appendChild(new Tween("t1", 0f, 100f, 500));

		rotationTween = new Tween("rotation", 0f, 2 * PConstants.PI, 213);
		effectExtrudeDistanceBase = 0;
		effectTwistXBase = 10f;
		effectScaleBase = 20;
	}

	public void play() {
		super.play();
		extrudeTween.repeat();
		extrudeTween.play();
		rotationTween.repeat();
		rotationTween.play();
	}

	public void stop() {
		super.stop();
		extrudeTween.stop();
		rotationTween.stop();
	}

	public void draw() {
		HEC_Sphere sphCreator = new HEC_Sphere();
		sphCreator.setRadius(30).setUFacets(4).setVFacets(5);
		sphere = new HE_Mesh(sphCreator);

		// Rotate to timeline
		WB_Point3d p = sphere.getCenter();
		WB_Point3d q = new WB_Point3d(p.x + 1, p.y, p.z);
		sphere.rotateAboutAxis(rotationTween.getPosition(), p, q);

		// Extrude
		HEM_Extrude extrude = new HEM_Extrude();
		extrude.setDistance(20);
		for (int i = 0; i < extrudeTween.getChildCount(); i++)
			if (extrudeTween.getPosition() <= extrudeTween.getChild(0).getDuration())
				extrude.setDistance(extrudeTween.getChild(0).getPosition());
			else
				extrude.setDistance(extrudeTween.getChild(1).getPosition());
		
		sphere.modifySelected(extrude, getRandomSelection(0, 101, sphere));

		applyGlobalMovement(sphere);
		applyGlobalEffects(sphere);
		
		sketch.noStroke();
		sketch.fill(160,240,20, 200);
		
		sketch.pushMatrix();
			sketch.translate(200, 0, 0);
			render.drawFaces(sphere);
		sketch.popMatrix();
		sketch.pushMatrix();
			sketch.translate(-200, 0, 0);
			render.drawFaces(sphere);
		sketch.popMatrix();
	}
}
