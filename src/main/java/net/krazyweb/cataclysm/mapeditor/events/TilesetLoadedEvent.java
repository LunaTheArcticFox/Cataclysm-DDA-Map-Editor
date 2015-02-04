package net.krazyweb.cataclysm.mapeditor.events;

import java.nio.file.Path;

public class TilesetLoadedEvent {

	private final Path tileset;

	public TilesetLoadedEvent(final Path tileset) {
		this.tileset = tileset;
	}

	public Path getPath() {
		return tileset;
	}

}
