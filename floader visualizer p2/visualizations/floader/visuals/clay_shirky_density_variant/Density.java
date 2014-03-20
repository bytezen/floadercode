package floader.visuals.clay_shirky_density_variant;

import processing.core.PApplet;
import processing.core.PGraphics;
import floader.visuals.AbstractVisual;
import floader.visuals.VisualConstants;

public class Density extends AbstractVisual {

	PApplet app;
	int diameter;
	int radius;
	int maxNumRows = 4;
	int numRows = maxNumRows;
	int newNumRows = 1;
	int strokeWeight = 2;
	int maxStrokeWeight = 5;
	
	int numLines = 100;
	int min, max;
	float movement = 0;
	float movementAmount = .007f;
	float maxMovementAmount = .05f;
	float scalar;
	float noiseStart[][][];
	float noiseEnd[][][];

	public Density(PApplet app) {
		this.app = app;
	}

	public void setup() {

		min = 1;
		max = 10000;
		noiseStart = new float[numLines][numRows][numRows];
		noiseEnd = new float[numLines][numRows][numRows];
		for (int i = 0; i < numLines; i++)
			for (int j = 0; j < numRows; j++)
				for (int k = 0; k < numRows; k++) {
					// noiseStart[i][j][k] = random(0, TWO_PI);
					// noiseEnd[i][j][k] = random(0, TWO_PI);
					noiseStart[i][j][k] = app.random(0, PApplet.TWO_PI);
					noiseEnd[i][j][k] = app.noise((float) i / 100,
							(float) j / 100, (float) k / 100) * PApplet.TWO_PI;
				}

	}

	public void draw(PGraphics g) {
		g.translate((int)(-VisualConstants.WIDTH/2.5), (int)(-VisualConstants.HEIGHT/2));
		g.noFill();
		
		g.strokeWeight(strokeWeight);
		g.stroke(curColorScheme.getColor(0).getRGB());

		numRows = newNumRows;
		movement += movementAmount;

		diameter = (VisualConstants.HEIGHT / numRows);
		radius = diameter / 2;

		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numRows; c++) {
				g.pushMatrix();
				g.translate(radius + ((radius * 2) * c), radius
						+ ((radius * 2) * r));

				// Draw lines
				for (int i = 0; i < numLines; i++) {
					// float a = random(0, TWO_PI);

					float a;
					a = noiseStart[i][r][c]
							+ (app.noise(movement + app.noise((float) r / 100)
									+ app.noise(i)) * PApplet.TWO_PI);
					float x1 = radius * PApplet.cos(a);
					float y1 = radius * PApplet.sin(a);
					// a = random(0, TWO_PI);
					a = noiseEnd[i][r][c]
							+ (app.noise(movement - app.noise((float) r / 100)
									+ app.noise(i)) * PApplet.TWO_PI);
					float x2 = radius * PApplet.cos(a);
					float y2 = radius * PApplet.sin(a);

					g.line(x1, y1, x2, y2);
				}
				g.popMatrix();
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
			newNumRows = (int)(val * (maxNumRows - 1)) + 1;
			break;
		case VisualConstants.LOCAL_EFFECT_3:
			//square size
			strokeWeight = (int)(val * maxStrokeWeight) + 1;
			break;
		default:
			System.err.println("Unrecognized effect " + index + " sent to Neveling ctrlEvent");
			break;
		}
	
}

}