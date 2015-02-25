package net.krazyweb.cataclysm.mapeditor.events;

import net.krazyweb.cataclysm.mapeditor.map.MapEditor;

public class MapSavedEvent {

	private final MapEditor map;

	public MapSavedEvent(final MapEditor map) {
		this.map = map;
	}

	public MapEditor getMap() {
		return map;
	}

}
