package floader.visuals.colorschemes;

import java.awt.Color;

public class Terminal extends ColorScheme {

	public Terminal()
	{
		colors = new Color[4];
		colors[0] = new Color(0, 166, 124); 
		colors[1] = new Color(14, 81, 167);
		colors[2] = new Color(65, 219, 0);
		colors[3] = new Color(0, 108, 81);
		
		bgColor = new Color(0,0,0);
	}
}
