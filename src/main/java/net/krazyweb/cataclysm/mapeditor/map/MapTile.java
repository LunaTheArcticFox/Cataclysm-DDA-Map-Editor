package net.krazyweb.cataclysm.mapeditor.map;

public class MapTile {

	public String terrain;
	public String furniture;

	public MapTile() {

	}

	public MapTile(final String terrain, final String furniture) {
		if (terrain != null && !terrain.equals("t_null")) {
			this.terrain = terrain;
		}
		if (furniture != null && !furniture.equals("f_null")) {
			this.furniture = furniture;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MapTile mapTile = (MapTile) o;

		if (furniture != null ? !furniture.equals(mapTile.furniture) : mapTile.furniture != null) return false;
		if (terrain != null ? !terrain.equals(mapTile.terrain) : mapTile.terrain != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = terrain != null ? terrain.hashCode() : 0;
		result = 31 * result + (furniture != null ? furniture.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return terrain + " " + furniture;
	}

}
