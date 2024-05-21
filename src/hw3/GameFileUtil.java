package hw3;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import api.Tile;

/**
 * Utility class with static methods for saving and loading game files.
 * @author Rafat Momin
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
			// TODO: write to file, can use writer.write()
			
			writer.write( game.getGrid().getWidth() + " " + 
						  game.getGrid().getHeight() + " " + 
						  game.getMinTileLevel() + " " + 
						  game.getMaxTileLevel() + " " + 
						  game.getScore() + "\n" );
			
			for (int j = 0; j < game.getGrid().getHeight(); ++j) {
				if (j != 0)
					writer.write("\n");
				for (int i = 0; i < game.getGrid().getWidth(); ++i) {
					if (i != 0)
						writer.write(" ");
					writer.write(game.getGrid().getTile(i, j).getLevel() + "");
				}
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the file at the given file path into the given game object. When the
	 * method returns the game object has been modified to represent the loaded
	 * game.
	 * <p>
	 * See the save() method for the specification of the file format.
	 * 
	 * @param filePath the path of the file to load
	 * @param game     the game to modify
	 */
	public static void load(String filePath, ConnectGame game) {
		try {
			Scanner scnr = new Scanner(new File(filePath));
			int width = scnr.nextInt();
			Grid grid = new Grid(width, scnr.nextInt());
			game.setGrid(grid);
			game.setMinTileLevel(scnr.nextInt());
			game.setMaxTileLevel(scnr.nextInt());
			game.setScore(scnr.nextInt());
			
			int i, j = 0;
			while (scnr.hasNextLine()) {
				i = 0;
				while (scnr.hasNextInt() && i < width) {
					grid.setTile(new Tile(scnr.nextInt()), i, j);
					++i;
				}
				++j;
			}
			scnr.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
