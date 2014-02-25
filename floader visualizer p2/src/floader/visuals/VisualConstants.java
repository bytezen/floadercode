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
	public static final boolean NANOKONTROL2MIDI_ENABLED = false;
	
	public static final boolean ABLETON_OSC_NANOKONTROL_ENABLED = true;
	public static final int ABLETON_OSC_NANOKONTROL_CHANNEL = 2;

	public static final boolean ABLETON_OSC_NOTE_ENABLED = true;
	public static final int ABLETON_OSC_NOTE_CHANNEL = 0;
	
	public static final boolean ABLETON_OSC_CTRL_ENABLED = true;
	public static final int ABLETON_OSC_CTRL_CHANNEL = 1;
	
	public static final boolean MONOMEMIDI_ENABLED = true;
	public static final boolean COMPUTERKEYBOARD_ENABLED = true;
	
	public static final int OSC_CTRL_INDEX = 1;
	public static final int OSC_VALUE_INDEX = 0;
	public static final int OSC_CHANNEL_INDEX = 2;
	
	public static final int WIDTH = 1024;
	public static final int HEIGHT = 768;
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
	public static final int GLOBAL_EFFECT_CLIPX = 18;
	public static final int GLOBAL_EFFECT_CLIPY = 19;
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
	public static final int GLOBAL_SCENE_KINECT = 20;
	//Local Effects
	public static final int LOCAL_EFFECT_1 = 100;
	public static final int LOCAL_EFFECT_2 = 101;
	public static final int LOCAL_EFFECT_3 = 102;
	public static final int LOCAL_EFFECT_4 = 103;
	public static final int LOCAL_EFFECT_5 = 104;
	public static final int LOCAL_EFFECT_6 = 105;
	public static final int LOCAL_EFFECT_7 = 106;
	public static final int LOCAL_EFFECT_8 = 107;
	public static final int LOCAL_EFFECT_START_INDEX = LOCAL_EFFECT_1;
	
	public static boolean isGlobalEffect(int effectIndex)
	{
		if(effectIndex >= LOCAL_EFFECT_START_INDEX)
			return false;
		else return true;
	}
	
}
