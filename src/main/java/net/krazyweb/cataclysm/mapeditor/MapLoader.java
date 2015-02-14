package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.krazyweb.cataclysm.mapeditor.events.MapLoadedEvent;
import net.krazyweb.cataclysm.mapeditor.events.RequestLoadMapEvent;
import net.krazyweb.cataclysm.mapeditor.map.MapDataFileReader;

import java.nio.file.Path;

public class MapLoader {

	private final EventBus eventBus;

	public MapLoader(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	public void loadMapEventListener(final RequestLoadMapEvent event) {
		loadMap(event.getPath());
	}

	private void loadMap(final Path path) {

		MapDataFileReader reader = new MapDataFileReader(path, eventBus);

		reader.setOnSucceeded(value -> eventBus.post(new MapLoadedEvent(reader.getMap())));

		reader.start();

	}

}
