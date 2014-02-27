package floader.visuals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import floader.visuals.colorschemes.ColorScheme;
import floader.visuals.colorschemes.Terminal;
import peasy.CameraState;
import peasy.PeasyCam;
import processing.core.PGraphics;

public class AbstractVisual  implements IVisual {
	protected String camStatePath;
	protected PeasyCam cam;
	protected int camCounter = 0;
	protected ColorScheme curColorScheme;
	
	@Override
	public void setup(){

	}
	
	@Override
	public void draw(PGraphics g)
	{
	}
	
	protected void loadCamState(int index) {
		loadCamState(index, 300);
	}

	public void setColorScheme(ColorScheme newColorScheme)
	{
		curColorScheme = newColorScheme;
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

	@Override
	public void camEvent(int camState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctrlEvent(int index, float val) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragEvent(int eventType, float amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tapEvent(int eventType, boolean isTapDown) {
		// TODO Auto-generated method stub
		
	}
	


	@Override
	public void reset() {

		
	}

	@Override
	public void scale(float amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotateX(float amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotateY(float amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotateZ(float amount) {
		// TODO Auto-generated method stub
		
	}


	
	
}
