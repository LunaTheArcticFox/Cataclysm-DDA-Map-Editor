package net.krazyweb.cataclysm.mapeditor.map;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.TileSet;
import net.krazyweb.cataclysm.mapeditor.map.tilemappings.FurnitureMapping;
import net.krazyweb.cataclysm.mapeditor.map.tilemappings.TerrainMapping;
import net.krazyweb.cataclysm.mapeditor.map.tilemappings.TileMapping;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MapTile {

	public List<TileMapping> tileMappings = new ArrayList<>();

	public String displayTerrain;
	public String displayFurniture;

	public MapTile() {

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
		if (displayTerrain == null || tile == null || tile.displayTerrain == null || Tile.get(displayTerrain) == null || Tile.get(tile.displayTerrain) == null) {
			return false;
		}
		if (Tile.get(displayTerrain).connectsToWalls && Tile.get(tile.displayTerrain).connectsToWalls) {
			return true;
		}
		return displayTerrain.equals(tile.displayTerrain);
	}

	public boolean furnitureConnectsTo(final MapTile tile) {
		return !(displayFurniture == null || tile == null || tile.displayFurniture == null) && displayFurniture.equals(tile.displayFurniture);
	}

	public Image getTexture(final int terrainBitwise, final int furnitureBitwise) {

		double terrainRotation = Math.toRadians(Tile.BITWISE_ROTATIONS[terrainBitwise]);
		double furnitureRotation = Math.toRadians(Tile.BITWISE_ROTATIONS[furnitureBitwise]);

		AffineTransform terrainTransform = new AffineTransform();
		terrainTransform.rotate(terrainRotation, TileSet.tileSize / 2, TileSet.tileSize / 2);

		AffineTransform furnitureTransform = new AffineTransform();
		furnitureTransform.rotate(furnitureRotation, TileSet.tileSize / 2, TileSet.tileSize / 2);

		//TODO Cache generated images instead of making new ones each call

		if (displayTerrain != null && displayFurniture != null) {

			if (!(Tile.get(displayTerrain).isMultiTile() || Tile.get(displayTerrain).rotates)) {
				terrainTransform = new AffineTransform();
			}

			if (!(Tile.get(displayFurniture).isMultiTile() || Tile.get(displayFurniture).rotates)) {
				furnitureTransform = new AffineTransform();
			}

			BufferedImage terrainImage = TileSet.textures.get(Tile.tiles.get(displayTerrain).getTile(Tile.BITWISE_TYPES[terrainBitwise]).getID());
			BufferedImage furnitureImage = TileSet.textures.get(Tile.tiles.get(displayFurniture).getTile(Tile.BITWISE_TYPES[furnitureBitwise]).getID());

			BufferedImage tempImage = new BufferedImage(TileSet.tileSize, TileSet.tileSize, BufferedImage.TYPE_4BYTE_ABGR);
			tempImage.createGraphics().drawImage(terrainImage, terrainTransform, null);
			tempImage.createGraphics().drawImage(furnitureImage, furnitureTransform, null);

			return SwingFXUtils.toFXImage(tempImage, null);

		} else if (displayTerrain != null) {

			if (Tile.tiles.containsKey(displayTerrain)) {

				if (!(Tile.get(displayTerrain).isMultiTile() || Tile.get(displayTerrain).rotates)) {
					terrainTransform = new AffineTransform();
				}

				BufferedImage terrainImage = TileSet.textures.get(Tile.get(displayTerrain).getTile(Tile.BITWISE_TYPES[terrainBitwise]).getID());
				BufferedImage tempImage = new BufferedImage(TileSet.tileSize, TileSet.tileSize, BufferedImage.TYPE_4BYTE_ABGR);
				tempImage.createGraphics().drawImage(terrainImage, terrainTransform, null);

				return SwingFXUtils.toFXImage(tempImage, null);

			}

		} else if (displayFurniture != null) {

			if (Tile.tiles.containsKey(displayFurniture)) {

				if (!(Tile.get(displayFurniture).isMultiTile() || Tile.get(displayFurniture).rotates)) {
					furnitureTransform = new AffineTransform();
				}

				BufferedImage furnitureImage = TileSet.textures.get(Tile.get(displayFurniture).getTile(Tile.BITWISE_TYPES[furnitureBitwise]).getID());
				BufferedImage tempImage = new BufferedImage(TileSet.tileSize, TileSet.tileSize, BufferedImage.TYPE_4BYTE_ABGR);
				tempImage.createGraphics().drawImage(furnitureImage, furnitureTransform, null);
				return SwingFXUtils.toFXImage(tempImage, null);

			}

		}

		return SwingFXUtils.toFXImage(new BufferedImage(TileSet.tileSize, TileSet.tileSize, BufferedImage.TYPE_4BYTE_ABGR), null);

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

}
