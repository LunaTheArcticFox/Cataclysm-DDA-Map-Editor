package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.Subscribe;
import net.krazyweb.cataclysm.mapeditor.events.ShowGroupsEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ApplicationSettings {

	public static class InvalidTypeException extends RuntimeException {
		public InvalidTypeException(final String message) {
			super(message);
		}
	}

	private static ApplicationSettings instance;

	public static enum Preference {
		SHOW_GRID,
		SHOW_GROUPS
	}

	private Map<Preference, Object> preferences = new HashMap<>();

	private ApplicationSettings() {
		preferences.put(Preference.SHOW_GRID, false);
		preferences.put(Preference.SHOW_GROUPS, true);
	}

	public static ApplicationSettings getInstance() {
		if (instance == null) {
			synchronized (ApplicationSettings.class) {
				instance = new ApplicationSettings();
			}
		}
		return instance;
	}

	public Optional<Boolean> getBoolean(final Preference preference) {
		if (!(preferences.get(preference) instanceof Boolean)) {
			throw new InvalidTypeException(preference.name() + " is not a Boolean value.");
		}
		return Optional.of((Boolean) preferences.get(preference));
	}

	@Subscribe
	public void showGroupsEventListener(final ShowGroupsEvent event) {
		preferences.put(Preference.SHOW_GROUPS, event.showGroups());
		//TODO Save
	}

}
