package net.krazyweb.cataclysm.mapeditor.map.undo;

import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import net.krazyweb.cataclysm.mapeditor.map.data.MapTile;
import net.krazyweb.cataclysm.mapeditor.tools.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TileChangeAction implements Action {

	private Logger log = LogManager.getLogger(TileChangeAction.class);

	private MapEditor map;
	private Point location;
	private MapTile before, after;

	public TileChangeAction(final MapEditor map, final Point location, final MapTile before, final MapTile after) {
		this.map = map;
		this.location = location;
		this.before = before;
		this.after = after;
	}

	public void execute() {
		log.debug("Redoing tile change on map '" + map + "', " + location + ", " + before + " -> " + after);
		map.setTile(location, after);
	}

	public void undo() {
		log.debug("Undoing tile change on map '" + map + "', " + location + ", " + after + " -> " + before);
		map.setTile(location, before);
	}

}
