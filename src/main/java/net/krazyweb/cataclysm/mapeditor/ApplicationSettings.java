package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.Subscribe;
import net.krazyweb.cataclysm.mapeditor.events.ShowGroupsEvent;

public class ApplicationSettings {

	private static ApplicationSettings instance;

	private boolean showGroups = true;

	private ApplicationSettings() {

	}

	public static ApplicationSettings getInstance() {
		if (instance == null) {
			synchronized (ApplicationSettings.class) {
				instance = new ApplicationSettings();
			}
		}
		return instance;
	}

	public boolean showGroups() {
		return showGroups;
	}

	@Subscribe
	public void showGroupsEventListener(final ShowGroupsEvent event) {
		showGroups = event.showGroups();
		//TODO Save
	}

}
