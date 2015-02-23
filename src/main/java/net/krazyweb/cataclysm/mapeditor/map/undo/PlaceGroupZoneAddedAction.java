package net.krazyweb.cataclysm.mapeditor.map.undo;

import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import net.krazyweb.cataclysm.mapeditor.map.PlaceGroupZone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlaceGroupZoneAddedAction implements Action {

	private static Logger log = LogManager.getLogger(PlaceGroupZoneAddedAction.class);

	private MapEditor map;
	private int index;
	private PlaceGroupZone zone;

	public PlaceGroupZoneAddedAction(final MapEditor map, final int index, final PlaceGroupZone zone) {
		this.map = map;
		this.index = index;
		this.zone = zone;
	}

	public PlaceGroupZoneAddedAction(final MapEditor map, final PlaceGroupZone zone) {
		this(map, -1, zone);
	}

	@Override
	public void execute() {
		if (index == -1) {
			log.debug("Redoing addition of PlaceGroupZone '" + zone + "' on map '" + map + "'.");
			map.addPlaceGroupZone(zone);
		} else {
			log.debug("Redoing addition of PlaceGroupZone '" + zone + "' on map '" + map + "' at index '" + index + "'.");
			map.addPlaceGroupZone(index, zone);
		}
	}

	@Override
	public void undo() {
		log.debug("Undoing addition of PlaceGroupZone '" + zone + "' on map '" + map + "'.");
		map.removePlaceGroupZone(zone);
	}

}
