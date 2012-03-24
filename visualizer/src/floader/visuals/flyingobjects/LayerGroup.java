package floader.visuals.flyingobjects;

import java.util.ArrayList;



public class LayerGroup {

	AbstractMovingObject[] layers;
	
	
	public LayerGroup()
	{
		layers = new AbstractMovingObject[FlyingObjectsVisual.LAYER_SIZE];
	}
	
	public void addLayer(AbstractMovingObject layer, int index)
	{
		layers[index] = layer;
	}
	
	public AbstractMovingObject getLayer(int index)
	{
		return layers[index];
	}
	
	public int getLayerCount()
	{
		return layers.length;
	}
	
	public void effectEnableExtrude(float effectExtrudeDistanceScale)
	{
		for(int i=0;i<layers.length;i++)
		{
			layers[i].effectEnableExtrude(effectExtrudeDistanceScale);
		}
	}
	
	public void effectEnableTwistX(float effectTwistXScale)
	{
		for(int i=0;i<layers.length;i++)
		{
			layers[i].effectEnableTwistX(effectTwistXScale);
		}
	}
	
	public void effectEnableScale(float effectScale)
	{
		for(int i=0;i<layers.length;i++)
		{
			layers[i].effectEnableScale(effectScale);
		}
	}

}
