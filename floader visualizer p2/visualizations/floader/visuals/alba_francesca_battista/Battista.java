package floader.visuals.alba_francesca_battista;

import processing.core.PApplet;
import processing.core.PGraphics;
import floader.visuals.AbstractVisual;
import floader.visuals.VisualConstants;

public class Battista extends AbstractVisual {

	PApplet app;
	int x = 20;
	float y = 50;
	int i = 0;
	int j = 0;
	int larghezza = 320;
	int lunghezza = 60;
	int strokeWeight = 3;
	int maxStrokeWeight = 8;
	int vicini = 80;
	int offset = 50;
	float A;
	float G;
	float scale = .5f;
	boolean animate = true;
	int height;

	public Battista(PApplet app) {
		this.app = app;
	}

	public void setup() {
		height = VisualConstants.HEIGHT + 400;
	}

	public void draw(PGraphics g) {
		g.translate((int)(-VisualConstants.WIDTH/3), (int)(-VisualConstants.HEIGHT/1.3));
		
		

		g.smooth();
		g.stroke(curColorScheme.getColor(0).getRGB());

		if (!animate) {
			y = 50;
			x = 20;
		}

		g.strokeWeight(strokeWeight);

		for (int i = -50; i < lunghezza; i = i + (int) app.random(2, 3)) {
			for (int j = 99 - i; j < larghezza; j = j + (int) app.random(1, 6)) {
				x = j - (i / 3) + vicini;
				if (y < 180 && x > (235 + 2)) {
				} else {

					g.point(x, y);
					g.point(VisualConstants.WIDTH - x, height
							- y);
				}
			}

			// y = (y<=height ? y=y+(int)4/*random(2,28)*/+j+2 : 0);
			// horizontal scale
			y = (y <= height ? y = y + scale + j : 0);
		}
		
		g.translate(-200,0);
		for (int i = -50; i < lunghezza; i = i + (int) app.random(2, 3)) {
			for (int j = 99 - i; j < larghezza; j = j + (int) app.random(1, 6)) {
				x = j - (i / 3) + vicini;
				if (y < 180 && x > (235 + 2)) {
				} else {

					g.point(x, y);
					g.point(VisualConstants.WIDTH - x, height
							- y);
				}
			}

			// y = (y<=height ? y=y+(int)4/*random(2,28)*/+j+2 : 0);
			// horizontal scale
			y = (y <= height ? y = y + scale + j : 0);
		}
	}
	
public void ctrlEvent(int index, float val) {
		
		switch(index)
		{
		case VisualConstants.LOCAL_EFFECT_1:
			//movement amount
			scale = val * 40;
			break;
		case VisualConstants.LOCAL_EFFECT_2:
			//movement amount
			strokeWeight = (int)(val * maxStrokeWeight) + 1;
			break;
			
		case VisualConstants.LOCAL_EFFECT_3:
			//movement amount
			if(val<.5)
				animate = false;
			else animate = true;
			break;
		
		
		default:
			System.err.println("Unrecognized effect " + index + " sent to Neveling ctrlEvent");
			break;
		}
	
}
}
