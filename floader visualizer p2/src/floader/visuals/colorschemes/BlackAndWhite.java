package floader.visuals.colorschemes;

import java.awt.Color;

public class BlackAndWhite extends ColorScheme {

	public BlackAndWhite()
	{
		colors = new Color[2];
		colors[0] = new Color(0,0,0,0); 
		colors[1] = new Color(131,131,131,255);

		
		bgColor = new Color(0,0,0);
	}
}
