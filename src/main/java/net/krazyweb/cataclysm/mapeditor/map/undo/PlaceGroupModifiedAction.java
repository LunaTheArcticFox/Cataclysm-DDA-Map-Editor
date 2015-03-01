package net.krazyweb.cataclysm.mapeditor.map.undo;

import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import net.krazyweb.cataclysm.mapeditor.map.data.PlaceGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlaceGroupModifiedAction implements Action {

	private static Logger log = LogManager.getLogger(PlaceGroupModifiedAction.class);

	private MapEditor map;
	private PlaceGroup placeGroup;
	private String beforeType, afterType, beforeGroup, afterGroup;
	private int beforeChance, afterChance;

	public PlaceGroupModifiedAction(final MapEditor map, final PlaceGroup placeGroup, final String type, final String group, final int chance) {
		this.map = map;
		this.placeGroup = placeGroup;
		this.beforeType = placeGroup.type;
		this.beforeGroup = placeGroup.group;
		this.beforeChance = placeGroup.chance;
		this.afterType = type;
		this.afterGroup = group;
		this.afterChance = chance;
	}

	@Override
	public void execute() {
		log.debug("Redoing modification of PlaceGroup '" + placeGroup + "' on map '" + map + "'." +
				" (New Values: [Type: " + afterType + ", Group: " + afterGroup + ", Chance: " + afterChance + "])");
		map.modifyPlaceGroup(placeGroup, afterType, afterGroup, afterChance);
	}

	@Override
	public void undo() {
		log.debug("Undoing modification of PlaceGroup '" + placeGroup + "' on map '" + map + "'." +
				" (New Values: [Type: " + beforeType + ", Group: " + beforeGroup + ", Chance: " + beforeChance + "])");
		map.modifyPlaceGroup(placeGroup, beforeType, beforeGroup, beforeChance);
	}

}
