package net.krazyweb.cataclysm.mapeditor.map.data;

import java.util.ArrayList;
import java.util.List;

public class OvermapEntry implements Jsonable {

	public String id;
	public String name;
	public boolean rotate;
	public boolean lineDrawing;
	public int symbol;
	public String symbolColor;
	public int seeCost;
	public String extras;
	public boolean knownUp;
	public boolean knownDown;
	public int monsterDensity;
	public boolean sidewalk;
	public boolean allowRoad;

	private OvermapEntry lastSavedState;

	public OvermapEntry() {
		name = "Overmap";
	}

	public OvermapEntry(final OvermapEntry entry) {
		id = entry.id;
		name = entry.name;
		rotate = entry.rotate;
		lineDrawing = entry.lineDrawing;
		knownUp = entry.knownUp;
		knownDown = entry.knownDown;
		symbol = entry.symbol;
		symbolColor = entry.symbolColor;
		seeCost = entry.seeCost;
		extras = entry.extras;
		monsterDensity = entry.monsterDensity;
		sidewalk = entry.sidewalk;
		allowRoad = entry.allowRoad;
	}

	public boolean isSaved() {
		return this.equals(lastSavedState);
	}

	public void markSaved() {
		lastSavedState = new OvermapEntry(this);
	}

	@SuppressWarnings("RedundantIfStatement")
	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		OvermapEntry that = (OvermapEntry) o;

		if (allowRoad != that.allowRoad) return false;
		if (knownDown != that.knownDown) return false;
		if (knownUp != that.knownUp) return false;
		if (lineDrawing != that.lineDrawing) return false;
		if (monsterDensity != that.monsterDensity) return false;
		if (rotate != that.rotate) return false;
		if (seeCost != that.seeCost) return false;
		if (sidewalk != that.sidewalk) return false;
		if (symbol != that.symbol) return false;
		if (extras != null ? !extras.equals(that.extras) : that.extras != null) return false;
		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (symbolColor != null ? !symbolColor.equals(that.symbolColor) : that.symbolColor != null) return false;

		return true;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (rotate ? 1 : 0);
		result = 31 * result + (lineDrawing ? 1 : 0);
		result = 31 * result + symbol;
		result = 31 * result + (symbolColor != null ? symbolColor.hashCode() : 0);
		result = 31 * result + seeCost;
		result = 31 * result + (extras != null ? extras.hashCode() : 0);
		result = 31 * result + (knownUp ? 1 : 0);
		result = 31 * result + (knownDown ? 1 : 0);
		result = 31 * result + monsterDensity;
		result = 31 * result + (sidewalk ? 1 : 0);
		result = 31 * result + (allowRoad ? 1 : 0);
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
		lines.add(INDENT + "\"line_drawing\": " + lineDrawing + ",");
		lines.add(INDENT + "\"sym\": " + symbol + ",");
		lines.add(INDENT + "\"color\": \"" + symbolColor + "\",");
		lines.add(INDENT + "\"see_cost\": " + seeCost + ",");
		lines.add(INDENT + "\"extras\": \"" + extras + "\",");
		lines.add(INDENT + "\"knownUp\": \"" + knownUp + "\",");
		lines.add(INDENT + "\"knownDown\": \"" + knownDown + "\",");
		lines.add(INDENT + "\"mondensity\": " + monsterDensity + ",");
		lines.add(INDENT + "\"sidewalk\": " + sidewalk);
		lines.add(INDENT + "\"allow_road\": " + allowRoad);
		lines.add("}");

		return lines;

	}

}
