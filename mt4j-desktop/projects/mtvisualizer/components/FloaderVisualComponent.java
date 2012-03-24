package mtvisualizer.components;


import org.mt4j.components.visibleComponents.AbstractVisibleComponent;

import floader.visuals.*;
import floader.visuals.flyingobjects.FlyingObjectsVisual;
import floader.visuals.tearsfordears.TearsForDearsVisual;

import oscP5.OscMessage;
import processing.core.PApplet;
import processing.core.PGraphics;

public class FloaderVisualComponent extends AbstractVisibleComponent {

	IVisual viz;
	PApplet app;

	public FloaderVisualComponent(PApplet app, String visualName) {
		super(app);
		this.app = app;
		viz = VisualFactory.getVisual(app, visualName);
		viz.setup();
	}

	public void drawComponent(PGraphics g) {
		viz.draw();
	}

	public void oscEvent(OscMessage msg) {
		viz.oscEvent(msg);
	}

	public static class VisualFactory {
		public static IVisual getVisual(PApplet app, String name) {
			if (name.equals(floader.visuals.flyingobjects.FlyingObjectsVisual.class.getName())) {
				return new FlyingObjectsVisual(app);
			} else if (name.equals(floader.visuals.tearsfordears.TearsForDearsVisual.class.getName())) {
				return new TearsForDearsVisual(app);
			} else {
				System.err.println("Error: undefined visual named: " + name);
				return null;
			}

		}
	}
}
