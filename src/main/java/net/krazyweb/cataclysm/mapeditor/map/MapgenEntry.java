package net.krazyweb.cataclysm.mapeditor.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapgenEntry {

	protected String[][] terrain = new String[MapEditor.SIZE][MapEditor.SIZE];
	protected String[][] furniture = new String[MapEditor.SIZE][MapEditor.SIZE];
	protected List<PlaceGroupZone> placeGroupZones = new ArrayList<>();
	protected MapSettings settings = new MapSettings();

	protected MapgenEntry() {

	}

	public MapgenEntry(final MapgenEntry mapgenEntry) {
		for (int x = 0; x < MapEditor.SIZE; x++) {
			for (int y = 0; y < MapEditor.SIZE; y++) {
				terrain[x][y] = mapgenEntry.terrain[x][y];
				furniture[x][y] = mapgenEntry.furniture[x][y];
			}
		}
		mapgenEntry.placeGroupZones.forEach(zone -> placeGroupZones.add(new PlaceGroupZone(zone)));
		settings = new MapSettings(mapgenEntry.settings);
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MapgenEntry mapgenEntry = (MapgenEntry) o;

		if (placeGroupZones != null ? !placeGroupZones.equals(mapgenEntry.placeGroupZones) : mapgenEntry.placeGroupZones != null) {
			return false;
		}

		if (settings != null ? !settings.equals(mapgenEntry.settings) : mapgenEntry.settings != null) {
			return false;
		}

		for (int x = 0; x < MapEditor.SIZE; x++) {
			for (int y = 0; y < MapEditor.SIZE; y++) {
				if (!mapgenEntry.terrain[x][y].equals(terrain[x][y]) || !mapgenEntry.furniture[x][y].equals(furniture[x][y])) {
					return false;
				}
			}
		}

		return true;

	}

	@Override
	public int hashCode() {
		int result = (placeGroupZones != null ? placeGroupZones.hashCode() : 0);
		result = 31 * result + Arrays.deepHashCode(terrain);
		result = 31 * result + Arrays.deepHashCode(furniture);
		return result;
	}

}
