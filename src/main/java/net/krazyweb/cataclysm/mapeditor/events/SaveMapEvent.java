package net.krazyweb.cataclysm.mapeditor.events;

import java.nio.file.Path;

public class SaveMapEvent {

	private final Path path;

	public SaveMapEvent(final Path path) {
		this.path = path;
	}

	public Path getPath() {
		return path;
	}

}
