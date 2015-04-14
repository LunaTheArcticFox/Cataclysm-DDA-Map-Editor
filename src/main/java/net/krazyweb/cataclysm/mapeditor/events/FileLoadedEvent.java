package net.krazyweb.cataclysm.mapeditor.events;

import net.krazyweb.cataclysm.mapeditor.map.data.MapgenEntry;

import java.nio.file.Path;
import java.util.List;

public class FileLoadedEvent {

	private Path path;
	private List<MapgenEntry> maps;

	public FileLoadedEvent(final Path path, final List<MapgenEntry> maps) {
		this.path = path;
		this.maps = maps;
	}

	public Path getPath() {
		return path;
	}

	public List<MapgenEntry> getMaps() {
		return maps;
	}

}
