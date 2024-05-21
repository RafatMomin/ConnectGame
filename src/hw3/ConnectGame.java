package hw3;

import java.util.ArrayList;
import java.util.Random;

import api.ScoreUpdateListener;
import api.ShowDialogListener;
import api.Tile;

/**
 * Class that models a game.
 * @author Rafat Momin
 */
public class ConnectGame {
	
	/*
	 * Dialog Listener object  
	 */
	private ShowDialogListener dialogListener;
	
	/*
	 * Score Listener object
	 */
	private ScoreUpdateListener scoreListener;
	
	/*
	 * This is a random object, we want the same randomness throughout the game
	 */
	private Random random;
	
	/*
	 * This variable Stores maximum tile level
	 */
	private int maxTileLevel;
	
	/*
	 * This variable Stores minimum tile level
	 */
	private int minTileLevel;
	
	/*
	 * This is the game board, where all the tiles are residing!
	 */
	private Grid grid;
	
	/*
	 * This variable will store the score!
	 */
	private long score;
	
	/*
	 * List to get the track of already selected tiles!
	 */
	private ArrayList<Tile> selectedTiles;
	
	/**
	 * Constructs a new ConnectGame object with given grid dimensions and minimum
	 * and maximum tile levels.
	 * 
	 * @param width  grid width
	 * @param height grid height
	 * @param min    minimum tile level
	 * @param max    maximum tile level
	 * @param rand   random number generator
	 */
	public ConnectGame(int width, int height, int min, int max, Random rand) {
		// TODO
		random = rand;
		grid = new Grid(width, height);
		minTileLevel = min;
		maxTileLevel = max;
		selectedTiles = new ArrayList<>();
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
		return new Tile(minTileLevel + random.nextInt(maxTileLevel - minTileLevel));
	}

	/**
	 * Regenerates the grid with all random tiles produced by getRandomTile().
	 */
	public void radomizeTiles() {
		// TODO
		for (int i = 0; i < grid.getWidth(); i++) {
			for (int j = 0; j < grid.getHeight(); j++) {
				grid.setTile(getRandomTile(), i, j);
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
		if(t1.getX() >= t2.getX() - 1 && t1.getX() <= t2.getX() + 1) {
			if(t1.getY() >= t2.getY() - 1 && t1.getY() <= t2.getY() + 1) {
				return true;
			}
		}
		return false;
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
		// TODO
		if (selectedTiles.size() == 0) {
			grid.getTile(x, y).setSelect(true);
			selectedTiles.add(grid.getTile(x, y));
			return true;
		}
		return false;
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
		Tile tileBeforeLast = null;

		if (selectedTiles.size() == 1) {
			if (grid.getTile(x, y).getLevel() != selectedTiles.get(0).getLevel())
				return;
		} else if (selectedTiles.size() > 1) {
			if (!grid.getTile(x, y).isSelected() && 
				grid.getTile(x, y).getLevel() != selectedTiles.get(selectedTiles.size() - 1).getLevel() && 
				grid.getTile(x, y).getLevel() != selectedTiles.get(selectedTiles.size() - 1).getLevel() + 1) {
				return;
			}
			tileBeforeLast = selectedTiles.get(selectedTiles.size() - 2);
		}

		if (selectedTiles.size() > 0) {
			Tile lastTile = null;
			lastTile = selectedTiles.get(selectedTiles.size() - 1);
			if (isAdjacent(grid.getTile(x, y), lastTile) && !grid.getTile(x, y).isSelected()) {
				grid.getTile(x, y).setSelect(true);
				selectedTiles.add(grid.getTile(x, y));
			} else if (tileBeforeLast != null && isAdjacent(grid.getTile(x, y), lastTile) && grid.getTile(x, y).isSelected()) {
				lastTile.setSelect(false);
				selectedTiles.remove(selectedTiles.size() - 1);
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
		// TODO
		if (selectedTiles.get(selectedTiles.size()-1) == grid.getTile(x, y)) {
			if (selectedTiles.size() == 1) {
				selectedTiles.get(selectedTiles.size() - 1).setSelect(false);
				selectedTiles.clear();
			} else if (selectedTiles.size() > 1) {
				upgradeLastSelectedTile();
				dropSelected();
				for(int i = 0; i < selectedTiles.size(); ++i) {
					selectedTiles.get(i).setSelect(false);
				}
				selectedTiles.clear();
			} 
			return true;
		}
		return false;
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
	public void upgradeLastSelectedTile() {
		// TODO
		Tile t = selectedTiles.get(selectedTiles.size() - 1);
		score += t.getValue();
		int newLevel = t.getLevel() + 1;
		t.setLevel(newLevel);
		t.setSelect(false);
		selectedTiles.remove(selectedTiles.get(selectedTiles.size() - 1));
		if (t.getLevel() > maxTileLevel) {
			dialogListener.showDialog("Removing old blocks: " + (int) Math.pow(2, minTileLevel) + ", and creating new block: "
					+  (int) Math.pow(2, maxTileLevel + 1));
			maxTileLevel++;
			minTileLevel++;
			dropLevel(minTileLevel-1);
		}
	}

	/**
	 * Gets the selected tiles in the form of an array. This does not mean selected
	 * tiles must be stored in this class as a array.
	 * 
	 * @return the selected tiles in the form of an array
	 */
	public Tile[] getSelectedAsArray() {
		// TODO
		return selectedTiles.toArray(Tile[]::new);
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
		for (int row = 0; row < grid.getWidth(); ++row) {
			for (int col = 0; col < grid.getHeight(); ++col) {
				if (level == grid.getTile(row, col).getLevel()) {
					for (int temp = col; temp > 0; --temp) {
						grid.setTile(grid.getTile(row, temp - 1), row, temp);
					}
					grid.setTile(getRandomTile(), row, 0);
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
		for(int i = 0; i < selectedTiles.size(); ++i) {
			score += selectedTiles.get(i).getValue();
			int row = selectedTiles.get(i).getX();
			for (int j = selectedTiles.get(i).getY(); j > 0; --j) {
				Tile moveTile = grid.getTile(row, j - 1);
				grid.setTile(moveTile, row, j);
			}
			grid.setTile(getRandomTile(), row, 0);
		}
		
		scoreListener.updateScore(score);
	}

	/**
	 * Remove the tile from the selected tiles.
	 * 
	 * @param x column of the tile
	 * @param y row of the tile
	 */
	public void unselect(int x, int y) {
		// TODO
		selectedTiles.remove(grid.getTile(x, y));
		grid.getTile(x, y).setSelect(false);
	}

	/**
	 * Gets the player's score.
	 * 
	 * @return the score
	 */
	public long getScore() {
		// TODO
		return score;
	}

	/**
	 * Gets the game grid.
	 * 
	 * @return the grid
	 */
	public Grid getGrid() {
		// TODO
		return grid;
	}

	/**
	 * Gets the minimum tile level.
	 * 
	 * @return the minimum tile level
	 */
	public int getMinTileLevel() {
		// TODO
		return minTileLevel;
	}

	/**
	 * Gets the maximum tile level.
	 * 
	 * @return the maximum tile level
	 */
	public int getMaxTileLevel() {
		// TODO
		return maxTileLevel;
	}

	/**
	 * Sets the player's score.
	 * 
	 * @param score number of points
	 */
	public void setScore(long score) {
		// TODO
		this.score = score;
	}

	/**
	 * Sets the game's grid.
	 * 
	 * @param grid game's grid
	 */
	public void setGrid(Grid grid) {
		// TODO
		this.grid = grid;
	}

	/**
	 * Sets the minimum tile level.
	 * 
	 * @param minTileLevel the lowest level tile
	 */
	public void setMinTileLevel(int minTileLevel) {
		// TODO
		this.minTileLevel = minTileLevel;
	}

	/**
	 * Sets the maximum tile level.
	 * 
	 * @param maxTileLevel the highest level tile
	 */
	public void setMaxTileLevel(int maxTileLevel) {
		// TODO
		this.maxTileLevel = maxTileLevel;
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
		GameFileUtil.load(filePath, this);
	}
}
