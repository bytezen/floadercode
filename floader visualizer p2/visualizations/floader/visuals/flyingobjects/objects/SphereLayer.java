package floader.visuals.flyingobjects.objects;

import java.awt.Color;
import java.util.Iterator;





import processing.core.*;
import wblut.geom.*;
import wblut.processing.*;
import wblut.hemesh.*;
import processing.opengl.*;
import peasy.*;

public class SphereLayer extends AbstractMovingObject {

	HE_Mesh sphere;
	
	public SphereLayer(int _distanceScale, float _duration, int _yOffset, int _xOffset, WB_Render _render) {
		super(_distanceScale, _duration, _yOffset, _xOffset, _render);

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

	public void draw(PGraphics g) {
		HEC_Sphere sphCreator = new HEC_Sphere();
		sphCreator.setRadius(40).setUFacets(4).setVFacets(5);
		HE_Mesh sphere = new HE_Mesh(sphCreator);

		// Rotate to timeline
		WB_Point3d p = sphere.getCenter();
		WB_Point3d q = new WB_Point3d(p.x + 1, p.y, p.z);
		//TODO retween
		//sphere.rotateAboutAxis(rotationTween.getPosition(), p, q);

		applyGlobalMovement(sphere);
		applyGlobalEffects(sphere);
		
		g.noStroke();
		g.fill(color.getRGB());
		
		g.pushMatrix();
			g.translate(200, 0, 0);
			render.drawFaces(sphere);
		g.popMatrix();
		g.pushMatrix();
			g.translate(-200, 0, 0);
			render.drawFaces(sphere);
		g.popMatrix();
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
