package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import net.krazyweb.cataclysm.mapeditor.events.MapSavedEvent;
import net.krazyweb.cataclysm.mapeditor.events.TileHoverEvent;
import org.controlsfx.control.StatusBar;

public class StatusBarController {

	@FXML
	private StatusBar statusBar;

	private EventBus eventBus;

	public void setEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	public void tileHoverEventListener(final TileHoverEvent event) {
		statusBar.setText(event.getX() + ", " + event.getY() + "\t\t" + event.getTileName());
	}

	@Subscribe
	public void mapSavedEventListener(final MapSavedEvent event) {
		statusBar.setText("File Saved Successfully");
	}

}
