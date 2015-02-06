package net.krazyweb.cataclysm.mapeditor;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import net.krazyweb.cataclysm.mapeditor.events.RotateMapEvent;
import net.krazyweb.cataclysm.mapeditor.events.ToolSelectedEvent;
import net.krazyweb.cataclysm.mapeditor.tools.*;

public class MapToolbar {

	private EventBus eventBus;

	@FXML
	private Button createPlaceGroupButton, movePlaceGroupButton;

	public void setEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
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
		eventBus.post(new RotateMapEvent());
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
		eventBus.post(new ToolSelectedEvent(new MovePlaceGroupTool()));
	}

}
