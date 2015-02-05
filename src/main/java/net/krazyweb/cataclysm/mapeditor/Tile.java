package net.krazyweb.cataclysm.mapeditor;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Tile {

	public static enum AdditionalTileType {
		CENTER, CORNER, EDGE, END_PIECE, T_CONNECTION, UNCONNECTED, BROKEN, OPEN
	}

	public static Map<String, Tile> tiles = new TreeMap<>();

	private String id;

	private Map<AdditionalTileType, Tile> additionalTiles = new HashMap<>();

	public Tile(final String id) {
		this.id = id;
	}

	public String getID() {
		return id;
	}

	public void addMultiTile(final Tile tile, final AdditionalTileType type) {
		additionalTiles.put(type, tile);
	}

	public boolean isMultiTile() {
		return !additionalTiles.isEmpty();
	}

	public Tile getTile() {
		return this;
	}

	public Tile getTile(final AdditionalTileType type) {
		if (additionalTiles.containsKey(type)) {
			return additionalTiles.get(type);
		} else {
			return getTile();
		}
	}

}
