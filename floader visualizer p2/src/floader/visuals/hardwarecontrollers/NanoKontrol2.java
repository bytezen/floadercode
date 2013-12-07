package floader.visuals.hardwarecontrollers;

import floader.visuals.VisualConstants;

public class NanoKontrol2 extends HardwareController {
		private static final int MIDI_CHANNEL = 0;
		private static final int KNOB_1_CTRL_NUM = 16;
		private static final int KNOB_2_CTRL_NUM = 17;
		private static final int KNOB_3_CTRL_NUM = 18;
		private static final int KNOB_4_CTRL_NUM = 19;
		private static final int KNOB_5_CTRL_NUM = 20;
		private static final int KNOB_6_CTRL_NUM = 21;
		private static final int KNOB_7_CTRL_NUM = 22;
		private static final int KNOB_8_CTRL_NUM = 23;
		private static final int KNOB_9_CTRL_NUM = 24;
		private static final int SLDR_1_CTRL_NUM = 0;
		private static final int SLDR_2_CTRL_NUM = 1;
		private static final int SLDR_3_CTRL_NUM = 2;
		private static final int SLDR_4_CTRL_NUM = 3;
		private static final int SLDR_5_CTRL_NUM = 4;
		private static final int SLDR_6_CTRL_NUM = 5;
		private static final int SLDR_7_CTRL_NUM = 6;
		private static final int SLDR_8_CTRL_NUM = 7;
		private static final int NOTE_1 = 43;
		private static final int NOTE_2 = 44;
		private static final int NOTE_3 = 42;
		private static final int NOTE_4 = 41;
		private static final int NOTE_5 = 45;
		private static final int NOTE_6 = 35;
		private static final int NOTE_7 = 26;
		private static final int NOTE_8 = 36;
		private static final int NOTE_9 = 27;
		private static final int NOTE_10 = 37;
		private static final int NOTE_11 = 28;
		private static final int NOTE_12 = 38;
		private static final int NOTE_13 = 29;
		private static final int NOTE_14 = 39;
		private static final int NOTE_15 = 30;
		private static final int NOTE_16 = 40;
		private static final int NOTE_17 = 31;
		private static final int NOTE_18 = 41;
		
		public static int getInputType(int chan, int num)
		{
			if(chan == MIDI_CHANNEL)
			{
				if(num >=SLDR_1_CTRL_NUM && num <=SLDR_8_CTRL_NUM)
				{
					return HardwareController.GLOBAL;
				}
				else if(num >=KNOB_1_CTRL_NUM && num <=KNOB_9_CTRL_NUM)
					return HardwareController.LOCAL;
				else
				{
					System.err.println("Unrecognized control number in nanKontrol2 class.  Ctrl: " + num);
					return -1;
				}
			}
			else
			{
				System.err.println("Unrecognized channel number in nanKontrol2 class.  Chan: " + chan);
				return -1;
			}
		}
		
		public static int convertInputToIndex(int chan, int num)
		{
			int  index = -1;
			switch (num) {
			//Local vars
			case KNOB_1_CTRL_NUM:
				index = 0;
				break;
			case KNOB_2_CTRL_NUM:
				index = 1;
				break;
			case KNOB_3_CTRL_NUM:
				index = 2;
				break;
			case KNOB_4_CTRL_NUM:
				index = 3;
				break;
			case KNOB_5_CTRL_NUM:
				index = 4;
				break;
			case KNOB_6_CTRL_NUM:
				index = 5;
				break;
				
			//Global vars
			case SLDR_1_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_BLUR;
				break;
			case SLDR_2_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_CAMDISTANCE;
				break;
			case SLDR_3_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_PERSPECTIVE;
				break;
			case SLDR_4_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_SCALE;
				break;
			case SLDR_5_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_ROTATEX;
				break;
			case SLDR_6_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_ROTATEY;
				break;
			case SLDR_7_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_ROTATEZ;
				break;
			case SLDR_8_CTRL_NUM:
				index = 7;
				break;
			default:
				System.err.println("Error: unidentified ctrl num in NanoKontrol2 conversion: " + num);
				break;
			}
			return index;
		}
}
