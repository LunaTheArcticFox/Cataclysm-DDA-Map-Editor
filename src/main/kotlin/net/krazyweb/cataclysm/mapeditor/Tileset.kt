package net.krazyweb.cataclysm.mapeditor

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.awt.image.BufferedImage
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.comparisons.compareBy

enum class TileConfiguration {
	CENTER, CORNER, EDGE, END_PIECE, T_CONNECTION, UNCONNECTED, BROKEN, OPEN
}

val BITWISE_TYPES = arrayOf(TileConfiguration.UNCONNECTED, TileConfiguration.END_PIECE, TileConfiguration.END_PIECE, TileConfiguration.CORNER, TileConfiguration.END_PIECE, TileConfiguration.EDGE, TileConfiguration.CORNER, TileConfiguration.T_CONNECTION, TileConfiguration.END_PIECE, TileConfiguration.CORNER, TileConfiguration.EDGE, TileConfiguration.T_CONNECTION, TileConfiguration.CORNER, TileConfiguration.T_CONNECTION, TileConfiguration.T_CONNECTION, TileConfiguration.CENTER)

data class Tileset(val width: Int, val height: Int, val isometric: Boolean = false, val pixelScale: Int = 1, val tiles: MutableMap<String, Tile> = sortedMapOf())
data class Tile(val id: String, val texture: BufferedImage, val rotates: Boolean = false, val multiTile: Boolean = false, val additionalTiles: Map<TileConfiguration, BufferedImage> = mutableMapOf())

fun loadTileset(path: Path) {

	val root = ObjectMapper().readTree(path.toFile())
	val tileInfo = root["tile_info"]

	var isometric = false
	if (tileInfo.has("iso")) {
		isometric = tileInfo["iso"].asBoolean()
	}

	var pixelScale = 0
	if (tileInfo.has("pixelscale")) {
		pixelScale = tileInfo["pixelscale"].asInt()
	}

	val tileset = Tileset(tileInfo["width"].asInt(), tileInfo["height"].asInt(), isometric, pixelScale)

	//TODO Regular "tiles" array
	root["tiles-new"].forEach {

		val spriteMap = ImageIO.read(path.resolve(it["file"].asText()).toFile())

		it["tiles"].forEach {

			val foreground = parseTextureIndexNode(it, "fg")
			val background = parseTextureIndexNode(it, "bg")

			val rotates = it.has("rotates") && it["rotates"].asBoolean()
			val multiTile = it.has("multitile") && it["multitile"].asBoolean()

			val tileImage = createTileImage(foreground, background, tileset.width, spriteMap)

			val additionalTiles: MutableMap<TileConfiguration, BufferedImage> = mutableMapOf()
			if (multiTile && it.has("additional_tiles")) {
				it["additional_tiles"].filter { isValidTileConfiguration(it["id"].asText()) }.forEach {
					val id = TileConfiguration.valueOf(it["id"].asText().toUpperCase())
					additionalTiles[id] = createTileImage(parseTextureIndexNode(it, "fg"), parseTextureIndexNode(it, "bg"), tileset.width, spriteMap)
				}
			}

			if (it["id"].isArray) {
				it["id"].forEach {
					tileset.tiles[it.asText()] = Tile(it.asText(), tileImage, rotates, multiTile, additionalTiles)
				}
			} else {
				tileset.tiles[it["id"].asText()] = Tile(it["id"].asText(), tileImage, rotates, multiTile, additionalTiles)
			}

		}

	}

}

private fun isValidTileConfiguration(id: String): Boolean {
	try {
		TileConfiguration.valueOf(id.toUpperCase())
		return true
	} catch (ignored: IllegalArgumentException) {
		return false
	}
}

private fun parseTextureIndexNode(node: JsonNode, type: String): Int {

	if (!node.has(type)) {
		return -1
	}

	if (node.isObject) {
		val sprite = node[type].sortedWith(compareBy { it["weight"].asInt() }).last()["sprite"]
		if (sprite.isArray) {
			return sprite.first().asInt()
		} else {
			return sprite.asInt()
		}
	} else if (node.isArray) {
		return node[type].first().asInt()
	} else {
		return node[type].asInt()
	}

}

private fun createTileImage(foreground: Int, background: Int, tileSize: Int, texture: BufferedImage): BufferedImage {

	val tempImage = BufferedImage(tileSize, tileSize, BufferedImage.TYPE_4BYTE_ABGR)

	if (background >= 0) {
		val x = background % 16
		val y = background / 16
		tempImage.graphics.drawImage(texture.getSubimage(x * tileSize, y * tileSize, tileSize, tileSize), 0, 0, null)
	}

	if (foreground >= 0) {
		val x = foreground % 16
		val y = foreground / 16
		tempImage.graphics.drawImage(texture.getSubimage(x * tileSize, y * tileSize, tileSize, tileSize), 0, 0, null)
	}

	return tempImage

}
