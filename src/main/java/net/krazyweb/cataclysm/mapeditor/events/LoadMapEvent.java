package net.krazyweb.cataclysm.mapeditor.events;

import java.nio.file.Path;

public class LoadMapEvent {

	private final Path map;

	public LoadMapEvent(final Path map) {
		this.map = map;
	}

	public Path getPath() {
		return map;
	}

}
