package net.krazyweb.cataclysm.mapeditor.map.data.entryeditorcontrollers;

import net.krazyweb.cataclysm.mapeditor.map.data.ItemGroupEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemGroupController {

	private static final Logger log = LogManager.getLogger(ItemGroupController.class);

	public void setItemGroup(final ItemGroupEntry itemGroup) {
		log.debug(itemGroup);
	}

}
