package floader.visuals.flyingobjects;

import java.util.ArrayList;
import java.util.LinkedList;

import processing.core.PGraphics;
import floader.visuals.colorschemes.ColorScheme;
import floader.visuals.flyingobjects.objects.AbstractMovingObject;

public class MasterLayer {

	ArrayList<LinkedList<AbstractMovingObject>> layerGroups;
	int curSize;

	public MasterLayer() {
		layerGroups = new ArrayList<LinkedList<AbstractMovingObject>>();
	}

	public void addGroup(LinkedList<AbstractMovingObject> layerGroup) {
		layerGroups.add(layerGroup);
	}

	/*
	 * public AbstractMovingObject getLayer(int index) { return layers[index]; }
	 */

	public int getLayerGroupCount() {
		return layerGroups.size();
	}

	public void effectEnableExtrude(float effectExtrudeDistanceScale) {
		for (LinkedList<AbstractMovingObject> layerGroup : layerGroups) {
			for (AbstractMovingObject obj : layerGroup) {
				obj.effectEnableExtrude(effectExtrudeDistanceScale);
			}
		}
	}

	public void effectEnableTwistX(float effectTwistXScale) {
		for (LinkedList<AbstractMovingObject> layerGroup : layerGroups) {
			for (AbstractMovingObject obj : layerGroup) {
				obj.effectEnableTwistX(effectTwistXScale);
			}
		}
	}

	public void effectEnableScale(float effectScale) {
		for (LinkedList<AbstractMovingObject> layerGroup : layerGroups) {
			for (AbstractMovingObject obj : layerGroup) {
				obj.effectEnableScale(effectScale);
			}
		}
	}

	public void tapEffect(int eventType, boolean isTapDown) {

		for (LinkedList<AbstractMovingObject> layerGroup : layerGroups) {
			for (AbstractMovingObject obj : layerGroup) {
				obj.tapEvent(eventType, isTapDown);
			}
		}
	}

	public void dragEffect(int eventType, float amount) {

		for (LinkedList<AbstractMovingObject> layerGroup : layerGroups) {
			for (AbstractMovingObject obj : layerGroup) {
				obj.dragEvent(eventType, amount);
			}
		}
	}

	public void drawPlayingLayers(PGraphics g, ColorScheme colorScheme) {
		int curColor = 0;
		
		for (LinkedList<AbstractMovingObject> layerGroup : layerGroups) {
			for (AbstractMovingObject obj : layerGroup) {
				if (obj.isPlaying())
				{
					obj.setColor(colorScheme.getColor(curColor));
					obj.draw(g);
				}
			}
			curColor++;
		}
	}

	public void effectEnableDuration(float duration) {
		for (LinkedList<AbstractMovingObject> layerGroup : layerGroups) {
			for (AbstractMovingObject obj : layerGroup) {
				obj.setDuration(duration);
			}
		}
		
	}

	public void effectEnableDistance(int distance) {
		for (LinkedList<AbstractMovingObject> layerGroup : layerGroups) {
			for (AbstractMovingObject obj : layerGroup) {
				obj.setDistance(distance);
			}
		}
		
	}

}
