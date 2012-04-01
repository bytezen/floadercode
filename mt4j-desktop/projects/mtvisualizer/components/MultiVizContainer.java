package mtvisualizer.components;

import floader.visuals.IVisual;

public class MultiVizContainer implements IVisual{
	
	IVisual viz1;
	IVisual viz2;
	
	public MultiVizContainer(IVisual viz1, IVisual viz2)
	{
		this.viz1=viz1;
		this.viz2=viz2;
	}

	@Override
	public void setup() {
		viz1.setup();
		viz2.setup();
		
	}

	@Override
	public void draw() {
		viz1.draw();
		viz2.draw();
	}

	@Override
	public void noteObjEvent(int note, int vel) {
		viz1.noteObjEvent(note, vel);
		viz2.noteObjEvent(note, vel);
		
	}

	@Override
	public void noteCamEvent(int note, int vel) {
		viz1.noteCamEvent(	note, vel);
		viz2.noteCamEvent(note, vel);
	}

	@Override
	public void ctrlEvent(int num, int val, int chan) {
		viz1.ctrlEvent(num, val, chan);
		viz2.ctrlEvent(num, val, chan);
		
	}

	@Override
	public void dragEvent(int eventType, float amount) {
		viz1.dragEvent(eventType, amount);
		viz2.dragEvent(eventType, amount);
		
	}

	@Override
	public void tapEvent(int eventType, boolean isTapDown) {
		viz1.tapEvent(eventType, isTapDown);
		viz2.tapEvent(eventType, isTapDown);
		
	}

}
