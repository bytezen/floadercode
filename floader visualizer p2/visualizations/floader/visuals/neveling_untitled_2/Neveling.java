package floader.visuals.neveling_untitled_2;

import processing.core.PApplet;
import processing.core.PGraphics;
import floader.visuals.AbstractVisual;
import floader.visuals.VisualConstants;

public class Neveling extends AbstractVisual {

	int rows = 9;
	float offsetX;
	float offsetY;
	int sqSize;
	float sizeDifference = 5;
	float movement;
	float movementAmount = .01f;
	float maxMovementAmount = .1f;
	PApplet app;

	public Neveling(PApplet app) {
		this.app = app;
	}

	public void setup() {
		offsetX = 3;
		offsetY = 3;

		sqSize = VisualConstants.WIDTH / rows;

		// animate this
		// sizeDifference = 1;
	}

	public void draw(PGraphics g) {
		g.translate(-VisualConstants.WIDTH/2, (int)(-VisualConstants.HEIGHT/1.5));
		
		g.rectMode(PGraphics.CENTER);
		g.strokeWeight(1);
		//g.stroke(curColorScheme.getColor(3).getRGB());
		g.stroke(0);
		g.fill(curColorScheme.getColor(0).getRGB());

		movement += movementAmount;
		// sizeDifference = ((float)mouseY/height * 10);

		for (int r = 1; r < rows; r++) {
			// for every column...
			for (int c = 1; c < rows; c++) {
				// choose a new offset
				offsetX = app.noise(r + movement) * 3 - 3; // random(-3,3);
				offsetY = app.noise(c + movement) * 3 - 3; // random(-3,3);
				// Draw grid
				g.rect(c * sqSize, r * sqSize, sqSize, sqSize);

				for (int i = 1; i < 20; i++) {
					g.strokeWeight((int) (app.noise(r + movement) * 5 + .4));
					g.rect((c * sqSize) + (i * offsetX), (r * sqSize)
							+ (i * offsetY), sqSize - (i * sizeDifference),
							sqSize - (i * sizeDifference));
				}
			}
		}

	}
	
	public void ctrlEvent(int index, float val) {
		
		switch(index)
		{
		case VisualConstants.LOCAL_EFFECT_1:
			//movement amount
			movementAmount = val * maxMovementAmount;
			break;
		case VisualConstants.LOCAL_EFFECT_2:
			//square size
			sizeDifference = val * 5.7f;
			break;
		default:
			System.err.println("Unrecognized effect " + index + " sent to Neveling ctrlEvent");
			break;
		}
}

}
