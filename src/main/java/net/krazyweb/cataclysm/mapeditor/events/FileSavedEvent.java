package net.krazyweb.cataclysm.mapeditor.events;

import java.nio.file.Path;

public class FileSavedEvent {

	private Path path;

	public FileSavedEvent(final Path path) {
		this.path = path;
	}

	public Path getPath() {
		return path;
	}

}
