import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.image.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;

@SuppressWarnings("serial")
class ImagePanel extends JPanel implements Scrollable
{
	private static final int CENTRETERRAIN=9;
	private static final int LEFTTERRAIN=10;
	private static final int RIGHTTERRAIN=11;
	
	private static final int LARGETERRAINVALUE=9;
	private static final int REDFLAGVALUE=2;
	private static final int BLUEFLAGVALUE=3;
	private static final int GREENFLAGVALUE=4;
	
	private int mapWidth;
	private int mapHeight;
	
	private CanvasButton[][] buttons;
	
	private int[][][][] controller;
	private int[][] mapValues;

  public ImagePanel(int width, int height, int[][] data)
  {
	mapWidth = width;
	mapHeight = height;
	  
	setLayout(new GridBagLayout());    
	setupSizing();
    setupTiles(data);
  }
  
  private void setupTiles(int[][] data)
  {
	   setupController();
	   buttons = new CanvasButton[mapWidth][mapHeight];
	   mapValues = data;
	   
	   loadTiles(mapValues);
	   finishTiles();
  }
  
  private void setupSizing()
  {
	    Dimension size = new Dimension(mapWidth * 40, mapHeight * 40);
	    setPreferredSize(size);
	    setMinimumSize(size);
	    setMaximumSize(size);
	    setSize(size);
  }
  
  private void setupController()
  {
	    controller = new int[2][2][2][2];
	    controller[0][0][0][0] = 0;
	    controller[0][0][0][1] = 1;
	    controller[0][0][1][0] = 2;
	    controller[0][0][1][1] = 5;
	    controller[0][1][0][0] = 3;
	    controller[0][1][0][1] = 6;
	    controller[0][1][1][0] = 8;
	    controller[0][1][1][1] = 14;
	    controller[1][0][0][0] = 4;
	    controller[1][0][0][1] = 7;
	    controller[1][0][1][0] = 9;
	    controller[1][0][1][1] = 13;
	    controller[1][1][0][0] = 10;
	    controller[1][1][0][1] = 12;
	    controller[1][1][1][0] = 11;
	    controller[1][1][1][1] = 15;
  }

	public void canvasPaint(int x, int y)
	{
		if (MainWindow.getPaletteTile() == LARGETERRAINVALUE && x!=0 && x != (mapWidth-1))
		{
			if (checkVillageRegion(mapValues[x-1][y]))
				removeVillage(x-1,y);
			
			if (checkVillageRegion(mapValues[x][y]))
				removeVillage(x,y);
			
			if (checkVillageRegion(mapValues[x+1][y]))
				removeVillage(x+1,y);
			
			if (mapValues[x][y]==LEFTTERRAIN)		
				paintLeftTerrain(x,y);
			else if (mapValues[x][y]==RIGHTTERRAIN)
				paintRightTerrain(x,y);
			else if (mapValues[x][y]!=LARGETERRAINVALUE)			
				paintAdjacentToLargeTerrain(x,y);
		}
		else if (MainWindow.getPaletteTile() == REDFLAGVALUE || 
				MainWindow.getPaletteTile() == BLUEFLAGVALUE ||
				MainWindow.getPaletteTile() == GREENFLAGVALUE)
		{
			if (x>1 && x<(mapWidth-1) && y>1 && y<(mapHeight-1))
			{
				for (int i=(x-4);i<(x+5);i++)
				{
					if (i>-1 && i<mapWidth)
					{
						for (int j=(y-4);j<(y+5);j++)
						{
							if (j>-1 && j<mapHeight)
							{
								if (checkVillageRegion(mapValues[i][j]))
									removeVillage(i,j);
								else if (mapValues[i][j]>8)
									removeLargeTerrain(i,j);
							}
						}
					}
				}
				placeVillage(x,y,MainWindow.getPaletteTile());
			}
		}
		else if (MainWindow.getPaletteTile()==LARGETERRAINVALUE )
		{
			
		}
		else if (mapValues[x][y]>8)
		{
			removeLargeTerrain(x,y);
			canvasPaint(x,y);
		}
		else if (checkVillageRegion(mapValues[x][y]))
		{
			removeVillage(x,y);
			canvasPaint(x,y);
		}
		else
		{
			paintTerrain(x,y);
		}
	}
	
	private boolean checkVillageRegion(int data)
	{
		if (data==7 || data==REDFLAGVALUE || data==BLUEFLAGVALUE || data==GREENFLAGVALUE)
			return true;
		else
			return false;
	}
	
	private void addButton(int x, int y)
	{
	    GridBagConstraints buttonConstraints = new GridBagConstraints();
	    buttonConstraints.anchor=GridBagConstraints.FIRST_LINE_START;
	    buttonConstraints.weightx=0;
	    buttonConstraints.weighty=0;
	    buttonConstraints.gridx = x;
	    buttonConstraints.gridy = y;
		add(buttons[x][y], buttonConstraints);
	}
	
	private void placeVillage(int x, int y, int flagType)
	{
		int fenceCounter = 0;
		
		for (int j=-3;j<4;j++)
		{
			for (int i=-3;i<4;i++)
			{
				if ((i==-3 || i==3 || j==-3 || j==3))
					adjustTilesAdjacentToVillage(x+i,y+j);
				else if ((i==-2 || i==2 || j==-2 || j==2))
				{
					placeFence(x+i,y+j,fenceCounter);
					fenceCounter++;
				}
				else if (i==0 && j==0)
					placeFlag(x, y, flagType);
				else
					placeOpenArea(x+i,y+j);
			}
		}
	}
	
	private void adjustTilesAdjacentToVillage(int a, int b)
	{
		if (a!=-1 && b!=-1 && a!=mapWidth && b!=mapHeight)
			buttons[a][b].ChangeImage(canvasCheck(a,b));
	}
	
	private void placeFence(int a, int b, int fenceCounter)
	{
		mapValues[a][b] = 7;
		buttons[a][b].ChangeImage(MainWindow.getFences(fenceCounter));
	}
	
	private void placeFlag(int x, int y, int flagType)
	{
		mapValues[x][y] = flagType;
		if (flagType==REDFLAGVALUE)
			buttons[x][y].ChangeImage(MainWindow.getRedFlag());
		else if (flagType==GREENFLAGVALUE)
			buttons[x][y].ChangeImage(MainWindow.getGreenFlag());
		else if (flagType==BLUEFLAGVALUE)
			buttons[x][y].ChangeImage(MainWindow.getBlueFlag());
	}
	
	private void placeOpenArea(int a, int b)
	{
		mapValues[a][b] = 7;
		buttons[a][b].ChangeImage(MainWindow.getBaseTile());
	}
	
	private void removeVillage(int x, int y)
	{
		System.out.println("Removing village at " + x + ", "+y);
		for (int j=-2;j<3;j++)
		{
			for (int i=-2;i<3;i++)
			{
				int a=i+x;
				int b=j+y;
				if (a>0 && b>0 && a<mapWidth & b<mapHeight)
				{
					if (mapValues[a][b]==REDFLAGVALUE ||
						mapValues[a][b]==GREENFLAGVALUE ||
						mapValues[a][b]==BLUEFLAGVALUE)
					{
						for (int k=-2;k<3;k++)
						{
							for (int l=-2;l<3;l++)
							{
									int c=k+a;
									int d=l+b;
									mapValues[c][d]=1;
									buttons[c][d].ChangeImage(MainWindow.getBaseTile());
							}
						}
						i=2;
						j=2;
					}
				}
			}
		}
	}
	
	private void paintLeftTerrain(int x, int y)
	{
		int lowx = x-2;
		
		if (mapValues[x-1][y] == RIGHTTERRAIN)
		{
			terrainChange(x-2,y,1);
			terrainChange(x-3,y,1);
			
			lowx = x-4;
		}
			terrainChange(x+2,y,1);
			
			terrainChange(x,y,CENTRETERRAIN);
			terrainChange(x+1,y,RIGHTTERRAIN);
			terrainChange(x-1,y,LEFTTERRAIN);
			
			terrainUpdate(lowx, x+3, y);
	}
	
	private void paintRightTerrain(int x, int y)
	{
		int highx = x+2;
		
		if (mapValues[x+1][y] == LEFTTERRAIN)
		{
			terrainChange(x+2,y,1);
			terrainChange(x+3,y,1);
			
			highx = x+4;
		}
			terrainChange(x-2,y,1);	
			
			terrainChange(x,y,CENTRETERRAIN);
			terrainChange(x+1,y,RIGHTTERRAIN);
			terrainChange(x-1,y,LEFTTERRAIN);
			
			terrainUpdate(x-3, highx, y);
	}
	
	private void paintAdjacentToLargeTerrain(int x,int y)
	{
		int lowx = x-2;
		int highx = x+2;
		
		if (mapValues[x-1][y] == RIGHTTERRAIN)
		{
			terrainChange(x-2,y,1);
			terrainChange(x-3,y,1);	
			lowx=x-4;
		}
		
		if (mapValues[x+1][y] == LEFTTERRAIN)
		{
			terrainChange(x+2,y,1);
			terrainChange(x+3,y,1);
			highx=x+4;
		}
		
		terrainChange(x,y,CENTRETERRAIN);
		terrainChange(x+1,y,RIGHTTERRAIN);
		terrainChange(x-1,y,LEFTTERRAIN);
		
		terrainUpdate(lowx, highx, y);
	}
	
	private void removeLargeTerrain(int x, int y)
	{
		int centrepoint=x;
		
		if (mapValues[x][y]==10)
			centrepoint=x+1;
		else if (mapValues[x][y]==11)
			centrepoint=x-1;
		
		mapValues[centrepoint][y]=1;
		mapValues[centrepoint-1][y]=1;
		mapValues[centrepoint+1][y]=1;
		
		buttons[centrepoint][y].ChangeImage(MainWindow.getBaseTile());
		buttons[centrepoint-1][y].ChangeImage(MainWindow.getBaseTile());
		buttons[centrepoint+1][y].ChangeImage(MainWindow.getBaseTile());
	}
	
	private void paintTerrain(int x, int y)
	{
		mapValues[x][y] = MainWindow.getPaletteTile();
		int a,b;
		for (int i=-1;i<2;i++)
		{
			for (int j=-1;j<2;j++)
			{
				a = x+i;
				b = y+j;
				if (a!=-1 && b!=-1 && a!=mapWidth && b!=mapHeight)
					buttons[a][b].ChangeImage(canvasCheck(a,b));
			}
		}
	}
	
	private void terrainChange(int x, int y, int tilevalue)
	{
		mapValues[x][y] = tilevalue;
		
		BufferedImage newImage;
		if (tilevalue==CENTRETERRAIN)
			newImage=MainWindow.getLargeTerrain(1);
		else if (tilevalue==LEFTTERRAIN)
			newImage=MainWindow.getLargeTerrain(0);
		else if (tilevalue==RIGHTTERRAIN)
			newImage=MainWindow.getLargeTerrain(2);
		else
			newImage=MainWindow.getBaseTile();
			
		buttons[x][y].ChangeImage(newImage);
		
		if (y>0)
			buttons[x][y-1].ChangeImage(canvasCheck(x,y-1));
		
		if (y<mapHeight-1)
			buttons[x][y+1].ChangeImage(canvasCheck(x,y+1));
		
	}
	
	private void terrainUpdate(int lowx, int highx, int y)
	{
		if (lowx>=0)
			buttons[lowx][y].ChangeImage(canvasCheck(lowx,y));
		
		if ((highx)<(mapWidth-1))
			buttons[highx][y].ChangeImage(canvasCheck(highx,y));
	}
	
	private BufferedImage canvasCheck(int x,int y)
	{
		if (mapValues[x][y]==7)
			return null;
		
		int a = 0;
		int b = 0;
		int c = 0;
		int d = 0;
		BufferedImage transferTile = MainWindow.getBaseTile();
		
		if (x!=0)
		{
			if (mapValues[x-1][y]==mapValues[x][y])
				a = 1;
		}
		if (y!=0)
		{
			if (mapValues[x][y-1]==mapValues[x][y])
				d = 1;
		}
		if (x!=(mapWidth-1))
		{
			if (mapValues[x+1][y]==mapValues[x][y])
				c = 1;
		}
		if (y!=(mapHeight-1))
		{
			if (mapValues[x][y+1]==mapValues[x][y])
				b = 1;
		}
		
		switch (mapValues[x][y])
		{
		case 1:
			transferTile = MainWindow.getBaseTile();
			break;
		case 2:
			transferTile = MainWindow.getRedFlag();
			break;
		case 3:
			transferTile = MainWindow.getBlueFlag();
			break;
		case 4:
			transferTile = MainWindow.getGreenFlag();
			break;
		case 5:
			transferTile = MainWindow.getTerrain1(controller[a][b][c][d]);
			break;
		case 6:
			transferTile = MainWindow.getTerrain2(controller[a][b][c][d]);
			break;
		case 8:
			transferTile = MainWindow.getRoads(controller[a][b][c][d]);
			break;
		case 9:
			transferTile = MainWindow.getLargeTerrain(1);
			break;
		case 10:
			transferTile = MainWindow.getLargeTerrain(0);
			break;
		case 11:
			transferTile = MainWindow.getLargeTerrain(2);
			break;
		}
		return transferTile;
	}
  
	public void newMapLoad()
	{
	    mapValues = new int[mapWidth][mapHeight];
	    for (int i=0; i<mapWidth; i++)
	    {
	    	for (int j=0; j<mapHeight; j++)
	    	{
	    		mapValues[i][j] = 1;
	    	    buttons[i][j].ChangeImage(MainWindow.getBaseTile());
	    	}
	    }    
	}
	
	private void loadTiles(int[][] mapData)
	{
		for (int i = 0; i<mapWidth; i++)
		{
			for (int j = 0; j<mapHeight; j++)
			{
				loadTile(i,j,mapData[i][j]);
			}
		}
	}
	
	private void loadTile(int i, int j, int type)
	{
		mapValues[i][j] = type;
		buttons[i][j] = new CanvasButton(null, i, j)		
		{
			public void actionPerformed(ActionEvent event)
			{
				canvasPaint(this.getXPos(),this.getYPos());
			}
		};
		;
	}
	
	private void finishTiles()
	{
		for (int i = 0; i<mapWidth; i++)
		{
			for (int j = 0; j<mapHeight; j++)
			{
				if (mapValues[i][j]==REDFLAGVALUE ||
					mapValues[i][j]==BLUEFLAGVALUE ||
					mapValues[i][j]==GREENFLAGVALUE)
					placeVillage(i,j, mapValues[i][j]);
				else if (mapValues[i][j]!=7)
					buttons[i][j].ChangeImage(canvasCheck(i,j));
				addButton(i,j);
			}
		}
	}
	
	public void writeMap(File myFile) throws IOException
	{
		FileWriter myFileWriter = null;
		myFileWriter = new FileWriter(myFile);
		
		writeParameters(myFileWriter);
		writeTiles(myFileWriter);
	
		myFileWriter.close();
	}
	
	private void writeParameters(FileWriter myFileWriter) throws IOException
	{
		myFileWriter.write((char)mapWidth);
		myFileWriter.write((char)mapHeight);
	}
	
	private void writeTiles(FileWriter myFileWriter) throws IOException
	{
		for (int i = 0; i<mapWidth; i++)
		{
			for (int j = 0; j<mapHeight; j++)
			{
				char myCharacter =(char) mapValues[i][j];
				myFileWriter.write(myCharacter);
			}
		}
	}
	
	public int[][] getMapWithWidthChange(int newWidth)
	{
		return getChangedMap(newWidth, mapHeight);
	}
	
	public int[][] getMapWithHeightChange(int newHeight)
	{
		return getChangedMap(mapWidth, newHeight);
	}
		
	private int[][] getChangedMap(int width, int height)
	{
		int[][] newData = new int[width][height];
	
		for (int i=0;i<width;i++)
		{
			for (int j=0;j<height;j++)
			{
				if (width<mapWidth || height<mapHeight)
					newData[i][j] = mapValues[i][j];
				else
					newData[i][j] = 1;
			}
		}
		
		return newData;
	}
	
	public int MapWidth()
	{
		return mapWidth;
	}

	public int MapHeight()
	{
		return mapHeight;
	}
	
@Override
public Dimension getPreferredScrollableViewportSize() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
	// TODO Auto-generated method stub
	return 0;
}

@Override
public boolean getScrollableTracksViewportHeight() {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean getScrollableTracksViewportWidth() {
	// TODO Auto-generated method stub
	return false;
}

@Override
public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
	// TODO Auto-generated method stub
	return 0;
}
}
