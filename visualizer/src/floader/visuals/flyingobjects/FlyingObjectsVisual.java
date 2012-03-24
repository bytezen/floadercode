package floader.visuals.flyingobjects;

import java.io.*;



import processing.core.*;
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

public class FlyingObjectsVisual {

	WB_Render render;
	PeasyCam cam;
	LayerGroup layerGroup;
	PApplet app;

	public static final int OBJECT_EVENT_CHANNEL = 1;
	public static final int CAM_EVENT_CHANNEL = 2;
	public static final int LAYER_SIZE = 11;
	private float baseDuration = 10;

	public FlyingObjectsVisual(PApplet app) {
		this.app = app;
		cam = new PeasyCam(app, 500);
	}

	public void setup() {
		
		cam.setMinimumDistance(100);
		cam.setMaximumDistance(500);
		cam.setDistance(400);
		render = new WB_Render(app);
		layerGroup = new LayerGroup();
		
		//turn off touch rotation
		//cam.setActive(false);
		
		layerGroup.addLayer(new SendCylinderLayer(4, baseDuration, 0, 0, app, render), 0);
		layerGroup.addLayer(new SendCylinderLayer(4, baseDuration, 0, 0, app, render), 1);
		layerGroup.addLayer(new SendCylinderLayer(4, baseDuration, 0, 0, app, render), 2);
		layerGroup.addLayer(new SendCylinderLayer(4, baseDuration, 0, 0, app, render), 3);

		layerGroup.addLayer(new SendSphereLayer(2, baseDuration, -100, 0, app, render), 4);
		layerGroup.addLayer(new SendSphereLayer(2, baseDuration, -100, 0, app, render), 5);

		layerGroup.addLayer(new SendConeLayer(6, baseDuration, -200, -200, app, render), 6);
		layerGroup.addLayer(new SendConeLayer(6, baseDuration, -200, -100, app, render), 7);
		layerGroup.addLayer(new SendConeLayer(6, baseDuration, -200, 0, app, render), 8);
		layerGroup.addLayer(new SendConeLayer(6, baseDuration, -200, 100, app, render), 9);
		layerGroup.addLayer(new SendConeLayer(6, baseDuration, -200, 200, app, render), 10);
	}

	public void draw() {
		app.lights();
		cam.feed();
		for (int i = 0; i < layerGroup.getLayerCount(); i++) {
			if (layerGroup.getLayer(i).isPlaying())
				layerGroup.getLayer(i).draw();
		}
	}

	public void keyPressed() {

		if (app.keyCode == 49) {
			if (!layerGroup.getLayer(0).isPlaying())
				layerGroup.getLayer(0).play();
		} else if (app.keyCode == 50) {

			/*
			 * CameraState state = cam.getState(); // get a serializable
			 * settings // object for current state
			 * 
			 * FileOutputStream f_out; ObjectOutputStream obj_out; try { f_out =
			 * new FileOutputStream("data\\camState" + camCounter); obj_out =
			 * new ObjectOutputStream(f_out); // Write object out to disk
			 * obj_out.writeObject(state); } catch (IOException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } camCounter++;
			 */

		}
	}

	public void keyReleased() {
		for (int i = 0; i < layerGroup.getLayerCount(); i++) {
			layerGroup.getLayer(i).stop();
		}
	}

	void loadCamState(int index) {
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

	public void oscEvent(OscMessage msg) {

		if (msg.checkAddrPattern("/mtn/note")) {
			int note = msg.get(0).intValue();
			int vel = msg.get(1).intValue();
			int chan = msg.get(2).intValue();

			if (chan == OBJECT_EVENT_CHANNEL) {
				// Escape if incoming note is higher than max number of layers
				if (note < LAYER_SIZE) {
					if (vel > 0) {
						layerGroup.getLayer(note).play();
					} else if (vel == 0) {
						layerGroup.getLayer(note).stop();
					}
				}
			} else if (chan == CAM_EVENT_CHANNEL) {
				if (vel > 0)
					loadCamState(note);
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
