package net.krazyweb.cataclysm.mapeditor.events;

import java.nio.file.Path;

public class RequestSaveMapEvent {

	private final Path path;

	public RequestSaveMapEvent(final Path path) {
		this.path = path;
	}

	public Path getPath() {
		return path;
	}

}
