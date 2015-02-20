package net.krazyweb.cataclysm.mapeditor.events;

public class ShowGroupsEvent {

	private boolean showGrid;

	public ShowGroupsEvent(final boolean showGrid) {
		this.showGrid = showGrid;
	}

	public boolean showGroups() {
		return showGrid;
	}
}
