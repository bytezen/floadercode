package floader.visuals.flyingobjects.objects;

import java.util.Iterator;



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

public class CylinderLayer extends AbstractMovingObject {

	HE_Mesh cylinder;

	public CylinderLayer(int _distanceScale, float _duration, int _yOffset, int _xOffset, PApplet _sketch, WB_Render _render) {
		super(_distanceScale, _duration, _yOffset, _xOffset, _sketch, _render);
		
		//rotationTween = new Tween("rotation", 0f, 2 * PConstants.PI, 60);
		//opacityTween = new Tween("opacity", 255,0,200);
				
		effectExtrudeDistanceBase = 10;
		effectTwistXBase = 1f;
		effectScaleBase = 40;
	}

	public void play() {
		super.play();
	}

	public void stop() {
		super.stop();
	}

	public void draw() {
		HEC_Cylinder cylCreator = new HEC_Cylinder();
		cylCreator.setHeight(4000).setRadius(20).setFacets(5).setSteps(7);
		cylinder = new HE_Mesh(cylCreator);

		// Rotate once
		WB_Point3d p = cylinder.getCenter();
		WB_Point3d q = new WB_Point3d(p.x, p.y + 1, p.z);
		cylinder.rotateAboutAxis(PConstants.PI / 2, p, q);

		// Rotate to timeline
		p = cylinder.getCenter();
		q = new WB_Point3d(p.x + 500, p.y, p.z);
		//TODO retween
		//cylinder.rotateAboutAxis(rotationTween.getPosition(), p, q);

		// Extrude
		HEM_Extrude extrude = new HEM_Extrude();
		extrude.setDistance(50);
		/*for (int i = 0; i < extrudeTween.getChildCount(); i++)
			if (extrudeTween.getPosition() <= extrudeTween.getChild(0).getDuration())
				extrude.setDistance(extrudeTween.getChild(0).getPosition());
			else
				extrude.setDistance(extrudeTween.getChild(1).getPosition());*/

		//cylinder.modifySelected(extrude, getRandomSelection(0, 40, cylinder));

		applyGlobalMovement(cylinder);
		applyGlobalEffects(cylinder);
		
		app.noStroke();
		//TODO retween opacity
		app.fill(125,242,24, 200);
		render.drawFaces(cylinder);
	}

	@Override
	public void tapEffect(int eventType, boolean isTapDown) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragEffect(int eventType, float amount) {
		// TODO Auto-generated method stub
		
	}

}
