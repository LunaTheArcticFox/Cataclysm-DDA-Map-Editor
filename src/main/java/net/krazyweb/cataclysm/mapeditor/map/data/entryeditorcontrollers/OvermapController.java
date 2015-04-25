package net.krazyweb.cataclysm.mapeditor.map.data.entryeditorcontrollers;

import net.krazyweb.cataclysm.mapeditor.map.data.OvermapEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OvermapController {

	private static final Logger log = LogManager.getLogger(OvermapController.class);

	public void setOvermap(final OvermapEntry overmap) {
		log.debug(overmap);
	}

}
