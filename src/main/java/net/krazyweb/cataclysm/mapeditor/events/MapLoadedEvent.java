package net.krazyweb.cataclysm.mapeditor.events;

import net.krazyweb.cataclysm.mapeditor.map.MapEditor;

public class MapLoadedEvent {

	private final MapEditor map;

	public MapLoadedEvent(final MapEditor map) {
		this.map = map;
	}

	public MapEditor getMap() {
		return map;
	}

}
