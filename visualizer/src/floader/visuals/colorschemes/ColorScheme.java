package floader.visuals.colorschemes;

import java.awt.Color;

public abstract class ColorScheme {
	protected Color[] colors;
	protected Color bgColor;
	
	public Color getColor(int color)
	{
		return colors[color];
	}
	
	public int getLength()
	{
		return colors.length;
	}
	
	public Color getBgColor()
	{
		return bgColor;
	}
}
