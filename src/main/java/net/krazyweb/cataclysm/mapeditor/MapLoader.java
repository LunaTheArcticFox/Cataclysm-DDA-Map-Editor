package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.krazyweb.cataclysm.mapeditor.events.LoadMapEvent;
import net.krazyweb.cataclysm.mapeditor.events.MapLoadedEvent;
import net.krazyweb.cataclysm.mapeditor.map.MapgenDataFileReader;

public class MapLoader {

	private final EventBus eventBus;

	public MapLoader(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	public void loadMapEventListener(final LoadMapEvent event) {

		MapgenDataFileReader reader = new MapgenDataFileReader(event.getPath(), eventBus);

		reader.setOnSucceeded(value -> {
			eventBus.post(new MapLoadedEvent(reader.getMap()));
		});

		reader.start();

	}

}
