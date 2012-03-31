package floader.visuals.flyingobjects.objects;

import java.util.Iterator;

import floader.looksgood.ani.*;
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

public abstract class AbstractMovingObject {
	
	public int distanceMult;
	public float duration;
	public boolean isPlaying;
	protected int yOffset;
	protected int xOffset;
	PApplet app;
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
	private float zOffset;
	Ani zOffsetAni;
	
	public AbstractMovingObject(int distanceMult, float duration, int yOffset, int xOffset, PApplet app, WB_Render render)
	{
		this.distanceMult = distanceMult;
		this.duration = duration;
		this.app = app;
		this.render = render;
		this.yOffset = yOffset;
		this.xOffset = xOffset;
		zOffsetAni = new Ani(this, duration, "zOffset", -2000 * distanceMult);
	}
	
	protected void applyGlobalMovement(HE_Mesh mesh)
	{
		mesh.move(xOffset, yOffset, zOffset);
	}
	
	public void play()
	{
		zOffsetAni.start();
		isPlaying = true;
	}
	
	public void stop()
	{
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
		 app.randomSeed(seed);
		 while (fItr.hasNext()) { f = fItr.next(); if (app.random(100) < threshold) { selection.add(f); } }
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
		mesh.scale((effectScale * effectScaleBase) + 1, 1, 1);
	}
	
	public void effectEnableScale(float effectScale)
	{
		this.effectScale = effectScale;
	}
	
	
	public abstract void tapEvent(int EventType, boolean isTapDown);
	public abstract void dragEvent(int EventType, float amount);
	public abstract void noteEvent(int note, int vel, int chan);
	
	
	
}
