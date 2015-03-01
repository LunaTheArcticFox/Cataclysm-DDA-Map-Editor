package net.krazyweb.cataclysm.mapeditor.map.data;

import java.util.*;

public class ItemGroupEntry implements Jsonable {

	public String id;
	public Map<String, Integer> items = new TreeMap<>();

	private ItemGroupEntry lastSavedState;

	public ItemGroupEntry() {

	}

	public ItemGroupEntry(Map<String, Integer> items, String id) {
		this.id = id;
		this.items = new TreeMap<>(items);
	}

	public ItemGroupEntry(final ItemGroupEntry entry) {
		this.id = entry.id;
		this.items = new TreeMap<>(entry.items);
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

		ItemGroupEntry that = (ItemGroupEntry) o;

		return id.equals(that.id) && items.equals(that.items);

	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + items.hashCode();
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
		items.entrySet().forEach(entry -> tempLines.add(INDENT + "[ \"" + entry.getKey() + "\", " + entry.getValue() + " ]"));

		for (int i = 0; i < tempLines.size(); i++) {
			lines.add(INDENT + tempLines.get(i) + ((i < tempLines.size() - 1) ? "," : ""));
		}

		lines.add(INDENT + "]");
		lines.add("}");

		return lines;

	}

}
