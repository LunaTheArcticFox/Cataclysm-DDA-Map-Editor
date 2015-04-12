package net.krazyweb.cataclysm.mapeditor.map.data;

import java.util.ArrayList;
import java.util.List;

public interface Jsonable {
	String INDENT = "    ";
	default List<String> getJsonLines() {
		return new ArrayList<>();
	}
	default String getJson() {
		return "";
	}
}
