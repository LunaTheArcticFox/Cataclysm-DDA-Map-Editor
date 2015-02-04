package net.krazyweb.cataclysm.mapeditor;

import java.util.HashMap;
import java.util.Map;

public class Tile {

	public static enum AdditionalTileType {
		CENTER, CORNER, EDGE, END_PIECE, T_CONNECTION, UNCONNECTED, BROKEN, OPEN
	}

	public static Map<String, Tile> tiles = new HashMap<>();

	private String name;
	private int foreground;
	private int background;

	private Map<AdditionalTileType, Tile> additionalTiles = new HashMap<>();

	public Tile(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
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
		if (additionalTiles.containsKey(type)) {
			return additionalTiles.get(type).foreground;
		} else {
			return getForeground();
		}
	}

	public int getForeground() {
		return foreground;
	}

	public int getBackground(final AdditionalTileType type) {
		if (additionalTiles.containsKey(type)) {
			return additionalTiles.get(type).background;
		} else {
			return getBackground();
		}
	}

	public int getBackground() {
		return background;
	}

}
