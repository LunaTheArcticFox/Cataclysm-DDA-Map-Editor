package net.krazyweb.cataclysm.mapeditor.map;

import java.util.ArrayList;
import java.util.List;

public class MapSettings implements Jsonable {

	protected String overMapTerrain;
	protected int weight = 200;

	public MapSettings() {

	}

	public MapSettings(final MapSettings mapSettings) {
		overMapTerrain = mapSettings.overMapTerrain;
		weight = mapSettings.weight;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MapSettings mapSettings = (MapSettings) o;

		if (weight != mapSettings.weight) return false;
		if (overMapTerrain != null ? !overMapTerrain.equals(mapSettings.overMapTerrain) : mapSettings.overMapTerrain != null) {
			return false;
		}

		return true;

	}

	@Override
	public int hashCode() {
		int result = overMapTerrain != null ? overMapTerrain.hashCode() : 0;
		result = 31 * result + weight;
		return result;
	}

	@Override
	public List<String> getJsonLines() {

		List<String> lines = new ArrayList<>();

		lines.add("\"type\": \"mapgen\"");
		lines.add("\"om_terrain\": [ \"" + overMapTerrain + "\" ]");
		lines.add("\"method\": \"json\"");
		lines.add("\"weight\": " + weight);

		return lines;

	}

}
