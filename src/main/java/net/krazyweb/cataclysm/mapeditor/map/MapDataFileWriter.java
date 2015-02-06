package net.krazyweb.cataclysm.mapeditor.map;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.eventbus.EventBus;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MapDataFileWriter extends Service<Boolean> {

	private static final char[] SYMBOLS = new char[] {
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '!', '@', '#', '$', '%', '^', '&', '*', '*', '(', ')'
	};

	private Path path;

	private CataclysmMap map;
	private EventBus eventBus;

	public MapDataFileWriter(final Path path, final CataclysmMap map, final EventBus eventBus) {
		this.path = path;
		this.map = map;
		this.eventBus = eventBus;
	}

	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				save();
				return Boolean.TRUE;
			}
		};
	}

	public CataclysmMap getMap() {
		return map;
	}

	private class Value<T> {
		T value;
	}

	private void save() throws IOException {

		try {

			//ObjectMapper objectMapper = new ObjectMapper();
			JsonGenerator generator = new JsonFactory().createGenerator(path.toFile(), JsonEncoding.UTF8);
			generator.useDefaultPrettyPrinter();

			//TODO support multiple maps per file
			generator.writeStartArray();
			generator.writeStartObject();

			generator.writeStringField("type", "mapgen");

			generator.writeArrayFieldStart("om_terrain");
			generator.writeString("house");
			generator.writeEndArray();

			generator.writeStringField("method", "json");
			generator.writeNumberField("weight", 300);

			generator.writeObjectFieldStart("object");

			generator.writeArrayFieldStart("rows");
			List<TerrainIdentifier> terrainIDs = getTerrainIDs();
			for (int y = 0; y < CataclysmMap.SIZE; y++) {
				String row = "";
				for (int x = 0; x < CataclysmMap.SIZE; x++) {
					TerrainIdentifier id = new TerrainIdentifier();
					id.terrain = map.currentState.terrain[x][y];
					id.furniture = map.currentState.furniture[x][y];
					row += terrainIDs.get(terrainIDs.indexOf(id)).symbol;
				}
				generator.writeString(row);
			}
			generator.writeEndArray();

			generator.writeObjectFieldStart("terrain");
			for (TerrainIdentifier id : terrainIDs) {
				if (id.terrain != null) {
					generator.writeStringField(id.symbol + "", id.terrain);
				}
			}
			generator.writeEndObject();

			generator.writeObjectFieldStart("furniture");
			for (TerrainIdentifier id : terrainIDs) {
				if (id.furniture != null) {
					generator.writeStringField(id.symbol + "", id.furniture);
				}
			}
			generator.writeEndObject();

			if (!map.currentState.placeGroupZones.isEmpty()) {
				generator.writeArrayFieldStart("place_groups");
				for (PlaceGroupZone placeGroupZone : map.currentState.placeGroupZones) {
					generator.writeStartObject();
					generator.writeStringField(placeGroupZone.group.type, placeGroupZone.group.name);
					generator.writeNumberField("chance", placeGroupZone.chance);
					generator.writeArrayFieldStart("x");
					generator.writeNumber(placeGroupZone.x);
					generator.writeNumber(placeGroupZone.x - 1 + placeGroupZone.w);
					generator.writeEndArray();
					generator.writeArrayFieldStart("y");
					generator.writeNumber(placeGroupZone.y);
					generator.writeNumber(placeGroupZone.y - 1 + placeGroupZone.h);
					generator.writeEndArray();
					generator.writeEndObject();
				}
				generator.writeEndArray();
			}

			generator.writeEndObject();

			generator.writeEndObject();
			generator.writeEndArray();

			generator.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static class TerrainIdentifier {

		private char symbol;
		private String terrain;
		private String furniture;

		@Override
		public boolean equals(Object object) {

			if (this == object) return true;
			if (object == null || getClass() != object.getClass()) return false;

			TerrainIdentifier other = (TerrainIdentifier) object;

			if (furniture != null ? !furniture.equals(other.furniture) : other.furniture != null) return false;
			if (terrain != null ? !terrain.equals(other.terrain) : other.terrain != null) return false;

			return true;

		}

		@Override
		public int hashCode() {
			int result = terrain != null ? terrain.hashCode() : 0;
			result = 31 * result + (furniture != null ? furniture.hashCode() : 0);
			return result;
		}

	}

	private List<TerrainIdentifier> getTerrainIDs() {

		List<TerrainIdentifier> currentIDs = new ArrayList<>();

		for (int x = 0; x < CataclysmMap.SIZE; x++) {
			for (int y = 0; y < CataclysmMap.SIZE; y++) {
				TerrainIdentifier id = new TerrainIdentifier();
				id.terrain = map.currentState.terrain[x][y];
				id.furniture = map.currentState.furniture[x][y];
				if (!currentIDs.contains(id)) {
					currentIDs.add(id);
				}
			}
		};

		for (int i = 0; i < currentIDs.size(); i++) {
			currentIDs.get(i).symbol = SYMBOLS[i];
		}

		return currentIDs;

	}

}
