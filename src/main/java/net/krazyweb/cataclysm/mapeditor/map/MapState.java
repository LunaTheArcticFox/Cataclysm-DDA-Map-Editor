package net.krazyweb.cataclysm.mapeditor.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapState {

	protected String[][] terrain = new String[CataclysmMap.SIZE][CataclysmMap.SIZE];
	protected String[][] furniture = new String[CataclysmMap.SIZE][CataclysmMap.SIZE];
	protected List<PlaceGroupZone> placeGroupZones = new ArrayList<>();

	protected MapState() {

	}

	protected MapState(final MapState mapState) {
		for (int x = 0; x < CataclysmMap.SIZE; x++) {
			for (int y = 0; y < CataclysmMap.SIZE; y++) {
				terrain[x][y] = mapState.terrain[x][y];
				furniture[x][y] = mapState.furniture[x][y];
			}
		}
		mapState.placeGroupZones.forEach(zone -> placeGroupZones.add(new PlaceGroupZone(zone)));
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MapState mapState = (MapState) o;

		if (placeGroupZones != null ? !placeGroupZones.equals(mapState.placeGroupZones) : mapState.placeGroupZones != null) {
			return false;
		}

		for (int x = 0; x < CataclysmMap.SIZE; x++) {
			for (int y = 0; y < CataclysmMap.SIZE; y++) {
				if (!mapState.terrain[x][y].equals(terrain[x][y]) || !mapState.furniture[x][y].equals(furniture[x][y])) {
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
