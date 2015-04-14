package net.krazyweb.cataclysm.mapeditor.map.undo;

import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapFillTerrainChangeAction implements Action {

	private static final Logger log = LogManager.getLogger(MapFillTerrainChangeAction.class);

	private MapEditor mapEditor;
	private String oldFillTerrain;
	private String newFillTerrain;

	public MapFillTerrainChangeAction(final MapEditor mapEditor, final String oldFillTerrain, final String newFillTerrain) {
		this.mapEditor = mapEditor;
		this.oldFillTerrain = oldFillTerrain;
		this.newFillTerrain = newFillTerrain;
	}

	public void execute() {
		log.debug("Redoing fill terrain change on map '" + mapEditor + "' - " + oldFillTerrain + " -> " + newFillTerrain);
		mapEditor.setFillTerrain(newFillTerrain);
	}

	public void undo() {
		log.debug("Undoing fill terrain change on map '" + mapEditor + "' - " + newFillTerrain + " -> " + oldFillTerrain);
		mapEditor.setFillTerrain(oldFillTerrain);
	}

}
