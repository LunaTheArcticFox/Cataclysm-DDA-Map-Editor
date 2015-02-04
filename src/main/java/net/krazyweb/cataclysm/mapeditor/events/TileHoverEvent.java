package net.krazyweb.cataclysm.mapeditor.events;

public class TileHoverEvent {

	private final String tileName;
	private final int x, y;

	public TileHoverEvent(final String tileName, final int x, final int y) {
		this.tileName = tileName;
		this.x = x;
		this.y = y;
	}

	public String getTileName() {
		return tileName;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
