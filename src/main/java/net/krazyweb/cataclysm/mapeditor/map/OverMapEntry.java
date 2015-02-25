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
