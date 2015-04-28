package net.krazyweb.cataclysm.mapeditor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.krazyweb.util.FileUtils;

import java.io.IOException;
import java.nio.file.Path;

public class CataclysmDefinitions {

	public static ObservableList<String> fields = FXCollections.observableArrayList();
	public static ObservableList<String> furniture = FXCollections.observableArrayList();
	public static ObservableList<String> items = FXCollections.observableArrayList();
	public static ObservableList<String> itemGroups = FXCollections.observableArrayList();
	public static ObservableList<String> monsters = FXCollections.observableArrayList();
	public static ObservableList<String> monsterGroups = FXCollections.observableArrayList();
	public static ObservableList<String> npcs = FXCollections.observableArrayList();
	public static ObservableList<String> terrain = FXCollections.observableArrayList();
	public static ObservableList<String> traps = FXCollections.observableArrayList();
	public static ObservableList<String> vehicles = FXCollections.observableArrayList();

	public static void load() {

		Path gameFolder = ApplicationSettings.getInstance().getPath(ApplicationSettings.Preference.GAME_FOLDER);
		Path dataFolder = gameFolder.resolve("data").resolve("json");

		try {
			FileUtils.listFiles(dataFolder).forEach(jsonFile -> {

				if (jsonFile.getFileName().toString().endsWith(".json")) {

					try {

						JsonNode root = new ObjectMapper().readTree(jsonFile.toFile());

						root.forEach(node -> {
							switch (node.get("type").asText()) {
								case "furniture":
									parseFurniture(node);
									break;
								case "AMMO":
								case "ARMOR":
								case "BIONIC_ITEM":
								case "BOOK":
								case "COMESTIBLE":
								case "CONTAINER":
								case "GENERIC":
								case "GUN":
								case "GUNMOD":
								case "TOOL":
								case "TOOL_ARMOR":
									parseItem(node);
									break;
								case "item_group":
									parseItemGroup(node);
									break;
								case "MONSTER":
									parseMonster(node);
									break;
								case "monstergroup":
									parseMonsterGroup(node);
									break;
								case "npc":
									parseNPC(node);
									break;
								case "terrain":
									parseTerrain(node);
									break;
								case "trap":
									parseTrap(node);
									break;
								case "vehicle":
									parseVehicle(node);
									break;
							}
						});

					} catch (IOException e) {
						e.printStackTrace();
					}

				}

			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		fields.add("fd_null");
		fields.add("fd_blood");
		fields.add("fd_bile");
		fields.add("fd_gibs_flesh");
		fields.add("fd_gibs_veggy");
		fields.add("fd_web");
		fields.add("fd_slime");
		fields.add("fd_acid");
		fields.add("fd_sap");
		fields.add("fd_sludge");
		fields.add("fd_fire");
		fields.add("fd_rubble");
		fields.add("fd_smoke");
		fields.add("fd_toxic_gas");
		fields.add("fd_tear_gas");
		fields.add("fd_nuke_gas");
		fields.add("fd_gas_vent");
		fields.add("fd_fire_vent");
		fields.add("fd_flame_burst");
		fields.add("fd_electricity");
		fields.add("fd_fatigue");
		fields.add("fd_push_items");
		fields.add("fd_shock_vent");
		fields.add("fd_acid_vent");
		fields.add("fd_plasma");
		fields.add("fd_laser");
		fields.add("fd_spotlight");
		fields.add("fd_dazzling");
		fields.add("fd_blood_veggy");
		fields.add("fd_blood_insect");
		fields.add("fd_blood_invertebrate");
		fields.add("fd_gibs_insect");
		fields.add("fd_gibs_invertebrate");
		fields.add("fd_cigsmoke");
		fields.add("fd_weedsmoke");
		fields.add("fd_cracksmoke");
		fields.add("fd_methsmoke");
		fields.add("fd_bees");
		fields.add("fd_incendiary");
		fields.add("fd_relax_gas");
		fields.add("fd_fungal_haze");
		fields.add("fd_hot_air1");
		fields.add("fd_hot_air2");
		fields.add("fd_hot_air3");
		fields.add("fd_hot_air4");

	}

	private static void parseFurniture(final JsonNode node) {
		furniture.add(node.get("id").asText());
	}

	private static void parseItem(final JsonNode node) {
		items.add(node.get("id").asText());
	}

	private static void parseItemGroup(final JsonNode node) {
		itemGroups.add(node.get("id").asText());
	}

	private static void parseMonster(final JsonNode node) {
		monsters.add(node.get("id").asText());
	}

	private static void parseMonsterGroup(final JsonNode node) {
		monsterGroups.add(node.get("name").asText());
	}

	private static void parseNPC(final JsonNode node) {
		npcs.add(node.get("id").asText());
	}

	private static void parseTerrain(final JsonNode node) {
		terrain.add(node.get("id").asText());
	}

	private static void parseTrap(final JsonNode node) {
		traps.add(node.get("id").asText());
	}

	private static void parseVehicle(final JsonNode node) {
		vehicles.add(node.get("id").asText());
	}

}
