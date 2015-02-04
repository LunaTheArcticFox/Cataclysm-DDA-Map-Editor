package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import net.krazyweb.cataclysm.mapeditor.events.TilePickedEvent;
import net.krazyweb.cataclysm.mapeditor.events.TilesetLoadedEvent;

public class TilePicker {

	@FXML
	private TilePane tileContainer;

	private EventBus eventBus;

	public void setEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	public void tilesetLoadedListener(final TilesetLoadedEvent event) {

		tileContainer.getChildren().clear();

		Tile.tiles.entrySet().forEach(entry -> {
			ImageView view = new ImageView(TileSet.textures.get(entry.getValue().getForeground()));
			view.setOnMousePressed(mouseEvent -> {
				eventBus.post(new TilePickedEvent(entry.getValue()));
			});
			tileContainer.getChildren().add(view);
		});

	}

}
