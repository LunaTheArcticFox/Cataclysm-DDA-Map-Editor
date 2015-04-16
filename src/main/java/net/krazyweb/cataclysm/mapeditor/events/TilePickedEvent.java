package net.krazyweb.cataclysm.mapeditor.events;

import net.krazyweb.cataclysm.mapeditor.map.data.MapTile;

public class TilePickedEvent {

	private final MapTile tile;

	public TilePickedEvent(final MapTile tile) {
		this.tile = tile;
	}

	public MapTile getTile() {
		return tile;
	}

}
