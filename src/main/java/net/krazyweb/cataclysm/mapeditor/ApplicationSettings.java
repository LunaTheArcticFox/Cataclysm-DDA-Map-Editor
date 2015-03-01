package net.krazyweb.cataclysm.mapeditor;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.Subscribe;
import net.krazyweb.cataclysm.mapeditor.events.ShowGridEvent;
import net.krazyweb.cataclysm.mapeditor.events.ShowGroupsEvent;
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

	private static final Path path = Paths.get("preferences.json");

	private static ApplicationSettings instance;

	public static enum Preference {
		SHOW_GRID,
		SHOW_GROUPS
	}

	private Map<Preference, Object> preferences = new HashMap<>();

	private ApplicationSettings() {
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
			throw new InvalidTypeException(preference.name() + " is not a Boolean value.");
		}
		return (boolean) preferences.get(preference);
	}

	private void load() throws IOException {

		if (!Files.exists(path)) {
			Files.createFile(path);
			FileWriter writer = new FileWriter(path.toFile());
			writer.append("{}");
			writer.close();
		}

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(path.toFile());

		root.fields().forEachRemaining(preference -> {
			if (preference.getValue().isBoolean()) {
				preferences.put(Preference.valueOf(preference.getKey()), preference.getValue().asBoolean());
			}
		});

	}

	public void save() {

		try {

			if (!Files.exists(path)) {
				Files.createFile(path);
			}

			JsonGenerator generator = new ObjectMapper().getFactory().createGenerator(path.toFile(), JsonEncoding.UTF8);
			generator.useDefaultPrettyPrinter();
			generator.writeStartObject();

			for (Map.Entry<Preference, Object> preference : preferences.entrySet()) {
				if (preference.getValue() instanceof Boolean) {
					generator.writeBooleanField(preference.getKey().name(), (Boolean) preference.getValue());
				}
			}

			generator.writeEndObject();

			generator.close();

		} catch (IOException e) {
			log.error("Error while saving ApplicationSettings:", e);
		}

	}

	@Subscribe
	public void showGroupsEventListener(final ShowGroupsEvent event) {
		preferences.put(Preference.SHOW_GROUPS, event.showGroups());
		//TODO Save
	}

	@Subscribe
	public void showGridEventListener(final ShowGridEvent event) {
		preferences.put(Preference.SHOW_GRID, event.showGrid());
		//TODO Save
	}

}
