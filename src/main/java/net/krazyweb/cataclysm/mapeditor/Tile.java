package net.krazyweb.cataclysm.mapeditor;

import java.util.HashMap;
import java.util.Map;

public class Tile {

	public static enum AdditionalTileType {
		CENTER, CORNER, EDGE, END_PIECE, T_CONNECTION, UNCONNECTED, BROKEN, OPEN
	}

	public static Map<String, Tile> tiles = new HashMap<>();

	private int foreground;
	private int background;

	private Map<AdditionalTileType, Tile> additionalTiles = new HashMap<>();

	public Tile() {
	}

	public void setForeground(final int foreground) {
		this.foreground = foreground;
	}

	public void setBackground(final int background) {
		this.background = background;
	}

	public void addMultiTile(final Tile tile, final AdditionalTileType type) {
		additionalTiles.put(type, tile);
	}

	public boolean isMultiTile() {
		return !additionalTiles.isEmpty();
	}

	public int getForeground(final AdditionalTileType type) {
		return additionalTiles.get(type).foreground;
	}

	public int getForeground() {
		return foreground;
	}

	public int getBackground(final AdditionalTileType type) {
		return additionalTiles.get(type).background;
	}

	public int getBackground() {
		return background;
	}

}
