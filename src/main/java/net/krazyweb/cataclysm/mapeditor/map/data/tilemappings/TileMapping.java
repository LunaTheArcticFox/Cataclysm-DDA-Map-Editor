package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

import net.krazyweb.cataclysm.mapeditor.map.data.Jsonable;

public abstract class TileMapping implements Jsonable {
	public abstract TileMapping copy();
}
