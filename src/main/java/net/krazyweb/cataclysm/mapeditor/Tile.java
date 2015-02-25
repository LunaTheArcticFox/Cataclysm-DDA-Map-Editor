package net.krazyweb.cataclysm.mapeditor;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Tile {

	private static final Map<String, String> tileGroups = new HashMap<>();

	public static enum AdditionalTileType {
		CENTER, CORNER, EDGE, END_PIECE, T_CONNECTION, UNCONNECTED, BROKEN, OPEN
	}

	public static Map<String, Tile> tiles = new TreeMap<>();

	private String id;

	private Map<AdditionalTileType, Tile> additionalTiles = new HashMap<>();

	public Tile(final String id) {
		this.id = id;
		tileGroups.put("t_wall_h", "wallGroup");
		tileGroups.put("t_wall_v", "wallGroup");
		tileGroups.put("t_window", "wallGroup");
		tileGroups.put("t_window_taped", "wallGroup");
		tileGroups.put("t_window_domestic", "wallGroup");
		tileGroups.put("t_window_domestic_taped", "wallGroup");
		tileGroups.put("t_window_open", "wallGroup");
		tileGroups.put("t_curtains", "wallGroup");
		tileGroups.put("t_window_alarm", "wallGroup");
		tileGroups.put("t_window_alarm_taped", "wallGroup");
		tileGroups.put("t_window_empty", "wallGroup");
		tileGroups.put("t_window_frame", "wallGroup");
		tileGroups.put("t_window_boarded", "wallGroup");
		tileGroups.put("t_window_boarded_noglass", "wallGroup");
		tileGroups.put("t_window_reinforced", "wallGroup");
		tileGroups.put("t_window_reinforced_noglass", "wallGroup");
		tileGroups.put("t_window_enhanced", "wallGroup");
		tileGroups.put("t_window_enhanced_noglass", "wallGroup");
		tileGroups.put("t_window_bars_alarm", "wallGroup");
		tileGroups.put("t_door_glass_c", "wallGroup");
		tileGroups.put("t_door_c", "wallGroup");
		tileGroups.put("t_door_c_peep", "wallGroup");
		tileGroups.put("t_door_b", "wallGroup");
		tileGroups.put("t_door_b_peep", "wallGroup");
		tileGroups.put("t_door_o", "wallGroup");
		tileGroups.put("t_door_o_peep", "wallGroup");
		tileGroups.put("t_rdoor_c", "wallGroup");
		tileGroups.put("t_rdoor_b", "wallGroup");
		tileGroups.put("t_rdoor_o", "wallGroup");
		tileGroups.put("t_door_locked", "wallGroup");
		tileGroups.put("t_door_locked_interior", "wallGroup");
		tileGroups.put("t_door_locked_peep", "wallGroup");
		tileGroups.put("t_door_locked_alarm", "wallGroup");
		tileGroups.put("t_door_frame", "wallGroup");
		tileGroups.put("t_mdoor_frame", "wallGroup");
		tileGroups.put("t_door_boarded", "wallGroup");
		tileGroups.put("t_door_boarded_damaged", "wallGroup");
		tileGroups.put("t_door_boarded_peep", "wallGroup");
		tileGroups.put("t_rdoor_boarded", "wallGroup");
		tileGroups.put("t_rdoor_boarded_damaged", "wallGroup");
		tileGroups.put("t_door_boarded_damaged_peep", "wallGroup");
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

	public boolean isFurniture() {
		return id.startsWith("f_");
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

	public static boolean tilesConnect(final String tile1, final String tile2) {

		if (tile1.equals(tile2)) {
			return true;
		}

		if (tileGroups.containsKey(tile1) && tileGroups.containsKey(tile2)) {
			return tileGroups.get(tile1).equals(tileGroups.get(tile2));
		}

		return (tile1.endsWith("_v") || tile1.endsWith("_h")) && (tile2.endsWith("_v") || tile2.endsWith("_h")) && tile1.startsWith(tile2.substring(0, tile2.lastIndexOf("_")));

	}

}
