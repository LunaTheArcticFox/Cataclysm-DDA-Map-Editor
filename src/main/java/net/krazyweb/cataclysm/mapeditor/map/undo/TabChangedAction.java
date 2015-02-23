package net.krazyweb.cataclysm.mapeditor.map.undo;

import javafx.scene.control.Tab;
import net.krazyweb.cataclysm.mapeditor.map.MapManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TabChangedAction implements Action {

	private static Logger log = LogManager.getLogger(TabChangedAction.class);

	private MapManager mapManager;
	private Tab previousTab;
	private Tab selectedTab;

	public TabChangedAction(final MapManager mapManager, final Tab previousTab, final Tab selectedTab) {
		this.mapManager = mapManager;
		this.previousTab = previousTab;
		this.selectedTab = selectedTab;
	}

	@Override
	public void execute() {
		log.debug("Redoing tab selection of '" + previousTab + "' to '" + selectedTab + "'.");
		mapManager.setTab(selectedTab);
	}

	@Override
	public void undo() {
		log.debug("Undoing tab selection of '" + previousTab + "' to '" + selectedTab + "'.");
		mapManager.setTab(previousTab);
	}

}
