package floader.visuals.flyingobjects.objects;

import java.awt.Color;
import java.util.Iterator;

import floader.looksgood.ani.*;
import floader.visuals.colorschemes.ColorScheme;
import processing.core.PApplet;
import processing.core.PGraphics;
import wblut.geom.*;
import wblut.processing.*;
import wblut.hemesh.*;

public abstract class AbstractMovingObject {
	
	public int distance;
	public float duration;
	public boolean isPlaying;
	protected int yOffset;
	protected int xOffset;
	protected float zOffset;
	WB_Render render;
	public Color color;
	
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
	
	Ani zOffsetAni;
	
	public AbstractMovingObject(int distance, float duration, int yOffset, int xOffset, WB_Render render)
	{
		this.duration = duration;
		this.distance = distance;
		this.render = render;
		this.yOffset = yOffset;
		this.xOffset = xOffset;
		zOffset = 0;
		zOffsetAni = new Ani(this, duration, "zOffset", distance);
		zOffsetAni.setPlayMode(Ani.YOYO);
		zOffsetAni.repeat();
	}
	
	protected void applyGlobalMovement(HE_Mesh mesh)
	{
		mesh.move(xOffset, yOffset, zOffset);
	}
	
	public void play()
	{
		zOffset = 0;
		zOffsetAni = new Ani(this, duration, "zOffset", distance);
		zOffsetAni.setPlayMode(Ani.YOYO);
		zOffsetAni.repeat();
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
		 while (fItr.hasNext()) { f = fItr.next(); if (Math.random() * 100 < threshold) { selection.add(f); } }
		 return selection;
	}
	
	public abstract void draw(PGraphics g);
	
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
	
	public void effectEnableScale(float effectScale)
	{
		this.effectScale = effectScale;
	}
	
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	public void setDuration(float duration)
	{
		zOffset = 0;
		this.duration = duration;
		zOffsetAni.setDuration(duration);
		/*zOffsetAni = new Ani(this, duration, "zOffset", distance);
		zOffsetAni.setPlayMode(Ani.YOYO);
		zOffsetAni.repeat();
		zOffsetAni.start();*/
	}
	
	public void setDistance(int distance) {
		zOffset = 0;
		this.distance = distance;
		zOffsetAni = new Ani(this, duration, "zOffset", distance);
		zOffsetAni.setPlayMode(Ani.YOYO);
		zOffsetAni.repeat();
		zOffsetAni.start();
	}
	
	
	public abstract void tapEvent(int EventType, boolean isTapDown);
	public abstract void dragEvent(int EventType, float amount);
	public abstract void noteEvent(int note, int vel);

	
	
	
	
}
