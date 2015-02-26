package net.krazyweb.cataclysm.mapeditor.map;

import java.util.ArrayList;
import java.util.List;

public class OverMapEntry implements Jsonable {

	public String id;
	public String name;
	public boolean rotate;
	public int symbol;
	public String symbolColor;
	public int seeCost;
	public String extras;
	public int monsterDensity;
	public boolean sidewalk;

	private OverMapEntry lastSavedState;

	public OverMapEntry() {

	}

	public OverMapEntry(final OverMapEntry entry) {
		id = entry.id;
		name = entry.name;
		rotate = entry.rotate;
		symbol = entry.symbol;
		symbolColor = entry.symbolColor;
		seeCost = entry.seeCost;
		extras = entry.extras;
		monsterDensity = entry.monsterDensity;
		sidewalk = entry.sidewalk;
	}

	public boolean isSaved() {
		return this.equals(lastSavedState);
	}

	protected void markSaved() {
		lastSavedState = new OverMapEntry(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		OverMapEntry that = (OverMapEntry) o;

		if (monsterDensity != that.monsterDensity) return false;
		if (rotate != that.rotate) return false;
		if (seeCost != that.seeCost) return false;
		if (sidewalk != that.sidewalk) return false;
		if (symbol != that.symbol) return false;
		if (extras != null ? !extras.equals(that.extras) : that.extras != null) return false;
		if (!id.equals(that.id)) return false;
		if (!name.equals(that.name)) return false;
		if (!symbolColor.equals(that.symbolColor)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + name.hashCode();
		result = 31 * result + (rotate ? 1 : 0);
		result = 31 * result + symbol;
		result = 31 * result + symbolColor.hashCode();
		result = 31 * result + seeCost;
		result = 31 * result + (extras != null ? extras.hashCode() : 0);
		result = 31 * result + monsterDensity;
		result = 31 * result + (sidewalk ? 1 : 0);
		return result;
	}

	@Override
	public List<String> getJsonLines() {

		List<String> lines = new ArrayList<>();

		lines.add("{");
		lines.add(INDENT + "\"type\": \"overmap_terrain\",");
		lines.add(INDENT + "\"id\": \"" + id + "\",");
		lines.add(INDENT + "\"name\": \"" + name + "\",");
		lines.add(INDENT + "\"rotate\": " + rotate + ",");
		lines.add(INDENT + "\"sym\": " + symbol + ",");
		lines.add(INDENT + "\"color\": \"" + symbolColor + "\",");
		lines.add(INDENT + "\"see_cost\": " + seeCost + ",");
		lines.add(INDENT + "\"extras\": \"" + extras + "\",");
		lines.add(INDENT + "\"mondensity\": " + monsterDensity + ",");
		lines.add(INDENT + "\"sidewalk\": " + sidewalk);
		lines.add("}");

		return lines;

	}

}
