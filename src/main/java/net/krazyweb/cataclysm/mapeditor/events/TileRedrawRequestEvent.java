package net.krazyweb.cataclysm.mapeditor.events;

public class TileRedrawRequestEvent {

	private final int x, y;

	public TileRedrawRequestEvent(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
