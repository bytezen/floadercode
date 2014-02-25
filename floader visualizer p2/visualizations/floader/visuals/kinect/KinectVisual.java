package floader.visuals.kinect;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import floader.visuals.AbstractVisual;

public class KinectVisual extends AbstractVisual {
	PApplet app;
	SimpleOpenNI kinect;
	
	public KinectVisual(PApplet app)
	{
		this.app = app;
	}
	
	public void setup() {
		super.setup();
		kinect = new SimpleOpenNI(app);
		if(kinect.isInit() == false)
		  {
		     System.err.println("Can't init SimpleOpenNI, maybe the camera is not connected!"); 
		     return;  
		  }
		  
		  // enable depthMap generation 
		  kinect.enableDepth();
		  
		  // enable ir generation
		  kinect.enableIR();
	}
	
	public void draw(PGraphics g) {
		  // update the cam
		  kinect.update();
		  
		  // draw depthImageMap
		  PImage newImage = kinect.depthImage();
//		  newImage.height= 768;
//		  newImage.width = 1024;
		  newImage.resize(1024, 768);
		  g.image(newImage,0,0);
	}
	
}
