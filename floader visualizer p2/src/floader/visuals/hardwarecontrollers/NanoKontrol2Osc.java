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
		private static final int KNOB_7_CTRL_NUM = 12;
		private static final int KNOB_8_CTRL_NUM = 13;
		private static final int SLDR_1_CTRL_NUM = 0;
		private static final int SLDR_2_CTRL_NUM = 1;
		private static final int SLDR_3_CTRL_NUM = 10;
		private static final int SLDR_4_CTRL_NUM = 11;
		private static final int SLDR_5_CTRL_NUM = 4;
		private static final int SLDR_6_CTRL_NUM = 5;
		private static final int SLDR_7_CTRL_NUM = 2;
		private static final int SLDR_8_CTRL_NUM = 3;
		private static final int BTN_S_7_NUM = 8;
		private static final int BTN_S_8_NUM = 9;
		
		
		public static int convertInputToIndex(OscMessage msg)
		{			
			if(!msg.checkAddrPattern("/mtn/ctrl"))
				return -1;
			
			int index = -1;
			int ctrlNum = msg.get(VisualConstants.OSC_CTRL_INDEX).intValue();
			switch (ctrlNum) {
				
			case SLDR_1_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_CLIPX;
				break;
			case SLDR_2_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_CLIPY;
				break;
			case SLDR_3_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_CAMDISTANCE;
				break;
			case SLDR_4_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_PERSPECTIVE;
				break;
			case SLDR_7_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_LIGHTFALLOFF;
				break;
			case SLDR_8_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_LIGHTFALLOFF;
				break;
			case KNOB_1_CTRL_NUM:
				index = VisualConstants.LOCAL_EFFECT_1;
				break;
			case KNOB_2_CTRL_NUM:
				index = VisualConstants.LOCAL_EFFECT_2;
				break;
			case KNOB_3_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_SCALE;
				break;
			case KNOB_4_CTRL_NUM:
				index = VisualConstants.LOCAL_EFFECT_4;
				break;
			case KNOB_7_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_LIGHTDIM;
				break;
			case KNOB_8_CTRL_NUM:
				index = VisualConstants.GLOBAL_EFFECT_LIGHTDIM;
				break;
			case BTN_S_7_NUM:
				index = VisualConstants.GLOBAL_EFFECT_BLUR;
				break;
			case BTN_S_8_NUM:
				index = VisualConstants.GLOBAL_EFFECT_BLUR;
				break;
			default:
				System.err.println("Error: unidentified ctrl num in NanoKontrol2Osc conversion: " + ctrlNum);
				break;
			}
			return index;
		}
}
