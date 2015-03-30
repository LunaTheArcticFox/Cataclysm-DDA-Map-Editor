package net.krazyweb.cataclysm.mapeditor.map.data;

import java.util.ArrayList;
import java.util.List;

public class MapSettings implements Jsonable {

	public String overmapTerrain;
	public int weight = 200;

	public MapSettings() {

	}

	public MapSettings(final MapSettings mapSettings) {
		overmapTerrain = mapSettings.overmapTerrain;
		weight = mapSettings.weight;
	}

	public MapSettings(final String overmapTerrain, final int weight) {
		this.overmapTerrain = overmapTerrain;
		this.weight = weight;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MapSettings that = (MapSettings) o;

		return weight == that.weight && overmapTerrain.equals(that.overmapTerrain);

	}

	@Override
	public int hashCode() {
		int result = overmapTerrain != null ? overmapTerrain.hashCode() : 0;
		result = 31 * result + weight;
		return result;
	}

	@Override
	public List<String> getJsonLines() {

		List<String> lines = new ArrayList<>();

		lines.add("\"type\": \"mapgen\"");
		lines.add("\"om_terrain\": [ \"" + overmapTerrain + "\" ]");
		lines.add("\"method\": \"json\"");
		lines.add("\"weight\": " + weight);

		return lines;

	}

	@Override
	public String toString() {
		return "MapSettings[Overmap Terrain: " + overmapTerrain + ", Weight: " + weight + "]";
	}

}
