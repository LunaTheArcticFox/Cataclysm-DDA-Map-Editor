package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.MapEditor;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class FillTool extends Tool {

	@Override
	public void click(final MouseEvent event, final Tile tile, final Node rootNode, final MapEditor map) {

		Set<Point> toFill = getFill(convertCoord(event.getX()), convertCoord(event.getY()), tile, map);

		if (!toFill.isEmpty()) {
			map.startEdit();
			toFill.forEach(point -> map.setTile(point.x, point.y, tile));
			map.finishEdit("Fill");
		}

	}

	@Override
	public Set<Point> getHighlight(final int x, final int y, final Tile tile, final MapEditor map) {
		Set<Point> toFill = getFill(x, y, tile, map);
		toFill.add(new Point(x, y));
		return toFill;
	}

	private Set<Point> getFill(final int x, final int y, final Tile tile, final MapEditor map) {

		MapEditor.Layer layer = tile.isFurniture() ? MapEditor.Layer.FURNITURE : MapEditor.Layer.TERRAIN;

		String targetTile = map.getTileAt(x, y, layer);

		Stack<Point> fillQueue = new Stack<>();
		if (!isSameTile(map.getTileAt(x, y, layer), tile.getID())) {
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

	private boolean shouldFill(final int x, final int y, final Tile replacementTile, final String targetTile, final MapEditor map, final Set<Point> toFill) {
		if (x < 0 || y < 0 || x >= MapEditor.SIZE || y >= MapEditor.SIZE) {
			return false;
		}
		MapEditor.Layer layer = replacementTile.isFurniture() ? MapEditor.Layer.FURNITURE : MapEditor.Layer.TERRAIN;
		return !toFill.contains(new Point(x, y)) && !isSameTile(map.getTileAt(x, y, layer), replacementTile.getID()) && map.getTileAt(x, y, layer) != null && isSameTile(map.getTileAt(x, y, layer), targetTile);
	}

	private boolean isSameTile(final String tile1, final String tile2) {
		if ((tile1.endsWith("_v") || tile1.endsWith("_h")) && (tile2.endsWith("_v") || tile2.endsWith("_h"))) {
			return tile1.substring(0, tile1.lastIndexOf("_")).equals(tile2.substring(0, tile2.lastIndexOf("_")));
		}
		return tile1.equals(tile2);
	}

}
