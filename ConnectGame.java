package hw3;

/**
 * Class that models a game.
 * 
 * @author Steven Bui
 */

import java.util.ArrayList;

import java.util.Random;

import api.ScoreUpdateListener;
import api.ShowDialogListener;
import api.Tile;

/**
 * Class that models a game.
 */
public class ConnectGame {
	private ShowDialogListener dialogListener;
	private ScoreUpdateListener scoreListener;
	private int rows;
	private int columns;
	private int minimumTile;
	private int maximumTile;
	private int random;
	private long playerScore = 0;
	private Grid gameGrid;
	private Random rand = new Random();
	private Tile lastChosen;
	private Tile startTile;
	private ArrayList<Tile> chosenTiles;

	/**
	 * Constructs a new ConnectGame object with given grid dimensions and minimum
	 * and maximum tile levels.
	 * 
	 * @param rows    grid rows
	 * @param columns grid columns
	 * @param min     minimum tile level
	 * @param max     maximum tile level
	 * @param rand    random number generator
	 */
	public ConnectGame(int rows, int columns, int min, int max, Random rand) {
		// TODO
		gameGrid = new Grid(rows, columns);
		this.rows = rows;
		this.columns = columns;
		this.minimumTile = min;
		this.maximumTile = max;
		this.chosenTiles = new ArrayList<>();
		this.rand = rand;

	}

	/**
	 * Gets a random tile with level between minimum tile level inclusive and
	 * maximum tile level exclusive. For example, if minimum is 1 and maximum is 4,
	 * the random tile can be either 1, 2, or 3.
	 * <p>
	 * DO NOT RETURN TILES WITH MAXIMUM LEVEL
	 * 
	 * @return a tile with random level between minimum inclusive and maximum
	 *         exclusive
	 */
	public Tile getRandomTile() {
		// TODO
		random = rand.nextInt(maximumTile - minimumTile) + minimumTile;
		Tile tile = new Tile(random);
		return tile;
	}

	/**
	 * Regenerates the grid with all random tiles produced by getRandomTile().
	 */
	public void radomizeTiles() {
		// TODO
		for (int row = 0; row < columns; row++) {
			for (int col = 0; col < rows; col++) {
				Tile newTile = getRandomTile();
				gameGrid.setTile(newTile, col, row);

			}
		}
	}

	/**
	 * Determines if two tiles are adjacent to each other. The may be next to each
	 * other horizontally, vertically, or diagonally.
	 * 
	 * @param t1 one of the two tiles
	 * @param t2 one of the two tiles
	 * @return true if they are next to each other horizontally, vertically, or
	 *         diagonally on the grid, false otherwise
	 */
	public boolean isAdjacent(Tile t1, Tile t2) {
		// TODO
		int x1 = t1.getX();
		int y1 = t1.getY();
		int x2 = t2.getX();
		int y2 = t2.getY();

		int xDiff = Math.abs(x1 - x2);
		int yDiff = Math.abs(y1 - y2);

		if (xDiff + yDiff == 1 || xDiff + yDiff == 2) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Indicates the user is trying to select (clicked on) a tile to start a new
	 * selection of tiles.
	 * <p>
	 * If a selection of tiles is already in progress, the method should do nothing
	 * and return false.
	 * <p>
	 * If a selection is not already in progress (this is the first tile selected),
	 * then start a new selection of tiles and return true.
	 * 
	 * @param x the column of the tile selected
	 * @param y the row of the tile selected
	 * @return true if this is the first tile selected, otherwise false
	 */
	public boolean tryFirstSelect(int x, int y) {

		Tile selectedTile = gameGrid.getTile(x, y);
		if (!chosenTiles.isEmpty()) {
			return false;
		} else {
			startTile = selectedTile;
			lastChosen = startTile;
			chosenTiles.add(selectedTile);
			selectedTile.setSelect(true);
			return true;
		}

	}

	/**
	 * Indicates the user is trying to select (mouse over) a tile to add to the
	 * selected sequence of tiles. The rules of a sequence of tiles are:
	 * 
	 * <pre>
	 * 1. The first two tiles must have the same level.
	 * 2. After the first two, each tile must have the same level or one greater than the level of the previous tile.
	 * </pre>
	 * 
	 * For example, given the sequence: 1, 1, 2, 2, 2, 3. The next selected tile
	 * could be a 3 or a 4. If the use tries to select an invalid tile, the method
	 * should do nothing. If the user selects a valid tile, the tile should be added
	 * to the list of selected tiles.
	 * 
	 * @param x the column of the tile selected
	 * @param y the row of the tile selected
	 */
	public void tryContinueSelect(int x, int y) {

		// TODO
		Tile previousTile = chosenTiles.get(chosenTiles.size() - 1);
		Tile selectedTile = gameGrid.getTile(x, y);
		if (isAdjacent(selectedTile, previousTile)) {
			if (chosenTiles.size() == 1) {
				if (selectedTile.getLevel() == chosenTiles.get(0).getLevel()) {
					selectedTile.setSelect(true);
					chosenTiles.add(selectedTile);
					lastChosen = selectedTile;
				}
			} else {
				if (selectedTile == chosenTiles.get(chosenTiles.size() - 2)) {
					unselect(previousTile.getY(), previousTile.getX());
				} else if (selectedTile.getLevel() - previousTile.getLevel() == 1
						|| selectedTile.getLevel() == previousTile.getLevel()) {
					selectedTile.setSelect(true);
					chosenTiles.add(selectedTile);
					lastChosen = selectedTile;
				}
			}
		}

	}

	/**
	 * Indicates the user is trying to finish selecting (click on) a sequence of
	 * tiles. If the method is not called for the last selected tile, it should do
	 * nothing and return false. Otherwise it should do the following:
	 * 
	 * <pre>
	 * 1. When the selection contains only 1 tile reset the selection and make sure all tiles selected is set to false.
	 * 2. When the selection contains more than one block:
	 *     a. Upgrade the last selected tiles with upgradeLastSelectedTile().
	 *     b. Drop all other selected tiles with dropSelected().
	 *     c. Reset the selection and make sure all tiles selected is set to false.
	 * </pre>
	 * 
	 * @param x the column of the tile selected
	 * @param y the row of the tile selected
	 * @return return false if the tile was not selected, otherwise return true
	 */
	public boolean tryFinishSelection(int x, int y) {
		int addScore = 0;
		boolean isLastTileSelected = false;

		if (lastChosen != null && lastChosen.isSelected()) {
			lastChosen.setLocation(x, y);

			if (lastChosen.getX() == startTile.getX() && lastChosen.getY() == startTile.getY()) {
				startTile.setSelect(false);
				lastChosen.setSelect(false);
				startTile = null;
				lastChosen = null;
				chosenTiles.clear();
				isLastTileSelected = true;
			} else {
				for (Tile selectedTile : chosenTiles) {
					addScore += Math.pow(2, selectedTile.getLevel());
					selectedTile.setSelect(false);
				}

				upgradeLastSelectedTile();
				dropSelected();
				isLastTileSelected = true;
				playerScore += addScore;
				setScore(playerScore);
				chosenTiles.clear();
				startTile = null;
				lastChosen = null;
			}
		}

		return isLastTileSelected;
	}

	/**
	 * Increases the level of the last selected tile by 1 and removes that tile from
	 * the list of selected tiles. The tile itself should be set to unselected.
	 * <p>
	 * If the upgrade results in a tile that is greater than the current maximum
	 * tile level, both the minimum and maximum tile level are increased by 1. A
	 * message dialog should also be displayed with the message "New block 32,
	 * removing blocks 2". Not that the message shows tile values and not levels.
	 * Display a message is performed with dialogListener.showDialog("Hello,
	 * World!");
	 */

	// TODO

	public void upgradeLastSelectedTile() {
		lastChosen.setLevel(lastChosen.getLevel() + 1);
		unselect(lastChosen.getY(), lastChosen.getX());
		int level = lastChosen.getLevel();
		lastChosen.setSelect(false);

		if (level > maximumTile) {
			maximumTile++;
			int highestValue = (int) Math.pow(2, maximumTile);
			int lowestValue = (int) Math.pow(2, minimumTile);
			minimumTile++;
			String message = "New block " + highestValue + ", removing blocks " + lowestValue;
			dialogListener.showDialog(message);
		}
	}

	/**
	 * Gets the selected tiles in the form of an array. This does not mean selected
	 * tiles must be stored in this class as a array.
	 * 
	 * @return the selected tiles in the form of an array
	 */
	public Tile[] getSelectedAsArray() {

		return chosenTiles.toArray(new Tile[chosenTiles.size()]);
	}

	/**
	 * Removes all tiles of a particular level from the grid. When a tile is
	 * removed, the tiles above it drop down one spot and a new random tile is
	 * placed at the top of the grid.
	 * 
	 * @param level the level of tile to remove
	 */
	public void dropLevel(int level) {
		// TODO
		int rows = gameGrid.getWidth();
		int columns = gameGrid.getHeight();

		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows; j++) {
				Tile tile = gameGrid.getTile(j, i);
				if (tile.getLevel() == level) {
					for (int k = i - 1; k >= 0; k--) {
						Tile toReplace = gameGrid.getTile(j, k);
						gameGrid.setTile(toReplace, j, k + 1);
					}
					gameGrid.setTile(getRandomTile(), j, 0);
					break;
				}
			}
		}
	}

	/**
	 * Removes all selected tiles from the grid. When a tile is removed, the tiles
	 * above it drop down one spot and a new random tile is placed at the top of the
	 * grid.
	 */
	public void dropSelected() {
		// TODO

		for (int i = 0; i < gameGrid.getWidth(); i++) {

			for (int j = 0; j < gameGrid.getHeight(); j++) {
				if (chosenTiles.contains(gameGrid.getTile(i, j))) {
					gameGrid.setTile(getRandomTile(), i, j);
					if (j == 0) {
						// grid.setTile(getRandomTile(), i, j);
					} else if (j >= 1) {
						// grid.getTile(i, j-1);
						gameGrid.setTile(gameGrid.getTile(i, j - 1), i, j);
					}
				}
			}
		}
	}

	/**
	 * Remove the tile from the selected tiles.
	 * 
	 * @param x column of the tile
	 * @param y row of the tile
	 */
	public void unselect(int x, int y) {
		// TODO
		gameGrid.getTile(x, y).setSelect(false);
		chosenTiles.remove(gameGrid.getTile(x, y));
	}

	/**
	 * Gets the player's score.
	 * 
	 * @return the score
	 */
	public long getScore() {
		// TODO
		return playerScore;
	}

	/**
	 * Gets the game grid.
	 * 
	 * @return the grid
	 */
	public Grid getGrid() {
		// TODO
		return gameGrid;
	}

	/**
	 * Gets the minimum tile level.
	 * 
	 * @return the minimum tile level
	 */
	public int getMinTileLevel() {
		// TODO
		return minimumTile;
	}

	/**
	 * Gets the maximum tile level.
	 * 
	 * @return the maximum tile level
	 */
	public int getMaxTileLevel() {
		// TODO
		return maximumTile;
	}

	/**
	 * Sets the player's score.
	 * 
	 * @param score number of points
	 */
	public void setScore(long score) {
		// TODO
		playerScore = score;
		try {
			scoreListener.updateScore(score);

		} catch (NullPointerException e) {

		}
	}

	/**
	 * Sets the game's grid.
	 * 
	 * @param grid game's grid
	 */
	public void setGrid(Grid grid) {
		// TODO

		this.gameGrid = grid;
	}

	/**
	 * Sets the minimum tile level.
	 * 
	 * @param minTileLevel the lowest level tile
	 */
	public void setMinTileLevel(int minTileLevel) {
		// TODO
		minimumTile = minTileLevel;
	}

	/**
	 * Sets the maximum tile level.
	 * 
	 * @param maxTileLevel the highest level tile
	 */
	public void setMaxTileLevel(int maxTileLevel) {
		// TODO
		maximumTile = maxTileLevel;
	}

	/**
	 * Sets callback listeners for game events.
	 * 
	 * @param dialogListener listener for creating a user dialog
	 * @param scoreListener  listener for updating the player's score
	 */
	public void setListeners(ShowDialogListener dialogListener, ScoreUpdateListener scoreListener) {
		this.dialogListener = dialogListener;
		this.scoreListener = scoreListener;
	}

	/**
	 * Save the game to the given file path.
	 * 
	 * @param filePath location of file to save
	 */
	public void save(String filePath) {
		GameFileUtil.save(filePath, this);
	}

	/**
	 * Load the game from the given file path
	 * 
	 * @param filePath location of file to load
	 */
	public void load(String filePath) {
		try {
			GameFileUtil.load(filePath, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
