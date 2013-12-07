package floader.visuals.hardwarecontrollers;

import floader.visuals.VisualConstants;

public class MonomeMidi extends HardwareController {
		
		//Controll sliders
		private static final int SLIDER_MIDI_CHANNEL = 0;
		private static final int PAGE_1_SLDR_1 = 40;
		private static final int PAGE_1_SLDR_2 = 41;
		private static final int PAGE_1_SLDR_3 = 42;
		private static final int PAGE_1_SLDR_4 = 43;
		private static final int PAGE_1_SLDR_5 = 44;
		private static final int PAGE_1_SLDR_6 = 45;
		private static final int PAGE_1_SLDR_7 = 46;
		private static final int PAGE_2_SLDR_1 = 47;
		private static final int PAGE_2_SLDR_2 = 48;
		private static final int PAGE_2_SLDR_3 = 49;
		private static final int PAGE_2_SLDR_4 = 50;
		private static final int PAGE_2_SLDR_5 = 51;
		private static final int PAGE_2_SLDR_6 = 52;
		private static final int PAGE_2_SLDR_7 = 53;
		private static final int PAGE_3_SLDR_1 = 54;
		private static final int PAGE_3_SLDR_2 = 55;
		private static final int PAGE_3_SLDR_3 = 56;
		private static final int PAGE_3_SLDR_4 = 57;
		private static final int PAGE_3_SLDR_5 = 58;
		private static final int PAGE_3_SLDR_6 = 59;
		private static final int PAGE_3_SLDR_7 = 60;
		//Melodizer notes
		private static final int NOTE_GLOBAL_MIDI_CHANNEL = 0;
		private static final int NOTE_LOCAL_MIDI_CHANNEL = 1;
		private static final int PAGE_1_ROW_1_NOTE_1 = 84;
		private static final int PAGE_1_ROW_1_NOTE_2 = 85;
		private static final int PAGE_1_ROW_1_NOTE_3 = 86;
		private static final int PAGE_1_ROW_1_NOTE_4 = 87;
		private static final int PAGE_1_ROW_1_NOTE_5 = 88;
		private static final int PAGE_1_ROW_1_NOTE_6 = 89;
		private static final int PAGE_1_ROW_1_NOTE_7 = 90;
		private static final int PAGE_2_ROW_1_NOTE_1 = 84;
		private static final int PAGE_2_ROW_1_NOTE_2 = 85;
		private static final int PAGE_2_ROW_1_NOTE_3 = 86;
		private static final int PAGE_2_ROW_1_NOTE_4 = 87;
		private static final int PAGE_2_ROW_1_NOTE_5 = 88;
		private static final int PAGE_2_ROW_1_NOTE_6 = 89;
		private static final int PAGE_2_ROW_1_NOTE_7 = 90;
		
		
		public static int getControllerInputType(int chan, int num)
		{
			if(chan == SLIDER_MIDI_CHANNEL)
			{
				if(num >=PAGE_2_SLDR_1 && num <=PAGE_2_SLDR_7)
				{
					return HardwareController.GLOBAL;
				}
				else if(num >=PAGE_3_SLDR_1 && num <=PAGE_3_SLDR_7)
					return HardwareController.LOCAL;
				else
				{
					System.err.println("Unrecognized control number in MonomeMidi class, getControllerInputType function.  Ctrl: " + num);
					return -1;
				}
			}
			else
			{
				System.err.println("Unrecognized channel number in MonomeMidi class, getControllerInputType function.  Chan: " + chan);
				return -1;
			}
		}
		
		public static int getNoteInputType(int chan)
		{
			if(chan == NOTE_GLOBAL_MIDI_CHANNEL)
				return HardwareController.GLOBAL;
			else if(chan == NOTE_LOCAL_MIDI_CHANNEL)
				return HardwareController.LOCAL;
			else {
				System.err.println("Unrecognized channel number in MonomeMidi class, getNoteInputeType function.  Chan: " + chan);
				return -1;
			}
		}
		
		public static int convertNote(int chan, int note)
		{
			int  index = -1;
			if(chan == NOTE_GLOBAL_MIDI_CHANNEL)
			{
				switch(note)
				{
				case PAGE_1_ROW_1_NOTE_1:
					return index = VisualConstants.GLOBAL_TRIGGER_CUBE;
				case PAGE_1_ROW_1_NOTE_2:
					return index = VisualConstants.GLOBAL_TRIGGER_CAPTUREBG;
				case PAGE_1_ROW_1_NOTE_3:
					return index = VisualConstants.GLOBAL_TRIGGER_EDGEDETECTION;
				case PAGE_1_ROW_1_NOTE_4:
					return index = VisualConstants.GLOBAL_TRIGGER_MIRROR;
				case PAGE_1_ROW_1_NOTE_5:
					return index = VisualConstants.GLOBAL_TRIGGER_TOGGLEBGFILL;
				case PAGE_1_ROW_1_NOTE_6:
					return index = VisualConstants.GLOBAL_TRIGGER_RESET;
				case PAGE_1_ROW_1_NOTE_7:
					return index = VisualConstants.GLOBAL_TRIGGER_CYCLECOLORSCHEME;
				}
			}
			System.err.println("Unrecognized input to MonomeMidi class, convertNote. Chan: " + chan + ", Note: " + note);
			return index;
		}
		
		public static int convertController(int chan, int num)
		{
			int  index = -1;
			switch (num) {
			//Global effects
			case PAGE_2_SLDR_1:
				index = VisualConstants.GLOBAL_EFFECT_BLUR;
				break;
			case PAGE_2_SLDR_2:
				index = VisualConstants.GLOBAL_EFFECT_CAMDISTANCE;
				break;
			case PAGE_2_SLDR_3:
				index = VisualConstants.GLOBAL_EFFECT_PERSPECTIVE;
				break;
			case PAGE_2_SLDR_4:
				index = VisualConstants.GLOBAL_EFFECT_SCALE;
				break;
			case PAGE_2_SLDR_5:
				index = VisualConstants.GLOBAL_EFFECT_ROTATEX;
				break;
			case PAGE_2_SLDR_6:
				index = VisualConstants.GLOBAL_EFFECT_ROTATEY;
				break;
			case PAGE_2_SLDR_7:
				index = VisualConstants.GLOBAL_EFFECT_ROTATEZ;
				break;
			//Local effects
			case PAGE_3_SLDR_1:
				index = 0;
				break;
			case PAGE_3_SLDR_2:
				index = 1;
				break;
			case PAGE_3_SLDR_3:
				index = 2;
				break;
			case PAGE_3_SLDR_4:
				index = 3;
				break;
			case PAGE_3_SLDR_5:
				index = 4;
				break;
			case PAGE_3_SLDR_6:
				index = 5;
				break;
			case PAGE_3_SLDR_7:
				index = 6;
				break;
			default:
				System.err.println("Error: unidentified ctrl num in MonomeMidi conversion: " + num);
				break;
			}
			return index;
		}
}
