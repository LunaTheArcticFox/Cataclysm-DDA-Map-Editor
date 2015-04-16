package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

public class TerrainMapping extends TileMapping {

	public String terrain;

	public TerrainMapping(final String terrain) {
		this.terrain = terrain;
	}

	@Override
	public String getJson() {
		return "\"" + terrain + "\"";
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TerrainMapping that = (TerrainMapping) o;

		return terrain.equals(that.terrain);

	}

	@Override
	public int hashCode() {
		return terrain.hashCode();
	}

	@Override
	public TerrainMapping copy() {
		return new TerrainMapping(terrain);
	}

	@Override
	public String toString() {
		return terrain;
	}

}
