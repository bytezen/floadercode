package floader.visuals.flyingobjects.objects;

import java.util.Iterator;

import floader.looksgood.ani.*;
import processing.core.*;
import wblut.geom.*;
import wblut.processing.*;
import wblut.hemesh.*;
import processing.opengl.*;
import peasy.*;

public class ConeLayer extends AbstractMovingObject {

	HE_Mesh cone;

	public ConeLayer(int _distanceScale, float _duration, int _yOffset, int _xOffset, PApplet _sketch, WB_Render _render) {
		super(_distanceScale, _duration, _yOffset, _xOffset, _sketch, _render);

		//rotationTween = new Tween("rotation", 0f, 2 * PConstants.PI, 213);
		
		effectExtrudeDistanceBase = 20;
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
		//Layer-specific creation & transforms
		////
		HEC_Cone conCreator = new HEC_Cone();
		conCreator.setRadius(10).setHeight(30).setFacets(4).setSteps(5);
		cone = new HE_Mesh(conCreator);

		//Rotate to point forwards
		WB_Point3d p = cone.getCenter();
		WB_Point3d q = new WB_Point3d(p.x + 1, p.y, p.z);
		cone.rotateAboutAxis(PConstants.PI, p, q);
		////
		
		applyGlobalMovement(cone);
		applyGlobalEffects(cone);
		
		//abstract this for themes
		app.fill(198,247,4);
		
		app.noStroke();
		render.drawFaces(cone);
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
