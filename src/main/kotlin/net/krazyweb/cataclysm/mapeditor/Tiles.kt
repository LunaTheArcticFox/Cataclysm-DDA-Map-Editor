package net.krazyweb.cataclysm.mapeditor

import com.fasterxml.jackson.databind.ObjectMapper
import net.krazyweb.util.FileUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.Path
import java.util.*

val log: Logger = LogManager.getLogger("Tiles")
val terrainDefs: SortedMap<String, TerrainDef> = sortedMapOf()

data class TerrainDef(val id: String, val name: String, val connectsToWalls: Boolean)

fun loadTiles(gameFolder: Path) {

	val files = FileUtils.listFiles(gameFolder.resolve("data").resolve("json")).filter { it.fileName.toString().endsWith(".json") }

	files.forEach {

		log.debug("Loading terrain definitions from: $it")

		val root = ObjectMapper().readTree(it.toFile())

		root.filter { it.has("type") }.filter { it.get("type").asText() == "terrain" || it.get("type").asText() == "furniture" }.forEach {

			val id = it["id"].asText()
			var connectsToWalls = false

			if (it.has("flags")) {
				connectsToWalls = it["flags"].any({ flag ->
					val parsedFlag = flag.asText().replace("\"".toRegex(), "")
					parsedFlag == "CONNECT_TO_WALL" || parsedFlag == "WALL"
				})
			}

			terrainDefs[id] = TerrainDef(id, it["name"].asText(), connectsToWalls)
			log.debug("Loaded tile '$id'")

		}

	}

}

