package floader.visuals.turingfractal;

import java.util.Iterator;

import floader.visuals.AbstractVisual;
import floader.visuals.IVisual;
import floader.visuals.VisualConstants;

import wblut.hemesh.modifiers.*;
import wblut.hemesh.creators.*;
import wblut.hemesh.*;
import oscP5.*;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PImage;
import wblut.hemesh.core.*;
import wblut.core.processing.*;

@SuppressWarnings("serial")
public class TuringFractalVisual extends AbstractVisual implements IVisual {
	PApplet app;
	
	//turing variables
	int n, levels;
	float[] grid;
	float[] diffusionLeft, diffusionRight, blurBuffer, variation;
	float[] bestVariation;
	int[] bestLevel;
	boolean[] direction;
	float[] stepSizes;
	int[] radii;
	PImage buffer;
	int width = 256;
	int height = 192;
	
	
	
	

	public TuringFractalVisual(PApplet app) {
		this.app = app;
		camStatePath = "data\\kalimba\\camState";
	}

	public void setup() {
		 // relates to the complexity (how many blur levels)
		  float base = app.random(1.5f, 2.3f); // should be between 1.3 and 3
		   
		  // these values affect how fast the image changes
		  float stepScale = app.random(.006f, .011f);
		  float stepOffset =  app.random(.007f, .012f);
		 
		  // allocate space
		  n = width * height;
		  levels = (int) (PApplet.log(width) / PApplet.log(base));
		  radii = new int[levels];
		  stepSizes = new float[levels];
		  grid = new float[n];
		  diffusionLeft = new float[n];
		  diffusionRight = new float[n];
		  blurBuffer = new float[n];
		  variation = new float[n];
		  bestVariation = new float[n];
		  bestLevel = new int[n];
		  direction = new boolean[n];
		  buffer = app.createImage(width, height, PApplet.RGB);
		 
		  // determines the shape of the patterns
		  for (int i = 0; i < levels; i++) {
		    int radius = (int) PApplet.pow(base, i);
		    radii[i] = radius;
		    stepSizes[i] = PApplet.log(radius) * stepScale + stepOffset;
		  }
		 
		  // initialize the grid with noise
		  for (int i = 0; i < n; i++) {
		    grid[i] = app.random(-1, +1);
		  }
	
		cam = new PeasyCam(app, 0);
		cam.setMaximumDistance(1000);
		//cam.setDistance(120);
		cam.setDistance(400);
		cam.setActive(true);
	}





	public void draw() {
		cam.feed();
		step();
		drawBuffer(grid);


	}

	@Override
	public void dragEvent(int eventType, float amount) {

	}

	@Override
	public void tapEvent(int eventType, boolean isTapDown) {

	}

	@Override
	public void noteObjEvent(int note, int velocity) {

	}

	@Override
	public void ctrlEvent(int num, int val, int chan) {
		// TODO Auto-generated method stub

	}

	@Override
	public void noteCamEvent(int note, int vel) {

	}
	
	//TURING
	void step() {
		  float[] activator = grid;
		  float[] inhibitor = diffusionRight;
		 
		  for (int level = 0; level < levels - 1; level++) {
		    // blur activator into inhibitor
		    int radius = radii[level];   
		    blur(activator, inhibitor, blurBuffer, width, height, radius);
		 
		    // absdiff between activator and inhibitor
		    for (int i = 0; i < n; i++) {
		      variation[i] = activator[i] - inhibitor[i];
		      if (variation[i] < 0) {
		        variation[i] = -variation[i];
		      }
		    }
		 
		    if (level == 0) {
		      // save bestLevel and bestVariation
		      for (int i = 0; i < n; i++) {
		        bestVariation[i] = variation[i];
		        bestLevel[i] = level;
		        direction[i] = activator[i] > inhibitor[i];
		      }
		      activator = diffusionRight;
		      inhibitor = diffusionLeft;
		    }
		    else {
		      // check/save bestLevel and bestVariation
		      for (int i = 0; i < n; i++) {
		        if (variation[i] < bestVariation[i]) {
		          bestVariation[i] = variation[i];
		          bestLevel[i] = level;
		          direction[i] = activator[i] > inhibitor[i];
		        }
		      }
		      float[] swap = activator;
		      activator = inhibitor;
		      inhibitor = swap;
		    }
		  }
		 
		  // update grid from bestLevel
		  float smallest = Float.POSITIVE_INFINITY;
		  float largest = Float.NEGATIVE_INFINITY;
		  for (int i = 0; i < n; i++) {
		    float curStep = stepSizes[bestLevel[i]];
		    if (direction[i]) {
		      grid[i] += curStep;
		    }
		    else {
		      grid[i] -= curStep;
		    }
		    smallest = PApplet.min(smallest, grid[i]);
		    largest = PApplet.max(largest, grid[i]);
		  }
		 
		  // normalize to [-1, +1]
		  float range = (largest - smallest) / 2;
		  for (int i = 0; i < n; i++) {
		    grid[i] = ((grid[i] - smallest) / range) - 1;
		  }
		}
		 
		void drawBuffer(float[] grid) {
		  buffer.loadPixels();
		  int[] pixels = buffer.pixels;
		  for (int i = 0; i < n; i++) {
		    pixels[i] = app.color(128 + 128 * grid[i]);
		  }
		  buffer.updatePixels();
		  app.image(buffer, -VisualConstants.WIDTH/8  , -VisualConstants.HEIGHT/8);
		  app.image(buffer, -VisualConstants.WIDTH/8  , VisualConstants.HEIGHT/8);
		  app.image(buffer, VisualConstants.WIDTH/8  , -VisualConstants.HEIGHT/8);
		  app.image(buffer, VisualConstants.WIDTH/8  , VisualConstants.HEIGHT/8);
		  
		}
		 
		 
		void mousePressed() {
		  setup();
		}


		void blur(float[] from, float[] to, float[] buffer, int w, int h, int radius) {
		  // build integral image
		  for (int y = 0; y < h; y++) {
		    for (int x = 0; x < w; x++) {
		      int i = y * w + x;
		      if (y == 0 && x == 0) {
		        buffer[i] = from[i];
		      } else if (y == 0) {
		        buffer[i] = buffer[i - 1] + from[i];
		      } else if (x == 0) {
		        buffer[i] = buffer[i - w] + from[i];
		      } else {
		        buffer[i] = buffer[i - 1] + buffer[i - w] - buffer[i - w - 1] + from[i];
		      }
		    }
		  }
		  // do lookups
		  for (int y = 0; y < h; y++) {
		    for (int x = 0; x < w; x++) {
		      int minx = PApplet.max(0, x - radius);
		      int maxx = PApplet.min(x + radius, w - 1);
		      int miny = PApplet.max(0, y - radius);
		      int maxy = PApplet.min(y + radius, h - 1);
		      int area = (maxx - minx) * (maxy - miny);
		       
		      int nw = miny * w + minx;
		      int ne = miny * w + maxx;
		      int sw = maxy * w + minx;
		      int se = maxy * w + maxx;
		       
		      int i = y * w + x;
		      to[i] = (buffer[se] - buffer[sw] - buffer[ne] + buffer[nw]) / area;
		    }
		  }
		}


}
