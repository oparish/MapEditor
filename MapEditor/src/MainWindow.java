import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.text.NumberFormat;
import java.awt.image.*;

import javax.imageio.ImageIO;
import javax.swing.*;


@SuppressWarnings("serial")
public class MainWindow extends JFrame implements KeyListener, ActionListener
{
	private static final int OPEN = 0;
	private static final int SAVE = 1;
	
	private static final int INITIALWIDTH= 50;
	private static final int INITIALHEIGHT = 10;
	
	private static BufferedImage baseTile;
	private static BufferedImage redFlag;
	private static BufferedImage blueFlag;
	private static BufferedImage greenFlag;
	private static BufferedImage[] terrain1;
	private static BufferedImage[] terrain2;
	private static BufferedImage[] largeTerrain;
	private static BufferedImage[] fences;
	private static BufferedImage[] roads;
	
	private static int paletteTile;
	private ImagePanel mapPanel;
	private JScrollPane scrollPane;
	
	private static File saveFile = null;
	
	private int defaultWidth;
	private int defaultHeight;
	
	private ChangeField widthField;
	private ChangeField heightField;
	
	MainWindow()
	{
		setUndecorated(true);
		loadAllImages();
		defaultWidth=INITIALWIDTH;
		defaultHeight=INITIALHEIGHT;
		setupScreen(INITIALWIDTH, INITIALHEIGHT);
		
	    paletteTile = 1;
		setVisible(true);
	}
	
	private void loadAllImages()
	{
	    baseTile = LoadImage("Green.PNG");
	    redFlag = LoadImage("GRedFlag.PNG");
	    blueFlag = LoadImage("GBlueFlag.PNG");
	    greenFlag = LoadImage("GGreenFlag.PNG");
	    
	    terrain1 = arrayfill("Forest",16);
	    terrain2 = arrayfill("Swamp",16);
	    largeTerrain = arrayfill("LargeSwamp",3);
	    fences = arrayfill("Fences",16);
	    roads = arrayfill("Road",16);
	}
	
	private void setupScreen(int width, int height)
	{
		setLayout(new GridBagLayout());
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize.width, screenSize.height);
		
		Dimension sizeForScrollPane = new Dimension(screenSize.width-160, screenSize.height-60);
		
		mapPanel = new ImagePanel(width, height, getDefaultData(width, height));
		setupScrollPane(sizeForScrollPane, mapPanel);
		setupPalettePanel(screenSize);
		
	    GridBagConstraints controlConstraints = setupControlConstraints();
		setupNewButton(controlConstraints);
		setupSaveButton(controlConstraints);
		setupSaveAsButton(controlConstraints);
		setupLoadButton(controlConstraints);
		setupChangeTilesetButton(controlConstraints);
		setupQuitButton(controlConstraints);
		
		setupWidthField(controlConstraints);
		setupHeightField(controlConstraints);
	}
	
	private int[][] getDefaultData(int width, int height)
	{
		int[][] data = new int[width][height];
		
		for (int i=0;i<width;i++)
		{
			for (int j=0;j<height;j++)
			{
				data[i][j]=1;
			}
		}
		
		return data;
	}
	
	public static BufferedImage LoadImage(String name)
	{
		BufferedImage img = null;
		try
		{
			img = ImageIO.read(new File("Images/" + name));
		}
		catch(IOException e)
		{
	    	JOptionPane.showMessageDialog(new JFrame(), name + " cannot be loaded. Exception: " + e, "Error",
	    	        JOptionPane.ERROR_MESSAGE);
		}	
		return img;
	}
	
	private BufferedImage[] arrayfill(String name, int number)
	{
		BufferedImage[] imageArray = new BufferedImage[number];
		String fullName;
			for(int i=0;i<number;i++)
			{
				fullName = "G" + name + (i+1) + ".PNG";
				imageArray[i ]= LoadImage(fullName);
			}
		
		return imageArray;
	}
	
	private GridBagConstraints setupControlConstraints()
	{
		GridBagConstraints controlConstraints = new GridBagConstraints();
		controlConstraints.gridy = 0;
		controlConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		controlConstraints.weightx = 0.5;
		controlConstraints.weighty = 0.5;
		
		return controlConstraints;
	}
	
	private void setupSaveButton(GridBagConstraints buttonConstraints)
	{
		ListenerButton saveMapButton = new ListenerButton("Save Map"){
			public void actionPerformed(ActionEvent event)
			{
				saveMap();
			}
		};
		buttonConstraints.gridx = 2;
		add(saveMapButton, buttonConstraints);	
	}
	
	private void setupSaveAsButton(GridBagConstraints buttonConstraints)
	{
		ListenerButton saveMapAsButton = new ListenerButton("Save Map As"){
			public void actionPerformed(ActionEvent event)
			{
				saveMapAs();
			}
		};
		buttonConstraints.gridx = 3;
		add(saveMapAsButton, buttonConstraints);	
	}
	
	private void setupQuitButton(GridBagConstraints buttonConstraints)
	{
		ListenerButton quitButton = new ListenerButton("Quit")
		{
			public void actionPerformed(ActionEvent event)
			{
				quit();
			}
		};
		buttonConstraints.gridx = 5;
		add(quitButton, buttonConstraints);		
	}
	
	private void setupChangeTilesetButton(GridBagConstraints buttonConstraints)
	{
		ListenerButton changeTilesetButton = new ListenerButton("Change Tileset")
		{
			public void actionPerformed(ActionEvent event)
			{
				changeTileset();
			}
		};
		buttonConstraints.gridx = 4;
		add(changeTilesetButton, buttonConstraints);	
	}
	
	private void setupPalettePanel(Dimension screenSize)
	{
		Palette PalettePanel = new Palette();
		
		GridBagConstraints paletteConstraints = new GridBagConstraints();
		paletteConstraints.gridx = 6;
		paletteConstraints.gridy = 1;
		paletteConstraints.gridwidth = 1;
		paletteConstraints.insets = new Insets (10, 2, 10, 2);
		paletteConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		paletteConstraints.weightx = 1;
		paletteConstraints.weighty = 1;
		
		add(PalettePanel, paletteConstraints);
	}
	
	private void setupScrollPane(Dimension sizeForScrollPane, ImagePanel mapPanel)
	{
		scrollPane = new JScrollPane(mapPanel);
	    scrollPane.setPreferredSize(sizeForScrollPane);
	    scrollPane.setMinimumSize(sizeForScrollPane);
	    scrollPane.setMaximumSize(sizeForScrollPane);
	    scrollPane.setSize(sizeForScrollPane);
	    scrollPane.setViewportBorder(BorderFactory.createLineBorder(Color.black));
	    
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.gridx = 0;
		scrollConstraints.gridy = 1;
		scrollConstraints.gridwidth = 6;
		scrollConstraints.insets = new Insets (10, 2, 10, 2);
		scrollConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		scrollConstraints.weightx = 1;
		scrollConstraints.weighty = 1;
	   
	    add(scrollPane, scrollConstraints);
	}
	
	private void setupLoadButton(GridBagConstraints buttonConstraints)
	{
		ListenerButton newMapButton = new ListenerButton("Load Map")
		{
			public void actionPerformed(ActionEvent event)
			{
				loadMap();
			}
		};
		buttonConstraints.gridx = 1;
		add(newMapButton, buttonConstraints);
	}
	
	private void setupNewButton(GridBagConstraints buttonConstraints)
	{
		ListenerButton newMapButton = new ListenerButton("New Map")
		{	
			public void actionPerformed(ActionEvent event)
			{
				newMap();
			}
		};
		buttonConstraints.gridx = 0;
		add(newMapButton, buttonConstraints);
	}
	
	private void newMap()
	{
		NewMapDialog newMapDialog = new NewMapDialog(this,defaultWidth,defaultHeight);
		newMapDialog.setLocationRelativeTo(this);
		newMapDialog.setVisible(true);
		
		int width = newMapDialog.Width();
		int height = newMapDialog.Height();
		
		updateDefaultWidth(width);
		updateDefaultHeight(height);
		
		refreshScrollPane(width,height,getDefaultData(width, height));
	}
	
	private void setupWidthField(GridBagConstraints controlConstraints)
	{
		NumberFormat widthFormat = NumberFormat.getIntegerInstance();
		widthField = new ChangeField(widthFormat)
		{
			public void actionPerformed(ActionEvent event)
			{
				int newWidth = Integer.parseInt(getText());
				if (newWidth>=0 && newWidth<256)
				{
					setValue(newWidth);
					int[][] newData  = mapPanel.getMapWithWidthChange(newWidth);
					defaultWidth = newWidth;
					refreshScrollPane(newWidth, mapPanel.MapHeight(), newData);
				}
				else
					setValue(defaultWidth);
			}
		
			public void focusLost(FocusEvent event)
			{
				if (isEditValid())
					actionPerformed(null);
				else
					setValue(defaultWidth);
			}
		};
		widthField.setValue(defaultWidth);
		controlConstraints.gridx=6;
		add(widthField, controlConstraints);
	}
	
	private void setupHeightField(GridBagConstraints controlConstraints)
	{
		NumberFormat heightFormat = NumberFormat.getIntegerInstance();
		heightField = new ChangeField(heightFormat)		
		{
			public void actionPerformed(ActionEvent event)
			{
				int newHeight = Integer.parseInt(getText());
				if (newHeight>=0 && newHeight<256)
				{
					setValue(newHeight);
					int[][] newData  = mapPanel.getMapWithHeightChange(newHeight);
					defaultHeight = newHeight;
					refreshScrollPane(mapPanel.MapWidth(), newHeight, newData);
				}
				else
					setValue(defaultHeight);
			}
			
			public void focusLost(FocusEvent event)
			{
				if (isEditValid())
					actionPerformed(null);
				else
					setValue(defaultHeight);
			}
		};
		heightField.setValue(defaultHeight);
		controlConstraints.gridx=7;
		add(heightField, controlConstraints);
	}
	
	private void refreshScrollPane(int width, int height, int[][] mapData)
	{
		scrollPane.remove(mapPanel);
		mapPanel = new ImagePanel(width,height,mapData);
		scrollPane.setViewportView(mapPanel);
		scrollPane.revalidate();
		scrollPane.repaint();
	}
	
	private void updateDefaultWidth(int width)
	{
		defaultWidth = width;
		widthField.setValue(width);
	}
	
	private void updateDefaultHeight(int height)
	{
		defaultHeight = height;
		heightField.setValue(height);
	}
	
	private void saveMap()
	{
		if (saveFile==null)
			saveMapAs();
		else
		{
			try
			{
				mapPanel.writeMap(saveFile);
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
		}
	}
	
	private void saveMapAs()
	{
		File myFile = chooseMap(SAVE);
		if (myFile!=null)
		{		
			saveFile=myFile;
			
			try
			{
				mapPanel.writeMap(myFile);
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
		}
	}
	
	private File chooseMap(int chooserMode)
	{
		File initialDirectory = new File("C:\\HistoryGame\\Maps\\");
		JFileChooser myFileChooser = new JFileChooser(initialDirectory);
		MapFileFilter myFileFilter = new MapFileFilter();
		myFileChooser.addChoosableFileFilter(myFileFilter);
		
		int result;
		
		if (chooserMode==SAVE)
			result = myFileChooser.showSaveDialog(this);
		else
			result = myFileChooser.showOpenDialog(this);
		
		if (result==JFileChooser.APPROVE_OPTION)
			return myFileChooser.getSelectedFile();
		else
			return null;
	}
	
	private void loadMap()
	{
		File myFile = chooseMap(OPEN);
		
		if (myFile!=null)
		{		
			saveFile=myFile;
			
			try
			{
				readFile(myFile);
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
		}
	}
	
	private void readFile(File myFile) throws IOException
	{
		FileReader myFileReader = null;
		myFileReader = new FileReader(myFile);
		
		int[] parameters = loadParametersFromFile(myFileReader);
		int[][] mapData = loadDataFromFile(myFileReader, parameters);
		
		refreshScrollPane(parameters[0], parameters[1], mapData);
	}
	
	private int[] loadParametersFromFile(FileReader myFileReader) throws IOException
	{
		char[] limitChars = new char[2];
		myFileReader.read(limitChars,0,2);
		
		int[] parameters = new int[2];
		
		parameters[0] = (int) limitChars[0];
		parameters[1] = (int) limitChars[1];
		
		updateDefaultWidth(parameters[0]);
		updateDefaultHeight(parameters[1]);
		
		return parameters;
	}
	
	private int[][] loadDataFromFile(FileReader myFileReader, int[] parameters) throws IOException
	{
		int charNumber = (parameters[0] * parameters[1]);
		char[] dataChars = new char[charNumber];
		myFileReader.read(dataChars,0,charNumber);
		
		int[][] mapData = new int[parameters[0]][parameters[1]];
		
		for(int i=0;i<parameters[0];i++)
		{
			for(int j=0;j<parameters[1];j++)
			{
				mapData[i][j] = (int)dataChars[(parameters[1]*i)+j];
			}
		}
		
		return mapData;
	}
	
	private void quit()
	{
		System.exit(0);
	}
	
	private void changeTileset()
	{
		
	}
	
	public static int getPaletteTile()
	{
		return paletteTile;
	}
	
	public static void setPaletteTile(int receivedPaletteTile)
	{
		paletteTile=receivedPaletteTile;
	}
	
	public static BufferedImage getBaseTile()
	{
		return baseTile;
	}
	
	public static BufferedImage getRedFlag()
	{
		return redFlag;
	}
	
	public static BufferedImage getGreenFlag()
	{
		return greenFlag;
	}
	
	public static BufferedImage getBlueFlag()
	{
		return blueFlag;
	}
	
	public static BufferedImage getTerrain1(int number)
	{
		return terrain1[number];
	}
	
	public static BufferedImage getTerrain2(int number)
	{
		return terrain2[number];
	}
	
	public static BufferedImage getLargeTerrain(int number)
	{
		return largeTerrain[number];
	}
	
	public static BufferedImage getFences(int number)
	{
		return fences[number];
	}

	public static BufferedImage getRoads(int number)
	{
		return roads[number];
	}
	
    public void keyTyped(KeyEvent e) 
    {
    }
    
    public void keyPressed(KeyEvent e)
    {
    }
    
    public void keyReleased(KeyEvent e)
    {
    }

	public void actionPerformed(ActionEvent event)
	{
	}
	
	public static void main(String args[]) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
               new MainWindow();
            }
        });
	}
}
