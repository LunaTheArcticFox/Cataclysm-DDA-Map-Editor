package net.krazyweb.cataclysm.mapeditor.map;

import com.google.common.eventbus.Subscribe;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import net.krazyweb.cataclysm.mapeditor.ApplicationSettings;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.TileConfiguration;
import net.krazyweb.cataclysm.mapeditor.TileSet;
import net.krazyweb.cataclysm.mapeditor.events.TilesetLoadedEvent;
import net.krazyweb.cataclysm.mapeditor.map.tilemappings.FurnitureMapping;
import net.krazyweb.cataclysm.mapeditor.map.tilemappings.TerrainMapping;
import net.krazyweb.cataclysm.mapeditor.map.tilemappings.TileMapping;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapTile {

	private static TileSet tileSet;

	public List<TileMapping> tileMappings = new ArrayList<>();

	public String displayTerrain;
	public String displayFurniture;


	public MapTile() {
		if (tileSet == null) {
			tileSet = ApplicationSettings.currentTileset;
		}
	}

	public MapTile(final TileMapping... tileMappings) {
		Collections.addAll(this.tileMappings, tileMappings);
	}

	@Subscribe
	public static void tileSetLoadedEventListener(final TilesetLoadedEvent event) {
		tileSet = event.getTileSet();
	}

	public void add(final TileMapping mapping) {
		tileMappings.add(mapping);
		//TODO Allow randomization of terrain/furniture
		if (mapping instanceof TerrainMapping && displayTerrain == null) {
			displayTerrain = ((TerrainMapping) mapping).terrain;
		}
		if (mapping instanceof FurnitureMapping && displayFurniture == null) {
			displayFurniture = ((FurnitureMapping) mapping).furniture;
		}
	}

	public String getTileID() {
		for (TileMapping mapping : tileMappings) {
			if (mapping instanceof TerrainMapping) {
				return ((TerrainMapping) mapping).terrain;
			}
		}
		return "";
	}

	public boolean terrainConnectsTo(final MapTile tile) {
		if (displayTerrain == null || tile == null || tile.displayTerrain == null) {
			return false;
		}
		Tile tile1 = Tile.get(displayTerrain);
		Tile tile2 = Tile.get(tile.displayTerrain);
		return !(tile1 == null || tile2 == null) && (tile1.connectsToWalls && tile2.connectsToWalls || displayTerrain.equals(tile.displayTerrain));
	}

	public boolean furnitureConnectsTo(final MapTile tile) {
		return !(displayFurniture == null || tile == null || tile.displayFurniture == null) && displayFurniture.equals(tile.displayFurniture);
	}

	public Image getTexture(final int terrainBitwise, final int furnitureBitwise) {

		double terrainRotation = Math.toRadians(TileConfiguration.BITWISE_ROTATIONS[terrainBitwise]);
		double furnitureRotation = Math.toRadians(TileConfiguration.BITWISE_ROTATIONS[furnitureBitwise]);

		AffineTransform terrainTransform = new AffineTransform();
		terrainTransform.rotate(terrainRotation, tileSet.tileSize / 2, tileSet.tileSize / 2);

		AffineTransform furnitureTransform = new AffineTransform();
		furnitureTransform.rotate(furnitureRotation, tileSet.tileSize / 2, tileSet.tileSize / 2);

		//TODO Cache generated images instead of making new ones each call

		if (displayTerrain != null && displayFurniture != null) {

			if (!(TileConfiguration.get(displayTerrain).isMultiTile() || TileConfiguration.get(displayTerrain).rotates)) {
				terrainTransform = new AffineTransform();
			}

			if (!(TileConfiguration.get(displayFurniture).isMultiTile() || TileConfiguration.get(displayFurniture).rotates)) {
				furnitureTransform = new AffineTransform();
			}

			BufferedImage terrainImage = tileSet.textures.get(TileConfiguration.tiles.get(displayTerrain).getTile(TileConfiguration.BITWISE_TYPES[terrainBitwise]).getID());
			BufferedImage furnitureImage = tileSet.textures.get(TileConfiguration.tiles.get(displayFurniture).getTile(TileConfiguration.BITWISE_TYPES[furnitureBitwise]).getID());

			BufferedImage tempImage = new BufferedImage(tileSet.tileSize, tileSet.tileSize, BufferedImage.TYPE_4BYTE_ABGR);
			tempImage.createGraphics().drawImage(terrainImage, terrainTransform, null);
			tempImage.createGraphics().drawImage(furnitureImage, furnitureTransform, null);

			return SwingFXUtils.toFXImage(tempImage, null);

		} else if (displayTerrain != null) {

			if (TileConfiguration.tiles.containsKey(displayTerrain)) {

				if (!(TileConfiguration.get(displayTerrain).isMultiTile() || TileConfiguration.get(displayTerrain).rotates)) {
					terrainTransform = new AffineTransform();
				}

				BufferedImage terrainImage = tileSet.textures.get(TileConfiguration.get(displayTerrain).getTile(TileConfiguration.BITWISE_TYPES[terrainBitwise]).getID());
				BufferedImage tempImage = new BufferedImage(tileSet.tileSize, tileSet.tileSize, BufferedImage.TYPE_4BYTE_ABGR);
				tempImage.createGraphics().drawImage(terrainImage, terrainTransform, null);

				return SwingFXUtils.toFXImage(tempImage, null);

			}

		} else if (displayFurniture != null) {

			if (TileConfiguration.tiles.containsKey(displayFurniture)) {

				if (!(TileConfiguration.get(displayFurniture).isMultiTile() || TileConfiguration.get(displayFurniture).rotates)) {
					furnitureTransform = new AffineTransform();
				}

				BufferedImage furnitureImage = tileSet.textures.get(TileConfiguration.get(displayFurniture).getTile(TileConfiguration.BITWISE_TYPES[furnitureBitwise]).getID());
				BufferedImage tempImage = new BufferedImage(tileSet.tileSize, tileSet.tileSize, BufferedImage.TYPE_4BYTE_ABGR);
				tempImage.createGraphics().drawImage(furnitureImage, furnitureTransform, null);
				return SwingFXUtils.toFXImage(tempImage, null);

			}

		}

		return SwingFXUtils.toFXImage(new BufferedImage(tileSet.tileSize, tileSet.tileSize, BufferedImage.TYPE_4BYTE_ABGR), null);

	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MapTile mapTile = (MapTile) o;

		return tileMappings.equals(mapTile.tileMappings);

	}

	@Override
	public int hashCode() {
		return tileMappings.hashCode();
	}

	@Override
	public String toString() {
		String output = "MapTile[";
		for (int i = 0; i < tileMappings.size(); i++) {
			output += tileMappings.get(i);
			if (i != tileMappings.size() - 1) {
				output += ", ";
			}
		}
		return output + "]";
	}

}
