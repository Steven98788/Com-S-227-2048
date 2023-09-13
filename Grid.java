package hw3;

/**
 * 
 * 	Class Description:
	Represents the game's grid.
 * 
 * @author Steven Bui
 */

import api.Tile;

/**
 * Represents the game's grid.
 */
public class Grid {
	
	private int rows;
	private int columns;
	private Tile[][] gameGrid;
	
	
	/**
	 * Creates a new grid.
	 * 
	 * @param width  number of columns
	 * @param height number of rows
	 */
	

	public Grid(int width, int height) {
		// TODO Auto-generated constructor stub
		rows = width;
		columns = height;
		gameGrid = new Tile[height][width];
		
	}

	/**
	 * Get the grid's width.
	 * 
	 * @return width
	 */
	public int getWidth() {
		// TODO
		return rows;
	}

	/**
	 * Get the grid's height.
	 * 
	 * @return height
	 */
	public int getHeight() {
		// TODO
		return columns;
	}

	/**
	 * Gets the tile for the given column and row.
	 * 
	 * @param x the column
	 * @param y the row
	 * @return
	 */
	public api.Tile getTile(int x, int y) {
		// TODO
		
		return gameGrid[y][x];
	}

	/**
	 * Sets the tile for the given column and row. Calls tile.setLocation().
	 * 
	 * @param tile the tile to set
	 * @param x    the column
	 * @param y    the row
	 */
	public void setTile(Tile tile, int x, int y) {
		// TODO
		
		tile.setLocation(y, x);
		gameGrid[y][x] = tile;
		
	}
	
	@Override
	public String toString() {
		String str = "";
		for (int y=0; y<getHeight(); y++) {
			if (y > 0) {
				str += "\n";
			}
			str += "[";
			for (int x=0; x<getWidth(); x++) {
				if (x > 0) {
					str += ",";
				}
				str += getTile(x, y);
			}
			str += "]";
		}
		return str;
	}
}
