package net.krazyweb.cataclysm.mapeditor.map.undo;

import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import net.krazyweb.cataclysm.mapeditor.map.PlaceGroupZone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlaceGroupZoneMovedAction implements Action {

	private static Logger log = LogManager.getLogger(PlaceGroupZoneMovedAction.class);

	private MapEditor map;
	private int deltaX, deltaY;
	private PlaceGroupZone zone;

	public PlaceGroupZoneMovedAction(final MapEditor map, final PlaceGroupZone zone, final int deltaX, final int deltaY) {
		this.map = map;
		this.zone = zone;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}

	@Override
	public void execute() {
		log.debug("Redoing move of PlaceGroupZone '" + zone + "' on map '" + map + "'.");
		map.movePlaceGroupZone(zone, deltaX, deltaY);
	}

	@Override
	public void undo() {
		log.debug("Undoing move of PlaceGroupZone '" + zone + "' on map '" + map + "'.");
		map.movePlaceGroupZone(zone, -deltaX, -deltaY);
	}

}
