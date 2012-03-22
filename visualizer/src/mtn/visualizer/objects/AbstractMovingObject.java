package mtn.visualizer.objects;

import java.util.Iterator;

import processing.core.PApplet;
import wblut.core.processing.WB_Render;
import wblut.geom.core.WB_Line;
import wblut.geom.core.WB_Point3d;
import wblut.geom.core.WB_Vector3d;
import wblut.hemesh.core.HE_Face;
import wblut.hemesh.core.HE_Mesh;
import wblut.hemesh.core.HE_Selection;
import wblut.hemesh.modifiers.HEM_Extrude;
import wblut.hemesh.modifiers.HEM_Twist;
import ijeoma.motion.*;
import ijeoma.motion.tween.*;

public abstract class AbstractMovingObject {
	
	public int distanceMult;
	public int duration;
	public boolean isPlaying;
	protected int yOffset;
	protected int xOffset;
	TweenParallel tp;
	PApplet sketch;
	WB_Render render;
	
	//effects
	//effects extrude
	HEM_Extrude extrude;
	float effectExtrudeDistanceScale;
	float effectExtrudeDistanceBase;
	//effects twistx
	HEM_Twist twist;
	float effectTwistXScale;
	float effectTwistXBase;
	//effects scale
	float effectScaleBase;
	float effectScale;
	
	public AbstractMovingObject(int _distanceMult, int _duration, int _yOffset, int _xOffset, PApplet _sketch, WB_Render _render)
	{
		distanceMult = _distanceMult;
		duration = _duration;
		sketch = _sketch;
		render = _render;
		yOffset = _yOffset;
		xOffset = _xOffset;
		
		tp = new TweenParallel();
		tp.addChild(new Tween("distance", 0, -1000 * distanceMult, duration));
	}
	
	protected void applyGlobalMovement(HE_Mesh mesh)
	{
		//Move backwards
		mesh.move(xOffset, yOffset, tp.getTween("distance").getPosition());
	}
	
	public void play()
	{
		tp.repeat();
		tp.play();
		isPlaying = true;
	}
	
	public void stop()
	{
		tp.stop();
		isPlaying = false;
	}
	
	public boolean isPlaying()
	{
		return isPlaying;
	}
	
	protected HE_Selection getRandomSelection(int seed, int threshold, HE_Mesh mesh)
	{
		 HE_Selection selection = new HE_Selection(mesh);
		 Iterator <HE_Face> fItr = mesh.fItr();
		 HE_Face f;
		 sketch.randomSeed(seed);
		 while (fItr.hasNext()) { f = fItr.next(); if (sketch.random(100) < threshold) { selection.add(f); } }
		 return selection;
	}
	
	public abstract void draw();
	
	//Effects
	protected void applyGlobalEffects(HE_Mesh mesh)
	{
		applyExtrude(mesh);
		applyTwistX(mesh);
		applyScale(mesh);
	}
	
	//EXTRUDE
	public void effectEnableExtrude(float _effectExtrudeDistanceScale)
	{
		effectExtrudeDistanceScale = _effectExtrudeDistanceScale;
	}
	
	protected void applyExtrude(HE_Mesh mesh)
	{
		if(effectExtrudeDistanceBase * effectExtrudeDistanceScale > 0)
		{
			extrude = new HEM_Extrude();
			extrude.setDistance(effectExtrudeDistanceBase * effectExtrudeDistanceScale);
			mesh.modify(extrude);
		}
	}
	
	//TWISTX
	protected void applyTwistX(HE_Mesh mesh)
	{
		if(effectTwistXBase * effectTwistXScale > 0)
			mesh.modify(new HEM_Twist().setAngleFactor(effectTwistXBase * effectTwistXScale).setTwistAxis(new WB_Line(new WB_Point3d(mesh.getCenter().xf()+10,mesh.getCenter().yf(),mesh.getCenter().zf()), new WB_Vector3d(mesh.getCenter().xf()+1,mesh.getCenter().yf(),mesh.getCenter().zf()))));
	}
	
	public void effectEnableTwistX(float _effectTwistXScale)
	{
		effectTwistXScale = _effectTwistXScale;
	}
	
	//SCALE
	protected void applyScale(HE_Mesh mesh)
	{
		//Add one so we don't end up with a scale of 0
		mesh.scale((effectScale * effectScaleBase) + 1);
	}
	
	public void effectEnableScale(float _effectScale)
	{
		effectScale = _effectScale;
	}
	
	
}
