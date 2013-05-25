package floader.visuals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import peasy.CameraState;
import peasy.PeasyCam;

public class AbstractVisual {
	protected String camStatePath;
	protected PeasyCam cam;
	protected int camCounter = 0;
	
	protected void loadCamState(int index) {
		loadCamState(index, 300);
	}
	
	protected void loadCamState(int index, int time) {
		// Read from disk using FileInputStream
		FileInputStream f_in;
		try {
			f_in = new FileInputStream(camStatePath + index);
			// Read object using ObjectInputStream
			ObjectInputStream obj_in = new ObjectInputStream(f_in);

			// Read an object
			Object obj = obj_in.readObject();

			if (obj instanceof CameraState) {
				// Cast object to a Vector
				CameraState state = (CameraState) obj;
				cam.setState(state, time);
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
	
	protected void writeCamState()
	{
		CameraState state = cam.getState(); // get a serializable settings //
		// object for current state

		FileOutputStream f_out;
		ObjectOutputStream obj_out;
		try {
			f_out = new FileOutputStream(camStatePath + camCounter);
			obj_out = new ObjectOutputStream(f_out); // Write object out to disk
			obj_out.writeObject(state);
		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		camCounter++;
	}
}
