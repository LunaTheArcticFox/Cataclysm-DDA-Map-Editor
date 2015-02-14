package net.krazyweb.cataclysm.mapeditor.events;

public class ZoomChangeEvent {

	private double zoomLevel;

	public ZoomChangeEvent(final double zoom) {
		zoomLevel = zoom / 100.0;
	}

	public double getZoomLevel() {
		return zoomLevel;
	}

}
