package net.krazyweb.cataclysm.mapeditor;

import java.util.HashMap;
import java.util.Map;

public class Tile {

	public final int foreground;
	public final int background;

	public Tile(final int foreground, final int background) {
		this.foreground = foreground;
		this.background = background;
	}

	public static Map<String, Tile> tiles = new HashMap<>();

	public static void addNewTile(final String tileName, final int foreground, final int background) {
		tiles.put(tileName, new Tile(foreground, background));
	}

}
