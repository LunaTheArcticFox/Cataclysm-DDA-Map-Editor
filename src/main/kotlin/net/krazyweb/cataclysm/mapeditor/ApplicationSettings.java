package net.krazyweb.cataclysm.mapeditor;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ApplicationSettings {

	private static final Logger log = LogManager.getLogger(ApplicationSettings.class);

	public static class InvalidTypeException extends RuntimeException {
		public InvalidTypeException(final String message) {
			super(message);
		}
	}

	private static final Path PATH = Paths.get("preferences.json");

	public static final Path DEFAULT_NEW_FILE = Paths.get("data", "default.json");

	private static ApplicationSettings instance;

	//public static TileSet currentTileset;

	public enum Preference {

		LAST_FOLDER(Path.class),
		SHOW_GRID(Boolean.class),
		SHOW_GROUPS(Boolean.class),
		GAME_FOLDER(Path.class);

		private final Class<?> clazz;

		Preference(Class<?> clazz) {
			this.clazz = clazz;
		}

	}

	private Map<Preference, Object> preferences = new HashMap<>();

	private ApplicationSettings() {
		preferences.put(Preference.LAST_FOLDER, Paths.get(""));
		preferences.put(Preference.SHOW_GRID, false);
		preferences.put(Preference.SHOW_GROUPS, true);
		try {
			load();
		} catch (IOException e) {
			log.error("Error while loading ApplicationSettings:", e);
		}
	}

	public static ApplicationSettings getInstance() {
		if (instance == null) {
			synchronized (ApplicationSettings.class) {
				instance = new ApplicationSettings();
			}
		}
		return instance;
	}

	public boolean getBoolean(final Preference preference) {
		if (!(preferences.get(preference) instanceof Boolean)) {
			throw new InvalidTypeException(preference.name() + " is not a Boolean object.");
		}
		return (boolean) preferences.get(preference);
	}

	public Path getPath(final Preference preference) {
		if (!(preference.clazz.isAssignableFrom(Path.class))) {
			throw new InvalidTypeException(preference.name() + " is not a Path object.");
		}
		return (Path) preferences.get(preference);
	}

	public void setPath(final Preference preference, Path value) {
		if (!(preference.clazz.isAssignableFrom(Path.class))) {
			throw new InvalidTypeException(preference.name() + " is not a Path object.");
		}

		if (value != null) {
			preferences.put(preference, value);
		} else {
			preferences.remove(preference);
		}
	}

	private void load() throws IOException {

		if (!Files.exists(PATH)) {
			Files.createFile(PATH);
			FileWriter writer = new FileWriter(PATH.toFile());
			writer.append("{}");
			writer.close();
		}

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(PATH.toFile());

		root.fields().forEachRemaining(preference -> {
			if (preference.getValue().isBoolean()) {
				preferences.put(Preference.valueOf(preference.getKey()), preference.getValue().asBoolean());
			} else if (Preference.valueOf(preference.getKey()).clazz == Path.class) {
				preferences.put(Preference.valueOf(preference.getKey()), Paths.get(preference.getValue().asText()));
			}
		});

	}

	public void save() {

		try {

			if (!Files.exists(PATH)) {
				Files.createFile(PATH);
			}

			JsonGenerator generator = new ObjectMapper().getFactory().createGenerator(PATH.toFile(), JsonEncoding.UTF8);
			generator.useDefaultPrettyPrinter();
			generator.writeStartObject();

			for (Map.Entry<Preference, Object> preference : preferences.entrySet()) {
				if (preference.getValue() instanceof Boolean) {
					generator.writeBooleanField(preference.getKey().name(), (Boolean) preference.getValue());
				} else if (preference.getValue() instanceof Path) {
					generator.writeStringField(preference.getKey().name(), ((Path) preference.getValue()).toAbsolutePath().toString());
				}
			}

			generator.writeEndObject();

			generator.close();

		} catch (IOException e) {
			log.error("Error while saving ApplicationSettings:", e);
		}

	}

	/*@Subscribe
	public void fileSavedEventListener(final FileSavedEvent event) {
		try {
			if (!Files.isSameFile(event.getPath(), DEFAULT_NEW_FILE)) {
				preferences.put(Preference.LAST_FOLDER, event.getPath().getParent());
			}
		} catch (IOException e) {
			log.error("Error while determining if FileLoadedEvent file is the same as the default file.", e);
		}
	}

	@Subscribe
	public void fileLoadedEventListener(final FileLoadedEvent event) {
		try {
			if (!Files.isSameFile(event.getPath(), DEFAULT_NEW_FILE)) {
				preferences.put(Preference.LAST_FOLDER, event.getPath().getParent());
			}
		} catch (IOException e) {
			log.error("Error while determining if FileLoadedEvent file is the same as the default file.", e);
		}
	}

	@Subscribe
	public void showGroupsEventListener(final ShowGroupsEvent event) {
		preferences.put(Preference.SHOW_GROUPS, event.showGroups());
	}

	@Subscribe
	public void showGridEventListener(final ShowGridEvent event) {
		preferences.put(Preference.SHOW_GRID, event.showGrid());
	}

	@Subscribe
	public void tileSetLoadedEventListener(final TilesetLoadedEvent event) {
		currentTileset = event.getTileSet();
	}*/

}
