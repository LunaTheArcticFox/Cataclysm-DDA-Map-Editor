package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

import java.util.Optional;

public class ItemMapping extends TileMapping {

	public String item;
	public Optional<Integer> chance = Optional.empty();

	public ItemMapping(final String item) {
		this.item = item;
	}

	public ItemMapping(final String item, final Integer chance) {
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

		ItemMapping that = (ItemMapping) o;

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
	public ItemMapping copy() {
		return new ItemMapping(item, chance.orElse(null));
	}

	@Override
	public String toString() {
		return "[Item: " + item + ", Chance: " + chance.orElse(null) + "]";
	}

}
