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
			/*MapgenMap map = reader.getMap();

			GraphicsContext graphics2D = canvas.getGraphicsContext2D();

			for (int x = 0; x < 24; x++) {
				for (int y = 0; y < 24; y++) {
					if (map.terrain[x][y] == 0) {
						graphics2D.setFill(Color.CHOCOLATE);
					} else {
						graphics2D.setFill(Color.CADETBLUE);
					}
					graphics2D.fillRect(x * 32, y * 32, 32, 32);
				}
			}*/

		});

	}

}
