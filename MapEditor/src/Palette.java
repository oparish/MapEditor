import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class Palette extends JPanel
{
	private BufferedImage[] paletteTiles;
	public PaletteButton[] buttons;
	
	Palette()
	{
	    Dimension size = new Dimension(120,120);
	    setPreferredSize(size);
	    setMinimumSize(size);
	    setMaximumSize(size);
	    setSize(size);
	    setLayout(null);
	    
		buttons = new PaletteButton[9];
	    paletteTiles = new BufferedImage[9];
	    paletteTiles[0] = MainWindow.getBaseTile();
	    paletteTiles[1] = MainWindow.getRedFlag();
	    paletteTiles[2] = MainWindow.getBlueFlag();
	    paletteTiles[3] = MainWindow.getGreenFlag();
	    paletteTiles[4] = MainWindow.getTerrain1(0);
	    paletteTiles[5] = MainWindow.getTerrain2(0);
	    paletteTiles[6] = MainWindow.getFences(1);
	    paletteTiles[7] = MainWindow.getRoads(0);
	    paletteTiles[8] = MainWindow.getLargeTerrain(1);

		for (int i=0; i<9; i++)
		{
			buttons[i] = new PaletteButton(paletteTiles[i], (i+1)){
				public void actionPerformed(ActionEvent event)
				{
					MainWindow.setPaletteTile(this.getID());
				}
			};
			add(buttons[i]);
		}
		
		buttons[0].setLocation(0,0);
		buttons[1].setLocation(40,0);
		buttons[2].setLocation(80,0);
		buttons[3].setLocation(0,40);
		buttons[4].setLocation(40,40);
		buttons[5].setLocation(80,40);
		buttons[6].setLocation(0,80);
		buttons[7].setLocation(40,80);
		buttons[8].setLocation(80,80);
	}
	
	public void paintComponent(Graphics g)
	{

	}
}
