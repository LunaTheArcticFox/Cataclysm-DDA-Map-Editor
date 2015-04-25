package net.krazyweb.cataclysm.mapeditor.map.data.entryeditorcontrollers;

import net.krazyweb.cataclysm.mapeditor.map.data.MonsterGroupEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MonsterGroupController {

	private static final Logger log = LogManager.getLogger(MonsterGroupController.class);

	public void setMonsterGroup(final MonsterGroupEntry monsterGroup) {
		log.debug(monsterGroup);
	}

}
