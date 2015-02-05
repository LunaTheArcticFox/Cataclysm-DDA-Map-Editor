package net.krazyweb.cataclysm.mapeditor.events;

import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;

public class MapLoadedEvent {

	private final CataclysmMap map;

	public MapLoadedEvent(final CataclysmMap map) {
		this.map = map;
	}

	public CataclysmMap getMap() {
		return map;
	}

}
