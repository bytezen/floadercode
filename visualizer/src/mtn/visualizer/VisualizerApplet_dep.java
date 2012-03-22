package mtn.visualizer;

import java.io.*;

import mtn.visualizer.objects.SendConeLayer;
import mtn.visualizer.objects.SendCylinderLayer;
import mtn.visualizer.objects.SendSphereLayer;

import processing.core.*;
import ijeoma.motion.*;
import ijeoma.motion.tween.*;
import wblut.geom.core.*;
import wblut.hemesh.creators.*;
import wblut.hemesh.tools.*;
import wblut.geom.grid.*;
import wblut.geom.nurbs.*;
import wblut.core.math.*;
import wblut.hemesh.subdividors.*;
import wblut.core.processing.*;
import wblut.hemesh.composite.*;
import wblut.core.random.*;
import wblut.hemesh.core.*;
import wblut.geom.frame.*;
import wblut.core.structures.*;
import wblut.hemesh.modifiers.*;
import wblut.hemesh.simplifiers.*;
import wblut.geom.triangulate.*;
import wblut.geom.tree.*;
import processing.opengl.*;
import peasy.*;
import oscP5.*;

public class VisualizerApplet_dep extends PApplet {

	WB_Render render;
	public static final int OSC_PORT = 7400;
	OscP5 oscP5;
	PeasyCam cam;
	LayerGroup layerGroup;
	int camCounter = 3;

	public void setup() {
		size(1024, 768, OPENGL);
		oscP5 = new OscP5(this, OSC_PORT);
		Motion.setup(this);
		render = new WB_Render(this);
		cam = new PeasyCam(this, 600);
		cam.pan(0, -200);
		cam.setMinimumDistance(0);
		cam.setMaximumDistance(500);

		layerGroup = new LayerGroup();

		layerGroup.addLayer(new SendCylinderLayer(4, 200, 0, 0, this, render), 0);
		layerGroup.addLayer(new SendCylinderLayer(4, 200, 0, 0, this, render), 1);
		layerGroup.addLayer(new SendCylinderLayer(4, 200, 0, 0, this, render), 2);
		layerGroup.addLayer(new SendCylinderLayer(4, 200, 0, 0, this, render), 3);

		layerGroup.addLayer(new SendSphereLayer(2, 200, -100, 0, this, render), 4);
		layerGroup.addLayer(new SendSphereLayer(2, 200, -100, 0, this, render), 5);

		layerGroup.addLayer(new SendConeLayer(6, 200, -200, -200, this, render), 6);
		layerGroup.addLayer(new SendConeLayer(6, 200, -200, -100, this, render), 7);
		layerGroup.addLayer(new SendConeLayer(6, 200, -200, 0, this, render), 8);
		layerGroup.addLayer(new SendConeLayer(6, 200, -200, 100, this, render), 9);
		layerGroup.addLayer(new SendConeLayer(6, 200, -200, 200, this, render), 10);
	}

	public void draw() {
		if (!keyPressed)
			background(58, 57, 47);
		lights();
		// Remember to use this when adding in MT4J
		/*
		 * cam.beginHUD(); cam.endHUD();
		 */

		for (int i = 0; i < layerGroup.getLayerCount(); i++) {
			if (layerGroup.getLayer(i).isPlaying())
				layerGroup.getLayer(i).draw();
		}

	}

	public void keyPressed() {

		if (keyCode == 49) {
			if (!layerGroup.getLayer(0).isPlaying())
				layerGroup.getLayer(0).play();
		} else if (keyCode == 50) {

			/*CameraState state = cam.getState(); // get a serializable settings
												// object for current state

			FileOutputStream f_out;
			ObjectOutputStream obj_out;
			try {
				f_out = new FileOutputStream("data\\camState" + camCounter);
				obj_out = new ObjectOutputStream(f_out);
				// Write object out to disk
				obj_out.writeObject(state);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			camCounter++;*/

		} 
	}

	public void keyReleased() {
		for (int i = 0; i < layerGroup.getLayerCount(); i++) {
			layerGroup.getLayer(i).stop();
		}
	}
	
	void loadCamState(int index)
	{
		// Read from disk using FileInputStream
		FileInputStream f_in;
		try {
			f_in = new FileInputStream("data\\camState" + index);
			// Read object using ObjectInputStream
			ObjectInputStream obj_in = new ObjectInputStream(f_in);

			// Read an object
			Object obj = obj_in.readObject();

			if (obj instanceof CameraState) {
				// Cast object to a Vector
				CameraState state = (CameraState) obj;
				cam.setState(state);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void oscEvent(OscMessage msg) {

		if (msg.checkAddrPattern("/mtn/note")) {
			int note = msg.get(0).intValue();
			int vel = msg.get(1).intValue();
			int chan = msg.get(2).intValue();

			// Objects
			if (chan == 0) {
				// Escape if incoming note is higher than max number of layers
				if (note < layerGroup.getLayerCount()) {
					// NoteOn
					if (vel > 0) {
						layerGroup.getLayer(note).play();
					}
					// NoteOff
					else if (vel == 0) {
						layerGroup.getLayer(note).stop();
					}
				}
			}
			// Camera
			else if (chan == 1) {
				if(vel>0)loadCamState(note);
			}
		} else if (msg.checkAddrPattern("/mtn/ctrl")) {
			int ctrlNum = msg.get(0).intValue();
			int ctrlVal = msg.get(1).intValue();

			// Extrude
			if (ctrlNum == 1) {
				// layerGroup.effectEnableExtrude((float)ctrlVal / 127.0f);
			}
			// Twist
			else if (ctrlNum == 2) {
				layerGroup.effectEnableTwistX((float) ctrlVal / 127.0f);
			}
			// Scale
			else if (ctrlNum == 3) {
				layerGroup.effectEnableScale((float) ctrlVal / 127.0f);
			}
		}
	}

}
