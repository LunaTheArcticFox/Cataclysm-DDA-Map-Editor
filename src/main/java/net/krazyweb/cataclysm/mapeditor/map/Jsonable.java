package net.krazyweb.cataclysm.mapeditor.map;

import java.util.List;

public interface Jsonable {
	public static final String INDENT = "    ";
	public List<String> getJsonLines();
}
