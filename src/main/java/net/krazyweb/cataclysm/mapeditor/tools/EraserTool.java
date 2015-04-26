package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import net.krazyweb.cataclysm.mapeditor.map.data.MapTile;

import java.util.HashSet;
import java.util.Set;

public class EraserTool extends Tool {

	private Set<Point> changedPoints = new HashSet<>();

	private boolean dragging = false;

	@Override
	public void click(final MouseEvent event, final MapTile tile, final Node rootNode, final MapEditor map) {
		if (!dragging) {
			changedPoints.clear();
		}
		//TODO Pick tiles better
		if (event.getButton() == MouseButton.PRIMARY) {
			map.startEdit();
			int x = convertCoord(event.getX());
			int y = convertCoord(event.getY());
			map.setTile(x, y, null);
			changedPoints.add(new Point(x, y));
		} else if (event.getButton() == MouseButton.SECONDARY && !dragging) {

			MenuItem clearAllMenuItem = new MenuItem("Erase All");
			clearAllMenuItem.setOnAction(event1 -> {
				map.startEdit();
				for (int x = 0; x < MapEditor.SIZE; x++) {
					for (int y = 0; y < MapEditor.SIZE; y++) {
						map.setTile(x, y, null);
						changedPoints.add(new Point(x, y));
					}
				}
				map.finishEdit("Eraser");
			});

			MenuItem cancelItem = new MenuItem("Cancel");

			ContextMenu contextMenu = new ContextMenu(clearAllMenuItem, new SeparatorMenuItem(), cancelItem);
			contextMenu.setAutoHide(true);
			contextMenu.setHideOnEscape(true);

			cancelItem.setOnAction(event1 -> contextMenu.hide());

			contextMenu.show(rootNode, event.getScreenX(), event.getScreenY());

		}
	}

	@Override
	public void release(final MouseEvent event, final MapTile tile, final Node rootNode, final MapEditor map) {
		dragging = false;
		if (event.getButton() == MouseButton.PRIMARY && !changedPoints.isEmpty()) {
			map.finishEdit("Eraser");
		}
	}

	@Override
	public void dragEnd(final MouseEvent event, final MapTile tile, final Node rootNode, final MapEditor map) {
		dragging = false;
		if (event.getButton() == MouseButton.PRIMARY && !changedPoints.isEmpty()) {
			map.finishEdit("Eraser");
		}
	}

	@Override
	public void drag(final MouseEvent event, final MapTile tile, final Node rootNode, final MapEditor map) {
		dragging = true;
		click(event, tile, rootNode, map);
	}

	@Override
	public Image getHighlightTile(final MapTile tile) {
		return null;
	}

}
