package net.krazyweb.cataclysm.mapeditor.events;

import net.krazyweb.cataclysm.mapeditor.Tile;

public class TilePickedEvent {

	private final Tile tile;

	public TilePickedEvent(final Tile tile) {
		this.tile = tile;
	}

	public Tile getTile() {
		return tile;
	}

}
