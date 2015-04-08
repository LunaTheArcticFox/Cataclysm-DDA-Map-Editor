package net.krazyweb.cataclysm.mapeditor.map;

import net.krazyweb.cataclysm.mapeditor.map.tilemappings.TerrainMapping;
import net.krazyweb.cataclysm.mapeditor.map.tilemappings.TileMapping;

import java.util.ArrayList;
import java.util.List;

public class MapTile {

	public List<TileMapping> tileMappings = new ArrayList<>();

	public MapTile() {

	}

	public void add(final TileMapping mapping) {
		tileMappings.add(mapping);
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MapTile mapTile = (MapTile) o;

		return tileMappings.equals(mapTile.tileMappings);

	}

	@Override
	public int hashCode() {
		return tileMappings.hashCode();
	}

	public String getTileID() {
		for (TileMapping mapping : tileMappings) {
			if (mapping instanceof TerrainMapping) {
				return ((TerrainMapping) mapping).terrain;
			}
		}
		return "";
	}

}
