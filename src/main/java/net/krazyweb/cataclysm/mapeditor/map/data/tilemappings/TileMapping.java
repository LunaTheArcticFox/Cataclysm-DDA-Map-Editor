package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

import net.krazyweb.cataclysm.mapeditor.map.data.Jsonable;

public abstract class TileMapping implements Jsonable {

	public enum Type {
		FIELD, FURNITURE, GAS_PUMP, ITEM, ITEMS, MONSTER, MONSTERS,
		NPC, SIGN, TERRAIN, TOILET, TRAP, VEHICLE, VENDING_MACHINE
	}

	public abstract TileMapping copy();

}
