package floader.visuals.hardwarecontrollers;

import floader.visuals.VisualConstants;

public class ComputerKeyboard extends HardwareController {
	public static int convertKeyPress(int key)
	{
		switch(key){
		case 'x':
			return VisualConstants.GLOBAL_TRIGGER_CUBE;
		case 'r':
			return VisualConstants.GLOBAL_TRIGGER_RESET;
		case 'c':
			return VisualConstants.GLOBAL_TRIGGER_CAPTUREBG;
		case 't':
			return VisualConstants.GLOBAL_TRIGGER_EDGEDETECTION;
		case 'm':
			return VisualConstants.GLOBAL_TRIGGER_MIRROR;
		case 'b':
			return VisualConstants.GLOBAL_TRIGGER_TOGGLEBGFILL;
		case 's':
			return VisualConstants.GLOBAL_TRIGGER_CYCLECOLORSCHEME;
		case '1':
			return VisualConstants.GLOBAL_SCENE_RECTANGLES;
		case '2':
			return VisualConstants.GLOBAL_SCENE_PERCENTAGES;
		case '3':
			return VisualConstants.GLOBAL_SCENE_SPINCYCLE;
		case '4':
			return VisualConstants.GLOBAL_SCENE_FLYINGOBJECTS;
		//Escape key
		case 27:
			return -1;
		}
		
		System.err.println("Unidentified key press in ComputerKeyboard class, convertKeyPress function, key: " + key);
		return -1;
	}
}
