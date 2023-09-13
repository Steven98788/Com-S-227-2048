package hw3;

/**
 * 	
 Utility class with static methods for saving and loading game files.
 * 
 * @author Steven Bui
 */

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import api.Tile;

/**
 * Utility class with static methods for saving and loading game files.
 */
public class GameFileUtil {
	/**
	 * Saves the current game state to a file at the given file path.
	 * <p>
	 * The format of the file is one line of game data followed by multiple lines of
	 * game grid. The first line contains the: width, height, minimum tile level,
	 * maximum tile level, and score. The grid is represented by tile levels. The
	 * conversion to tile values is 2^level, for example, 1 is 2, 2 is 4, 3 is 8, 4
	 * is 16, etc. The following is an example:
	 * 
	 * <pre>
	 * 5 8 1 4 100
	 * 1 1 2 3 1
	 * 2 3 3 1 3
	 * 3 3 1 2 2
	 * 3 1 1 3 1
	 * 2 1 3 1 2
	 * 2 1 1 3 1
	 * 4 1 3 1 1
	 * 1 3 3 3 3
	 * </pre>
	 * 
	 * @param filePath the path of the file to save
	 * @param game     the game to save
	 */
	public static void save(String filePath, ConnectGame game) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
			Grid grid = game.getGrid();
			int width = grid.getWidth();
			int height = grid.getHeight();
			int minTileLevel = game.getMinTileLevel();
			int maxTileLevel = game.getMaxTileLevel();
			long score = game.getScore();
			writer.write(width + " " + height + " " + minTileLevel + " " + maxTileLevel + " " + score);
			for (int col = 0; col < height; col++) {
				writer.write('\n');
				for (int row = 0; row < width; row++) {
					Tile tile = grid.getTile(row, col);
					if (tile != null && row == width - 1) {
						writer.write(tile.getLevel() + "");
					} 
					else if(tile!=null) {
						writer.write(tile.getLevel() + " ");
					}
					else {
						writer.write("0 ");
					}
				}
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param filePath
	 * @param game
	 * 
	 * This method Loads the save file
	 */

	public static void load(String filePath, ConnectGame game) {
		try {
			Scanner reader = new Scanner(new FileReader(filePath));
			int width = reader.nextInt();
			int height = reader.nextInt();
			int minTileLevel = reader.nextInt();
			int maxTileLevel = reader.nextInt();
			long score = reader.nextLong();
			Grid grid = new Grid(width, height);
			for (int row = 0; row < height; row++) {
				for (int col = 0; col < width; col++) {
					int level = reader. nextInt();
					if (level != 0) {
						Tile tile = new Tile(level);
						grid.setTile(tile, col, row);
					}
				}
			}
			game.setGrid(grid);
			game.setMinTileLevel(minTileLevel);
			game.setMaxTileLevel(maxTileLevel);
			game.setScore(score);
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

