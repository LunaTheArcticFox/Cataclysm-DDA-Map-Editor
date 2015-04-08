package net.krazyweb.cataclysm.mapeditor;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Tile {

	public enum AdditionalTileType {
		CENTER, CORNER, EDGE, END_PIECE, T_CONNECTION, UNCONNECTED, BROKEN, OPEN
	}

	public static final Tile.AdditionalTileType[] BITWISE_TYPES = {
			Tile.AdditionalTileType.UNCONNECTED,
			Tile.AdditionalTileType.END_PIECE,
			Tile.AdditionalTileType.END_PIECE,
			Tile.AdditionalTileType.CORNER,
			Tile.AdditionalTileType.END_PIECE,
			Tile.AdditionalTileType.EDGE,
			Tile.AdditionalTileType.CORNER,
			Tile.AdditionalTileType.T_CONNECTION,
			Tile.AdditionalTileType.END_PIECE,
			Tile.AdditionalTileType.CORNER,
			Tile.AdditionalTileType.EDGE,
			Tile.AdditionalTileType.T_CONNECTION,
			Tile.AdditionalTileType.CORNER,
			Tile.AdditionalTileType.T_CONNECTION,
			Tile.AdditionalTileType.T_CONNECTION,
			Tile.AdditionalTileType.CENTER
	};

	public static final int[] BITWISE_ROTATIONS = {
			0, 0, 270, 0, 180, 0, 270, 270, 90, 90, 90, 0, 180, 90, 180, 0
	};

	public static Map<String, Tile> tiles = new TreeMap<>();

	private final String id;
	public boolean connectsToWalls = false;
	public boolean rotates = false;

	private Map<AdditionalTileType, Tile> additionalTiles = new HashMap<>();

	public Tile(final String id) {
		this.id = id;
	}

	public static Tile get(final String tileId) {
		return tiles.get(tileId);
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
