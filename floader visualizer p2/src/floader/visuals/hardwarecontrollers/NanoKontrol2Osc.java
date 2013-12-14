package floader.visuals.hardwarecontrollers;

import floader.visuals.VisualConstants;
import oscP5.*;

public class NanoKontrol2Osc  {
		private static final int MIDI_CHANNEL = 0;
		private static final int KNOB_1_CTRL_NUM = 4;
		private static final int KNOB_2_CTRL_NUM = 5;
		private static final int KNOB_3_CTRL_NUM = 6;
		private static final int KNOB_4_CTRL_NUM = 7;
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
		private static final int SLDR_7_CTRL_NUM = 2;
		private static final int SLDR_8_CTRL_NUM = 3;
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
		
		public static int convertInputToIndex(OscMessage msg)
		{			
			if(!msg.checkAddrPattern("/mtn/ctrl"))
				return -1;
			
			int index = -1;
			int ctrlNum = msg.get(0).intValue();
			switch (ctrlNum) {
				
			case SLDR_1_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_CLIPX;
				break;
			case SLDR_2_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_CLIPY;
				break;
			case SLDR_7_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_CAMDISTANCE;
				break;
			case SLDR_8_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_PERSPECTIVE;
				break;
			case KNOB_1_CTRL_NUM:
				index = VisualConstants.LOCAL_EFFECT_1;
				break;
			case KNOB_2_CTRL_NUM:
				index = VisualConstants.LOCAL_EFFECT_2;
				break;
			case KNOB_3_CTRL_NUM:
				index = VisualConstants.LOCAL_EFFECT_3;
				break;
			case KNOB_4_CTRL_NUM:
				index = VisualConstants.LOCAL_EFFECT_4;
				break;
			default:
				System.err.println("Error: unidentified ctrl num in NanoKontrol2Osc conversion: " + ctrlNum);
				break;
			}
			return index;
		}
}
