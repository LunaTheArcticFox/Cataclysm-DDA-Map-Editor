package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import net.krazyweb.cataclysm.mapeditor.events.FileSavedEvent;
import net.krazyweb.cataclysm.mapeditor.events.TileHoverEvent;
import net.krazyweb.cataclysm.mapeditor.events.ZoomChangeEvent;
import org.controlsfx.control.StatusBar;

public class StatusBarController {

	@FXML
	private StatusBar statusBar;

	@FXML
	private Slider zoomSlider;

	private EventBus eventBus;

	public void setEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Subscribe
	public void tileHoverEventListener(final TileHoverEvent event) {
		statusBar.setText(event.getX() + ", " + event.getY() + "\t\t" + event.getTileName());
	}

	@Subscribe
	public void mapSavedEventListener(final FileSavedEvent event) {
		//TODO statusBar.setText("Saved map to '" + event.getMap().getPath() + "'");
	}

	@FXML
	private void zoom() {
		eventBus.post(new ZoomChangeEvent(zoomSlider.getValue()));
	}

}
