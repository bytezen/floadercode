package floader.visuals.flyingobjects;

import floader.visuals.flyingobjects.objects.AbstractMovingObject;

public class LayerGroup {

	AbstractMovingObject[] layers;
	
	public LayerGroup(final int size)
	{
		layers = new AbstractMovingObject[size];
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
	
	public void tapEffect(int eventType, boolean isTapDown)
	{
		for(int i=0;i<layers.length;i++)
		{
			layers[i].tapEffect(eventType, isTapDown);
		}
	}
	
	public void dragEffect(int eventType, float amount)
	{
		for(int i=0;i<layers.length;i++)
		{
			layers[i].dragEffect(eventType, amount);
		}
	}

}
