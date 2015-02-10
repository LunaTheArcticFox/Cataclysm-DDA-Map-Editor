package net.krazyweb.cataclysm.mapeditor.events;

import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;

public class MapSavedEvent {

	private final CataclysmMap map;

	public MapSavedEvent(final CataclysmMap map) {
		this.map = map;
	}

	public CataclysmMap getMap() {
		return map;
	}

}
