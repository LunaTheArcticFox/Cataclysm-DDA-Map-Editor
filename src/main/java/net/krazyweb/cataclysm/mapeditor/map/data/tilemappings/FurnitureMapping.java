package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

public class FurnitureMapping extends TileMapping {

	public String furniture;

	public FurnitureMapping(final String furniture) {
		this.furniture = furniture;
	}

	@Override
	public String getJson() {
		return "\"" + furniture + "\"";
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FurnitureMapping that = (FurnitureMapping) o;

		return furniture.equals(that.furniture);

	}

	@Override
	public int hashCode() {
		return furniture.hashCode();
	}

	@Override
	public FurnitureMapping copy() {
		return new FurnitureMapping(furniture);
	}

	@Override
	public String toString() {
		return "[Furniture: " + furniture + "]";
	}

}
