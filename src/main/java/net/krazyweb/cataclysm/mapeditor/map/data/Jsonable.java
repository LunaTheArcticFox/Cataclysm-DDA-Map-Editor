package net.krazyweb.cataclysm.mapeditor.map.data;

import java.util.List;

public interface Jsonable {
	String INDENT = "    ";
	List<String> getJsonLines();
}
