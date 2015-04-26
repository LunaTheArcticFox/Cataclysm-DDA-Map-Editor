package net.krazyweb.cataclysm.mapeditor.map.data;

import java.util.*;

public class ItemGroupEntry implements Jsonable {

	public static class ItemSpawn {

		public String name;
		public int chance;

		public ItemSpawn() {
		}

		public ItemSpawn(final String name, final int chance) {
			this.name = name;
			this.chance = chance;
		}

		public ItemSpawn(final ItemSpawn itemSpawn) {
			name = itemSpawn.name;
			chance = itemSpawn.chance;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			ItemSpawn itemSpawn = (ItemSpawn) o;

			if (chance != itemSpawn.chance) return false;
			return name.equals(itemSpawn.name);

		}

		@Override
		public int hashCode() {
			int result = name.hashCode();
			result = 31 * result + chance;
			return result;
		}

		public ItemSpawn copy() {
			return new ItemSpawn(this);
		}

	}

	public String id;
	public List<ItemSpawn> itemSpawns = new ArrayList<>();

	private ItemGroupEntry lastSavedState;

	public ItemGroupEntry() {
		id = "item_group";
	}

	public ItemGroupEntry(List<ItemSpawn> itemSpawns, String id) {
		this.id = id;
		this.itemSpawns = new ArrayList<>();
		itemSpawns.forEach(itemGroup -> this.itemSpawns.add(itemGroup.copy()));
	}

	public ItemGroupEntry(final ItemGroupEntry entry) {
		this.id = entry.id;
		this.itemSpawns = new ArrayList<>();
		entry.itemSpawns.forEach(itemGroup -> this.itemSpawns.add(itemGroup.copy()));
	}

	public boolean isSaved() {
		return this.equals(lastSavedState);
	}

	public void markSaved() {
		lastSavedState = new ItemGroupEntry(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ItemGroupEntry entry = (ItemGroupEntry) o;

		if (!id.equals(entry.id)) return false;
		return itemSpawns.equals(entry.itemSpawns);

	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + itemSpawns.hashCode();
		return result;
	}

	@Override
	public List<String> getJsonLines() {

		List<String> lines = new ArrayList<>();

		lines.add("{");
		lines.add(INDENT + "\"type\": \"item_group\",");
		lines.add(INDENT + "\"id\": \"" + id + "\",");
		lines.add(INDENT + "\"items\": [");

		List<String> tempLines = new ArrayList<>();
		itemSpawns.forEach(itemGroup -> tempLines.add(INDENT + "[ \"" + itemGroup.name + "\", " + itemGroup.chance + " ]"));

		for (int i = 0; i < tempLines.size(); i++) {
			lines.add(INDENT + tempLines.get(i) + ((i < tempLines.size() - 1) ? "," : ""));
		}

		lines.add(INDENT + "]");
		lines.add("}");

		return lines;

	}

}
