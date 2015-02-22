package net.krazyweb.cataclysm.mapeditor.map.undo;

import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RotateMapAction implements Action {

	private static Logger log = LogManager.getLogger(RotateMapAction.class);

	private CataclysmMap map;

	public RotateMapAction(final CataclysmMap map) {
		this.map = map;
	}

	@Override
	public void execute() {
		log.debug("Redoing rotation of map '" + map + "'.");
		map.rotateMapClockwise();
	}

	@Override
	public void undo() {
		log.debug("Undoing rotation of map '" + map + "'.");
		map.rotateMapClockwise();
		map.rotateMapClockwise();
		map.rotateMapClockwise();
	}

}
