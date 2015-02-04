package net.krazyweb.cataclysm.mapeditor.events;

import net.krazyweb.cataclysm.mapeditor.MapgenMap;

public class MapLoadedEvent {

	private final MapgenMap map;

	public MapLoadedEvent(final MapgenMap map) {
		this.map = map;
	}

	public MapgenMap getMap() {
		return map;
	}

}
