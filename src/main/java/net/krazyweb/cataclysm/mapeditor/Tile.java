package net.krazyweb.cataclysm.mapeditor;

import java.util.HashMap;
import java.util.Map;

public class Tile {

	private static int terrainTileCount = 0;
	private static int furnitureTileCount = 0;

	public static Map<String, Integer> terrain = new HashMap<>();
	public static Map<String, Integer> furniture = new HashMap<>();

	public static void addNewTile(final String tileName, boolean isFurniture) {
		if (isFurniture) {
			System.out.println("Mapping '" + tileName + "' to id '" + furnitureTileCount + "'");
			furniture.put(tileName, furnitureTileCount++);
		} else {
			System.out.println("Mapping '" + tileName + "' to id '" + terrainTileCount + "'");
			terrain.put(tileName, terrainTileCount++);
		}
	}
}
