package net.krazyweb.cataclysm.mapeditor.map.data;

import com.google.common.eventbus.Subscribe;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import net.krazyweb.cataclysm.mapeditor.ApplicationSettings;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.TileConfiguration;
import net.krazyweb.cataclysm.mapeditor.TileSet;
import net.krazyweb.cataclysm.mapeditor.events.TilesetLoadedEvent;
import net.krazyweb.cataclysm.mapeditor.map.data.tilemappings.*;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapTile {

	private static TileSet tileSet;

	public List<TileMapping> tileMappings = new ArrayList<>();

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

	public void clear() {
		tileMappings.clear();
	}

	public void add(final TileMapping mapping) {
		tileMappings.add(mapping);
		//TODO Allow randomization of terrain/furniture
	}

	public String getDisplayTerrain() {

		boolean isTerrain = true;
		String terrain = null;

		for (TileMapping mapping : tileMappings) {
			if (mapping instanceof TerrainMapping) {
				if (isTerrain && terrain == null) {
					terrain = ((TerrainMapping) mapping).terrain;
				}
			}
			if (mapping instanceof GasPumpMapping) {
				isTerrain = false;
				terrain = "t_gas_pump";
			}
		}

		return terrain;

	}

	public String getDisplayFurniture() {

		boolean isFurniture = true;
		String furniture = null;

		for (TileMapping mapping : tileMappings) {
			if (mapping instanceof FurnitureMapping) {
				if (isFurniture && furniture == null) {
					furniture = ((FurnitureMapping) mapping).furniture;
				}
			} else if (mapping instanceof ToiletMapping) {
				isFurniture = false;
				furniture = "f_toilet";
			} else if (mapping instanceof SignMapping) {
				isFurniture = false;
				furniture = "f_sign";
			} else if (mapping instanceof VendingMachineMapping) {
				isFurniture = false;
				furniture = "f_vending_c";
			}
		}

		return furniture;

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
		if (getDisplayTerrain() == null || tile == null || tile.getDisplayTerrain() == null) {
			return false;
		}
		Tile tile1 = Tile.get(getDisplayTerrain());
		Tile tile2 = Tile.get(tile.getDisplayTerrain());
		return !(tile1 == null || tile2 == null) && (tile1.connectsToWalls && tile2.connectsToWalls || getDisplayTerrain().equals(tile.getDisplayTerrain()));
	}

	public boolean furnitureConnectsTo(final MapTile tile) {
		return !(getDisplayFurniture() == null || tile == null || tile.getDisplayFurniture() == null) && getDisplayFurniture().equals(tile.getDisplayFurniture());
	}

	public Image getTexture(final int terrainBitwise, final int furnitureBitwise) {

		double terrainRotation = Math.toRadians(TileConfiguration.BITWISE_ROTATIONS[terrainBitwise]);
		double furnitureRotation = Math.toRadians(TileConfiguration.BITWISE_ROTATIONS[furnitureBitwise]);

		AffineTransform terrainTransform = new AffineTransform();
		terrainTransform.rotate(terrainRotation, tileSet.tileSize / 2, tileSet.tileSize / 2);

		AffineTransform furnitureTransform = new AffineTransform();
		furnitureTransform.rotate(furnitureRotation, tileSet.tileSize / 2, tileSet.tileSize / 2);

		//TODO Cache generated images instead of making new ones each call

		if (getDisplayTerrain() != null && getDisplayFurniture() != null) {

			if (!(TileConfiguration.get(getDisplayTerrain()).isMultiTile() || TileConfiguration.get(getDisplayTerrain()).rotates)) {
				terrainTransform = new AffineTransform();
			}

			if (!(TileConfiguration.get(getDisplayFurniture()).isMultiTile() || TileConfiguration.get(getDisplayFurniture()).rotates)) {
				furnitureTransform = new AffineTransform();
			}

			BufferedImage terrainImage = tileSet.textures.get(TileConfiguration.tiles.get(getDisplayTerrain()).getTile(TileConfiguration.BITWISE_TYPES[terrainBitwise]).getID());
			BufferedImage furnitureImage = tileSet.textures.get(TileConfiguration.tiles.get(getDisplayFurniture()).getTile(TileConfiguration.BITWISE_TYPES[furnitureBitwise]).getID());

			BufferedImage tempImage = new BufferedImage(tileSet.tileSize, tileSet.tileSize, BufferedImage.TYPE_4BYTE_ABGR);
			tempImage.createGraphics().drawImage(terrainImage, terrainTransform, null);
			tempImage.createGraphics().drawImage(furnitureImage, furnitureTransform, null);

			return SwingFXUtils.toFXImage(tempImage, null);

		} else if (getDisplayTerrain() != null) {

			if (TileConfiguration.tiles.containsKey(getDisplayTerrain())) {

				if (!(TileConfiguration.get(getDisplayTerrain()).isMultiTile() || TileConfiguration.get(getDisplayTerrain()).rotates)) {
					terrainTransform = new AffineTransform();
				}

				BufferedImage terrainImage = tileSet.textures.get(TileConfiguration.get(getDisplayTerrain()).getTile(TileConfiguration.BITWISE_TYPES[terrainBitwise]).getID());
				BufferedImage tempImage = new BufferedImage(tileSet.tileSize, tileSet.tileSize, BufferedImage.TYPE_4BYTE_ABGR);
				tempImage.createGraphics().drawImage(terrainImage, terrainTransform, null);

				return SwingFXUtils.toFXImage(tempImage, null);

			}

		} else if (getDisplayFurniture() != null) {

			if (TileConfiguration.tiles.containsKey(getDisplayFurniture())) {

				if (!(TileConfiguration.get(getDisplayFurniture()).isMultiTile() || TileConfiguration.get(getDisplayFurniture()).rotates)) {
					furnitureTransform = new AffineTransform();
				}

				BufferedImage furnitureImage = tileSet.textures.get(TileConfiguration.get(getDisplayFurniture()).getTile(TileConfiguration.BITWISE_TYPES[furnitureBitwise]).getID());
				BufferedImage tempImage = new BufferedImage(tileSet.tileSize, tileSet.tileSize, BufferedImage.TYPE_4BYTE_ABGR);
				tempImage.createGraphics().drawImage(furnitureImage, furnitureTransform, null);
				return SwingFXUtils.toFXImage(tempImage, null);

			}

		}

		return SwingFXUtils.toFXImage(new BufferedImage(tileSet.tileSize, tileSet.tileSize, BufferedImage.TYPE_4BYTE_ABGR), null);

	}

	public MapTile copy() {

		MapTile tile = new MapTile();
		tileMappings.forEach(tileMapping -> tile.add(tileMapping.copy()));

		return tile;

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
