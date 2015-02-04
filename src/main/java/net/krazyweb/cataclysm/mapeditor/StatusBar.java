package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import net.krazyweb.cataclysm.mapeditor.events.TileHoverEvent;

public class StatusBar {

	@FXML
	private Label mainTextArea;

	private EventBus eventBus;

	public void setEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	public void tileHoverEventListener(final TileHoverEvent event) {
		mainTextArea.setText(event.getX() + ", " + event.getY() + "\t\t" + event.getTileName());
	}

}
