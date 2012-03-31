package mtvisualizer.components;


import org.mt4j.components.visibleComponents.AbstractVisibleComponent;

import floader.visuals.*;
import floader.visuals.flyingobjects.FlyingObjectsVisual;
import floader.visuals.flyingobjects.LeakierPhysicsVisual;
import floader.visuals.hangon.AvanteHangOnVisual;
import floader.visuals.hangon.HangOnVisual;
import floader.visuals.imagineyourgarden.ImagineYourGardenVisual;
import floader.visuals.kalimba.KalimbaVisual;
import floader.visuals.percentages.Percentages;
import floader.visuals.tearsfordears.TearsForDearsVisual;

import oscP5.OscMessage;
import processing.core.PApplet;
import processing.core.PGraphics;

public class VisualizationComponent extends AbstractVisibleComponent {

	IVisual viz;
	PApplet app;

	public VisualizationComponent(PApplet app, String visualName) {
		super(app);
		this.app = app;
		viz = VisualFactory.getVisual(app, visualName);
		viz.setup();
	}

	@Override
	public void drawComponent(PGraphics g) {
		viz.draw();
	}

	public IVisual getIVisual()
	{
		return viz;
	}
	
	public static class VisualFactory {
		public static IVisual getVisual(PApplet app, String name) {
			if (name.equals(floader.visuals.flyingobjects.FlyingObjectsVisual.class.getName())) {
				return new FlyingObjectsVisual(app);
			} else if (name.equals(floader.visuals.tearsfordears.TearsForDearsVisual.class.getName())) {
				return new TearsForDearsVisual(app);
			} else if (name.equals(floader.visuals.hangon.AvanteHangOnVisual.class.getName())) {
				return new AvanteHangOnVisual(app);
			} else if (name.equals(floader.visuals.imagineyourgarden.ImagineYourGardenVisual.class.getName())) {
				return new ImagineYourGardenVisual(app);
			} else if (name.equals(floader.visuals.flyingobjects.LeakierPhysicsVisual.class.getName())) {
				return new LeakierPhysicsVisual(app);
			} else if (name.equals(floader.visuals.kalimba.KalimbaVisual.class.getName())) {
				return new KalimbaVisual(app);
			} else if (name.equals(floader.visuals.percentages.Percentages.class.getName())) {
				return new Percentages(app);
			} else {
				System.err.println("Error: undefined visual named: " + name);
				return null;
			}
			
			
			
			
		}
	}
}
