package net.krazyweb.cataclysm.mapeditor.events;

import net.krazyweb.cataclysm.mapeditor.map.MapTile;

public class TileHoverEvent {

	private final MapTile tile;
	private final int x, y;

	public TileHoverEvent(final MapTile tile, final int x, final int y) {
		this.tile = tile;
		this.x = x;
		this.y = y;
	}

	public MapTile getTile() {
		return tile;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
