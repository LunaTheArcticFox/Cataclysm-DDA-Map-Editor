package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import net.krazyweb.cataclysm.mapeditor.map.data.MapTile;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class FillTool extends Tool {

	@Override
	public void click(final MouseEvent event, final MapTile tile, final Node rootNode, final MapEditor map) {

		Set<Point> toFill = getFill(convertCoord(event.getX()), convertCoord(event.getY()), tile, map);

		if (!toFill.isEmpty()) {
			map.startEdit();
			toFill.forEach(point -> map.setTile(point.x, point.y, tile));
			map.finishEdit("Fill");
		}

	}

	@Override
	public Set<Point> getHighlight(final int x, final int y, final MapTile tile, final MapEditor map) {
		Set<Point> toFill = getFill(x, y, tile, map);
		toFill.add(new Point(x, y));
		return toFill;
	}

	private Set<Point> getFill(final int x, final int y, final MapTile tile, final MapEditor map) {

		MapTile targetTile = map.getTileAt(x, y);

		Stack<Point> fillQueue = new Stack<>();
		if (targetTile != null && !targetTile.equals(tile)) {
			fillQueue.push(new Point(x, y));
		} else if (targetTile == null && tile != null) {
			fillQueue.push(new Point(x, y));
		}

		Set<Point> toFill = new HashSet<>();

		while (!fillQueue.isEmpty()) {
			Point point = fillQueue.pop();
			toFill.add(point);
			if (shouldFill(point.x + 1, point.y, tile, targetTile, map, toFill)) {
				fillQueue.push(new Point(point.x + 1, point.y));
			}
			if (shouldFill(point.x - 1, point.y, tile, targetTile, map, toFill)) {
				fillQueue.push(new Point(point.x - 1, point.y));
			}
			if (shouldFill(point.x, point.y + 1, tile, targetTile, map, toFill)) {
				fillQueue.push(new Point(point.x, point.y + 1));
			}
			if (shouldFill(point.x, point.y - 1, tile, targetTile, map, toFill)) {
				fillQueue.push(new Point(point.x, point.y - 1));
			}
		}

		return toFill;

	}

	private boolean shouldFill(final int x, final int y, final MapTile replacementTile, final MapTile targetTile, final MapEditor map, final Set<Point> toFill) {
		if (x < 0 || y < 0 || x >= MapEditor.SIZE || y >= MapEditor.SIZE) {
			return false;
		}
		if (!toFill.contains(new Point(x, y)) && map.getTileAt(x, y) != null && targetTile == null) {
			return false;
		} else if (!toFill.contains(new Point(x, y)) && targetTile == null) {
			return true;
		}
		return !toFill.contains(new Point(x, y)) && map.getTileAt(x, y) != null && !map.getTileAt(x, y).equals(replacementTile) && map.getTileAt(x, y).equals(targetTile);
	}

}
