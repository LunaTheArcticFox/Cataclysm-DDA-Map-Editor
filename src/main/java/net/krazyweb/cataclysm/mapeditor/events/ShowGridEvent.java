package net.krazyweb.cataclysm.mapeditor.events;

public class ShowGridEvent {

	private boolean showGrid;

	public ShowGridEvent(final boolean showGrid) {
		this.showGrid = showGrid;
	}

	public boolean showGrid() {
		return showGrid;
	}
}
