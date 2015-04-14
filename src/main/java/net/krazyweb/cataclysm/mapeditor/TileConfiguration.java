package net.krazyweb.cataclysm.mapeditor;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class TileConfiguration {

	public enum AdditionalTileType {
		CENTER, CORNER, EDGE, END_PIECE, T_CONNECTION, UNCONNECTED, BROKEN, OPEN
	}

	public static final TileConfiguration.AdditionalTileType[] BITWISE_TYPES = {
			TileConfiguration.AdditionalTileType.UNCONNECTED,
			TileConfiguration.AdditionalTileType.END_PIECE,
			TileConfiguration.AdditionalTileType.END_PIECE,
			TileConfiguration.AdditionalTileType.CORNER,
			TileConfiguration.AdditionalTileType.END_PIECE,
			TileConfiguration.AdditionalTileType.EDGE,
			TileConfiguration.AdditionalTileType.CORNER,
			TileConfiguration.AdditionalTileType.T_CONNECTION,
			TileConfiguration.AdditionalTileType.END_PIECE,
			TileConfiguration.AdditionalTileType.CORNER,
			TileConfiguration.AdditionalTileType.EDGE,
			TileConfiguration.AdditionalTileType.T_CONNECTION,
			TileConfiguration.AdditionalTileType.CORNER,
			TileConfiguration.AdditionalTileType.T_CONNECTION,
			TileConfiguration.AdditionalTileType.T_CONNECTION,
			TileConfiguration.AdditionalTileType.CENTER
	};

	public static final int[] BITWISE_ROTATIONS = {
			0, 0, 270, 0, 180, 0, 270, 270, 90, 90, 90, 0, 180, 90, 180, 0
	};

	public static Map<String, TileConfiguration> tiles = new TreeMap<>();

	private final String id;
	public boolean rotates;

	private Map<AdditionalTileType, TileConfiguration> additionalTiles = new HashMap<>();

	public TileConfiguration(final String id) {
		this.id = id;
	}

	public static TileConfiguration get(final String tileId) {
		return tiles.get(tileId);
	}

	public String getID() {
		return id;
	}

	public void addMultiTile(final TileConfiguration tileConfiguration, final AdditionalTileType type) {
		additionalTiles.put(type, tileConfiguration);
	}

	public boolean isMultiTile() {
		return !additionalTiles.isEmpty();
	}

	public TileConfiguration getTile() {
		return this;
	}

	public TileConfiguration getTile(final AdditionalTileType type) {
		if (additionalTiles.containsKey(type)) {
			return additionalTiles.get(type);
		} else {
			return getTile();
		}
	}

}
