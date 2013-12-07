package floader.visuals;

public final class VisualConstants {
	
	public static final int OBJECT_EVENT_CHANNEL = 1;
	public static final int CAM_EVENT_CHANNEL = 2;
	public static final String OSC_CTRL_PATH = "/mtn/ctrl";
	public static final String OSC_NOTE_PATH = "/mtn/note";
	public static final boolean CAM_ENABLED = true;
	public static final boolean PROSCENE_GUIDES_ENABLED = false;
	public static final String MIDI_DEVICE = "5. Internal MIDI";
	//public static final String MIDI_DEVICE = "nanoKONTROL2";
	public static final boolean NANOKONTROL2_ENABLED = false;
	public static final boolean MONOMEMIDI_ENABLED = true;
	public static final boolean COMPUTERKEYBOARD_ENABLED = true;
	
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 960;
	/*public static final int WIDTH = 4000;
	public static final int HEIGHT =  3000;*/
	public static final boolean FULLSCREEN = false;
	
	//Global Effects
	public static final int GLOBAL_EFFECT_BLUR = 0;
	public static final int GLOBAL_EFFECT_CAMDISTANCE = 1;
	public static final int GLOBAL_EFFECT_PERSPECTIVE = 2;
	public static final int GLOBAL_EFFECT_SCALE = 3;
	public static final int GLOBAL_EFFECT_ROTATEX = 4;
	public static final int GLOBAL_EFFECT_ROTATEY = 5;
	public static final int GLOBAL_EFFECT_ROTATEZ = 6;
	//Global Triggers
	public static final int GLOBAL_TRIGGER_CUBE = 7;
	public static final int GLOBAL_TRIGGER_CAPTUREBG = 8;
	public static final int GLOBAL_TRIGGER_EDGEDETECTION = 9;
	public static final int GLOBAL_TRIGGER_MIRROR = 10;
	public static final int GLOBAL_TRIGGER_TOGGLEBGFILL = 11;
	public static final int GLOBAL_TRIGGER_RESET = 12;
	public static final int GLOBAL_TRIGGER_CYCLECOLORSCHEME = 17;
	//Global Scenes
	public static final int GLOBAL_SCENE_RECTANGLES = 13;
	public static final int GLOBAL_SCENE_PERCENTAGES = 14;
	public static final int GLOBAL_SCENE_SPINCYCLE = 15;
	public static final int GLOBAL_SCENE_FLYINGOBJECTS = 16;
}
