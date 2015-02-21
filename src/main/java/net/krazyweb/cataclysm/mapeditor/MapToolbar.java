package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import net.krazyweb.cataclysm.mapeditor.events.ToolSelectedEvent;
import net.krazyweb.cataclysm.mapeditor.map.MapManager;
import net.krazyweb.cataclysm.mapeditor.tools.*;

public class MapToolbar {

	private EventBus eventBus;
	private MapManager mapManager;

	@FXML
	private Button createPlaceGroupButton, movePlaceGroupButton;

	public void setEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}
	public void setMapManager(final MapManager mapManager) {
		this.mapManager = mapManager;
	}

	@FXML
	private void selectPencil() {
		eventBus.post(new ToolSelectedEvent(new PencilTool())); //TODO Get instances of tools instead of creating new ones?
		createPlaceGroupButton.setVisible(false);
		createPlaceGroupButton.setManaged(false);
		movePlaceGroupButton.setVisible(false);
		movePlaceGroupButton.setManaged(false);
	}

	@FXML
	private void selectLine() {
		eventBus.post(new ToolSelectedEvent(new LineTool()));
		createPlaceGroupButton.setVisible(false);
		createPlaceGroupButton.setManaged(false);
		movePlaceGroupButton.setVisible(false);
		movePlaceGroupButton.setManaged(false);
	}

	@FXML
	private void selectBox() {
		eventBus.post(new ToolSelectedEvent(new BoxTool()));
		createPlaceGroupButton.setVisible(false);
		createPlaceGroupButton.setManaged(false);
		movePlaceGroupButton.setVisible(false);
		movePlaceGroupButton.setManaged(false);
	}

	@FXML
	private void selectFill() {
		eventBus.post(new ToolSelectedEvent(new FillTool()));
		createPlaceGroupButton.setVisible(false);
		createPlaceGroupButton.setManaged(false);
		movePlaceGroupButton.setVisible(false);
		movePlaceGroupButton.setManaged(false);
	}

	@FXML
	private void selectEraser() {
		eventBus.post(new ToolSelectedEvent(new EraserTool()));
		createPlaceGroupButton.setVisible(false);
		createPlaceGroupButton.setManaged(false);
		movePlaceGroupButton.setVisible(false);
		movePlaceGroupButton.setManaged(false);
	}

	@FXML
	private void rotateMap() {
		mapManager.rotateMap();
	}

	@FXML
	private void selectPlaceGroup() {
		createPlaceGroupButton.setVisible(true);
		createPlaceGroupButton.setManaged(true);
		movePlaceGroupButton.setVisible(true);
		movePlaceGroupButton.setManaged(true);
	}

	@FXML
	private void selectCreatePlaceGroup() {
		eventBus.post(new ToolSelectedEvent(new CreatePlaceGroupTool()));
	}

	@FXML
	private void selectMovePlaceGroup() {
		eventBus.post(new ToolSelectedEvent(new EditPlaceGroupTool()));
	}

}
