package net.krazyweb.cataclysm.mapeditor.map.undo;

import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import net.krazyweb.cataclysm.mapeditor.map.data.MapSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapSettingsChangeAction implements Action {

	private Logger log = LogManager.getLogger(MapSettingsChangeAction.class);

	private MapEditor map;
	private MapSettings oldSettings;
	private MapSettings newSettings;

	public MapSettingsChangeAction(final MapEditor map, final MapSettings oldSettings, final MapSettings newSettings) {
		this.map = map;
		this.oldSettings = oldSettings;
		this.newSettings = newSettings;
	}

	public void execute() {
		log.debug("Redoing settings change on map '" + map + "' - " + oldSettings + " -> " + newSettings);
		map.setMapSettings(newSettings);
	}

	public void undo() {
		log.debug("Undoing settings change on map '" + map + "' - " + newSettings + " -> " + oldSettings);
		map.setMapSettings(oldSettings);
	}

}
