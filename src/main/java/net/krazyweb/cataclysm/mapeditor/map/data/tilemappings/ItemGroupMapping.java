package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

import java.util.Optional;

public class ItemGroupMapping extends TileMapping {

	public String item;
	public Optional<Integer> chance = Optional.empty();

	public ItemGroupMapping(final String item) {
		this.item = item;
	}

	public ItemGroupMapping(final String item, final Integer chance) {
		this.item = item;
		this.chance = Optional.ofNullable(chance);
	}

	@Override
	public String getJson() {
		String output = "{ \"item\": \"" + item + "\"";
		if (chance.isPresent()) {
			output += ", \"chance\": " + chance.get();
		}
		return output + " }";
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ItemGroupMapping that = (ItemGroupMapping) o;

		if (!item.equals(that.item)) return false;
		return chance.equals(that.chance);

	}

	@Override
	public int hashCode() {
		int result = item.hashCode();
		result = 31 * result + chance.hashCode();
		return result;
	}

	@Override
	public ItemGroupMapping copy() {
		return new ItemGroupMapping(item, chance.orElse(null));
	}

	@Override
	public String toString() {
		return "[Item Group: " + item + ", Chance: " + chance.orElse(null) + "]";
	}

}
