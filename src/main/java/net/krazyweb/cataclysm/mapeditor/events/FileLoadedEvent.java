package net.krazyweb.cataclysm.mapeditor.events;

import java.nio.file.Path;

public class FileLoadedEvent {

	private Path path;

	public FileLoadedEvent(final Path path) {
		this.path = path;
	}

	public Path getPath() {
		return path;
	}

}
