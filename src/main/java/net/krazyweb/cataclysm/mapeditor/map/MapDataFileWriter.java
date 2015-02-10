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
					//A slightly hackish way of getting the right symbol for the tile TODO fix this?
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
				if (id.furniture != null && !id.furniture.equals("f_null")) {
					generator.writeStringField(id.symbol + "", id.furniture);
				}
			}
			generator.writeEndObject();

			generator.writeArrayFieldStart("place_specials");
			for (int x = 0; x < CataclysmMap.SIZE; x++) {
				for (int y = 0; y < CataclysmMap.SIZE; y++) {
					if (map.currentState.furniture[x][y].equals("f_toilet")) {
						generator.writeStartObject();
						generator.writeStringField("type", "toilet");
						generator.writeNumberField("x", x);
						generator.writeNumberField("y", y);
						generator.writeEndObject();
					}
				}
			}
			generator.writeEndArray();

			generator.writeArrayFieldStart("set");
			createRandomGrass(generator);
			generator.writeEndArray();

			if (!map.currentState.placeGroupZones.isEmpty()) {
				generator.writeArrayFieldStart("place_groups");
				for (PlaceGroupZone placeGroupZone : map.currentState.placeGroupZones) {
					generator.writeStartObject();
					generator.writeStringField(placeGroupZone.group.type, placeGroupZone.group.group);
					generator.writeNumberField("chance", placeGroupZone.group.chance);
					if (placeGroupZone.w != 1) {
						generator.writeArrayFieldStart("x");
						generator.writeNumber(placeGroupZone.x);
						generator.writeNumber(placeGroupZone.x - 1 + placeGroupZone.w);
						generator.writeEndArray();
					} else {
						generator.writeNumberField("x", placeGroupZone.x);
					}
					if (placeGroupZone.h != 1) {
						generator.writeArrayFieldStart("y");
						generator.writeNumber(placeGroupZone.y);
						generator.writeNumber(placeGroupZone.y - 1 + placeGroupZone.h);
						generator.writeEndArray();
					} else {
						generator.writeNumberField("y", placeGroupZone.y);
					}
					generator.writeEndObject();
				}
				generator.writeEndArray();
			}

			generator.writeEndObject();

			generator.writeEndObject();
			generator.writeEndArray();

			generator.close();

			map.path = path;
			map.saved = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void createRandomGrass(final JsonGenerator generator) throws IOException {

		boolean[][] grassArray = new boolean[CataclysmMap.SIZE][CataclysmMap.SIZE];

		for (int x = 0; x < CataclysmMap.SIZE; x++) {
			for (int y = 0; y < CataclysmMap.SIZE; y++) {
				grassArray[x][y] = map.getTerrainAt(x, y).equals("t_grass");
			}
		}

		List<Rectangle> grassRectangles = new ArrayList<>();

		for (int x = 0; x < CataclysmMap.SIZE; x++) {
			for (int y = 0; y < CataclysmMap.SIZE; y++) {

				if (!grassArray[x][y]) {
					continue;
				}

				int y2 = getGrassY2(x, y, grassArray);
				int x2 = getGrassX2(x, y, y2, grassArray);

				Rectangle r = new Rectangle();
				r.x1 = x;
				r.y1 = y;
				r.x2 = x2;
				r.y2 = y2;

				for (int ix = r.x1; ix <= r.x2; ix++) {
					for (int iy = r.y1; iy <= r.y2; iy++) {
						grassArray[ix][iy] = false;
					}
				}

				grassRectangles.add(r);

			}
		}

		for (Rectangle r : grassRectangles) {

			generator.writeStartObject();
			generator.writeStringField("point", "terrain");
			generator.writeStringField("id", "t_dirt");

			if (r.x1 == r.x2) {
				generator.writeNumberField("x", r.x1);
			} else {
				generator.writeArrayFieldStart("x");
				generator.writeNumber(r.x1);
				generator.writeNumber(r.x2);
				generator.writeEndArray();
			}

			if (r.y1 == r.y2) {
				generator.writeNumberField("y", r.y1);
			} else {
				generator.writeArrayFieldStart("y");
				generator.writeNumber(r.y1);
				generator.writeNumber(r.y2);
				generator.writeEndArray();
			}

			int area = (r.x2 - r.x1 + 1) * (r.y2 - r.y1 + 1);
			int repeatMin = Math.max(Math.min((int) (area / 3.5) - 1, 8), 0);
			int repeatMax = Math.min(Math.max((int) (area / 2.5) - 1, 1), 14);

			generator.writeArrayFieldStart("repeat");
			generator.writeNumber(repeatMin);
			generator.writeNumber(repeatMax);
			generator.writeEndArray();

			generator.writeEndObject();
		}

	}

	private int getGrassY2(final int x, final int y, final boolean[][] grassArray) {
		int y2 = y;
		for (int iy = y; iy < CataclysmMap.SIZE; iy++) {
			if (grassArray[x][iy]) {
				y2 = iy;
			} else {
				break;
			}
		}
		return y2;
	}

	private int getGrassX2(final int x, final int y, final int y2, final boolean[][] grassArray) {
		int x2 = x;
		for (int ix = x; ix < CataclysmMap.SIZE; ix++) {
			boolean nonGrassFound = false;
			for (int iy = y; iy <= y2; iy++) {
				if (!grassArray[ix][iy]) {
					nonGrassFound = true;
				}
			}
			if (!nonGrassFound) {
				x2 = ix;
			} else {
				break;
			}
		}
		return x2;
	}

	private static class Rectangle {
		private int x1, y1, x2, y2;
		@Override
		public String toString() {
			return "[" + x1 + ", " + y1 + ", " + x2 + ", " + y2 + "]";
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
