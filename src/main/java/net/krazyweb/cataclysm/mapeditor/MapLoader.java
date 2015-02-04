package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.krazyweb.cataclysm.mapeditor.events.LoadMapEvent;
import net.krazyweb.cataclysm.mapeditor.events.MapLoadedEvent;

import java.nio.file.Paths;

public class MapLoader {

	private final EventBus eventBus;

	public MapLoader(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	public void loadMapEventListener(final LoadMapEvent event) {

		MapgenDataFileReader reader = new MapgenDataFileReader(Paths.get("Sample Data").resolve("house05.json"));
		reader.start();

		reader.setOnSucceeded(value -> {
			eventBus.post(new MapLoadedEvent(reader.getMap()));
		});

	}

}
