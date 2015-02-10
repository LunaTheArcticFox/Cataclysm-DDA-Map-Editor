package net.krazyweb.cataclysm.mapeditor.events;

import java.nio.file.Path;

public class RequestLoadMapEvent {

	private final Path map;

	public RequestLoadMapEvent(final Path map) {
		this.map = map;
	}

	public Path getPath() {
		return map;
	}

}
