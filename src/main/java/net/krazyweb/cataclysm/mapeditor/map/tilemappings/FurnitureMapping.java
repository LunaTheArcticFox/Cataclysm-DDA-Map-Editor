package net.krazyweb.cataclysm.mapeditor.map.tilemappings;

import java.util.ArrayList;
import java.util.List;

public class FurnitureMapping extends TileMapping {

	public String furniture;

	public FurnitureMapping(final String furniture) {
		this.furniture = furniture;
	}

	@Override
	public List<String> getJsonLines() {
		List<String> lines = new ArrayList<>();
		lines.add(furniture);
		return lines;
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

}
