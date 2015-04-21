package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

public class ItemGroupMapping extends TileMapping {

	public String item;
	public int chance;

	public ItemGroupMapping(final String item, final int chance) {
		this.item = item;
		this.chance = chance;
	}

	@Override
	public String getJson() {
		return "{ \"item\": \"" + item + "\", \"chance\": " + chance + " }";
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ItemGroupMapping that = (ItemGroupMapping) o;

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
	public ItemGroupMapping copy() {
		return new ItemGroupMapping(item, chance);
	}

	@Override
	public String toString() {
		return "[Item Group: " + item + ", Chance: " + chance + "]";
	}

}
