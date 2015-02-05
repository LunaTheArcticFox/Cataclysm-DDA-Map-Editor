package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;
import net.krazyweb.cataclysm.mapeditor.events.RotateMapEvent;
import net.krazyweb.cataclysm.mapeditor.events.ToolSelectedEvent;
import net.krazyweb.cataclysm.mapeditor.tools.EraserTool;
import net.krazyweb.cataclysm.mapeditor.tools.LineTool;
import net.krazyweb.cataclysm.mapeditor.tools.PencilTool;

public class MapToolbar {

	private EventBus eventBus;

	public void setEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@FXML
	private void selectPencil() {
		eventBus.post(new ToolSelectedEvent(new PencilTool())); //TODO Get instances of tools instead of creating new ones?
	}

	@FXML
	private void selectLine() {
		eventBus.post(new ToolSelectedEvent(new LineTool()));
	}

	@FXML
	private void selectEraser() {
		eventBus.post(new ToolSelectedEvent(new EraserTool()));
	}

	@FXML
	private void rotateMap() {
		eventBus.post(new RotateMapEvent());
	}

}
