package net.krazyweb.cataclysm.mapeditor.events;

import java.nio.file.Path;

public class LoadTilesetEvent {

	private final Path tileset;

	public LoadTilesetEvent(final Path tileset) {
		this.tileset = tileset;
	}

	public Path getPath() {
		return tileset;
	}

}
