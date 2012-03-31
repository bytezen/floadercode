package floader.visuals.flyingobjects.objects;

import java.util.Iterator;

import floader.looksgood.ani.Ani;
import floader.looksgood.ani.AniConstants;
import floader.visuals.VisualConstants;

import processing.core.*;
import wblut.geom.core.*;
import wblut.hemesh.creators.*;
import wblut.hemesh.tools.*;

import wblut.core.processing.*;

import wblut.hemesh.core.*;

import wblut.hemesh.modifiers.*;

public class LeakySphereLayer extends AbstractMovingObject {

	HE_Mesh sphere;
	HEC_Sphere sphCreator;
	int seed = 1;
	int initRadius = 2;
	float noise = 0;
	float maxNoise = .2f;
	int tapRadius = 10;
	int radius;
	Ani radiusAni;
	Ani rotateAni;
	Ani noiseAni;
	float rotate;
	float rotateDuration = 10;
	float maxRotateDuration = 9;

	public LeakySphereLayer(int _distanceScale, float _duration, int _yOffset, int _xOffset, PApplet _sketch, WB_Render _render) {
		super(_distanceScale, _duration, _yOffset, _xOffset, _sketch, _render);

		// TODO retween
		// tp.addChild(new Tween("distance", 0, -1000 * distanceMult,
		// duration));
		// rotationTween = new Tween("rotation", 0f, 2 * PConstants.PI, 213);
		effectExtrudeDistanceBase = 0;
		effectTwistXBase = 10f;
		effectScaleBase = 20;
		radiusAni = new Ani(this, .5f, "tapRadius", 1);
		radiusAni.pause();

		rotateAni = new Ani(this, 10, "rotate", 360);
		rotateAni.repeat();
		
		noise = maxNoise;
		noiseAni = new Ani(this, .5f, "noise", 0);
		noise = 0;

		radius = initRadius;
	}

	public void play() {

		super.play();
		seed = (int) app.random(100);
	}

	public void stop() {
		super.stop();
	}

	public void draw() {

		sphCreator = new HEC_Sphere();
		if (radiusAni.isPlaying()) {
			radius = tapRadius;
		} else
			radius = initRadius;
		sphCreator.setRadius(radius).setUFacets(20).setVFacets(20);
		sphere = new HE_Mesh(sphCreator);

		// Extrude
		HE_Selection selection = getRandomSelection(seed, 12, sphere);
		sphere.modifySelected(new HEM_Extrude().setDistance(400), selection);
		
		//Noise
		sphere.modify(new HEM_Noise().setDistance(noise));

		// Rotate to timeline
		sphere.rotateAboutAxis(PApplet.radians(rotate), sphere.getVerticesAsPoint()[0], sphere.getVerticesAsPoint()[1]);

		// applyGlobalMovement(sphere);
		sphere.move(this.xOffset, this.yOffset, 0);
		applyGlobalEffects(sphere);

		app.noStroke();
		app.fill(255, 255, 255);
		render.drawFaces(sphere);
	}

	@Override
	public void tapEvent(int eventType, boolean isTapDown) {
		noise = maxNoise;
		noiseAni.start();
	}

	@Override
	public void dragEvent(int eventType, float amount) {
		if (eventType == 0 || eventType == 2) rotateAni.setDuration((amount * maxRotateDuration) + 1f);
	}
	
	public void noteEvent(int note, int vel, int chan)
	{
		if(chan == VisualConstants.OBJECT_EVENT_CHANNEL)
		{
			if(note == 0 && vel > 0)
			{
				radiusAni.start();
			}
		}
	}
	
}
