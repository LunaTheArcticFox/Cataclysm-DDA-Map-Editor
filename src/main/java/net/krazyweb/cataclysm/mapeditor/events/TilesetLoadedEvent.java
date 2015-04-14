package net.krazyweb.cataclysm.mapeditor.events;

import net.krazyweb.cataclysm.mapeditor.TileSet;

public class TilesetLoadedEvent {

	private final TileSet tileset;

	public TilesetLoadedEvent(final TileSet tileset) {
		this.tileset = tileset;
	}

	public TileSet getTileSet() {
		return tileset;
	}

}
