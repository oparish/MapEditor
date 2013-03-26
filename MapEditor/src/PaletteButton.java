import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.*;
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class PaletteButton extends JButton implements ActionListener
{
	private int ID;
	private BufferedImage associatedTile;
	
	PaletteButton(BufferedImage img, int number)
	{
		Dimension size = new Dimension(40, 40);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		
		addActionListener(this);
		
		ID = number;
		associatedTile = img;
	}
	
	public void paintComponent(Graphics g)
	{
		g.drawImage(associatedTile, 0, 0, null);
	}
	
	public int getID()
	{
		return ID;
	}
	
	public void actionPerformed(ActionEvent event)
	{
		
	}
	
}
