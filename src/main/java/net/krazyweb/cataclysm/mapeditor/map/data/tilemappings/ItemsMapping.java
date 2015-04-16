package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

import java.util.ArrayList;
import java.util.List;

public class ItemsMapping extends TileMapping {

	public String item;
	public int chance;

	public ItemsMapping(final String item, final int chance) {
		this.item = item;
		this.chance = chance;
	}

	@Override
	public List<String> getJsonLines() {
		List<String> lines = new ArrayList<>();
		lines.add("{ \"item\": \"" + item + "\", \"chance\": " + chance + " }");
		return lines;
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ItemsMapping that = (ItemsMapping) o;

		if (chance != that.chance) return false;
		return item.equals(that.item);

	}

	@Override
	public int hashCode() {
		int result = item.hashCode();
		result = 31 * result + chance;
		return result;
	}

	@Override
	public ItemsMapping copy() {
		return new ItemsMapping(item, chance);
	}

}
