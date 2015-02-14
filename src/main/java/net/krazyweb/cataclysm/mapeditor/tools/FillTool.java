package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FillTool extends Tool {

	@Override
	public void click(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {

		for (Point point : getFill(convertCoord(event.getX()), convertCoord(event.getY()), tile, map)) {
			map.setTile(point.x, point.y, tile);
		}

	}

	@Override
	public List<Point> getHighlight(final int x, final int y, final Tile tile, final CataclysmMap map) {
		return getFill(x, y, tile, map);
	}

	private List<Point> getFill(final int x, final int y, final Tile tile, final CataclysmMap map) {

		CataclysmMap.Layer layer = tile.isFurniture() ? CataclysmMap.Layer.FURNITURE : CataclysmMap.Layer.TERRAIN;

		String targetTile = map.getTileAt(x, y, layer);

		Stack<Point> fillQueue = new Stack<>();
		fillQueue.push(new Point(x, y));

		List<Point> toFill = new ArrayList<>();

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

	private boolean shouldFill(final int x, final int y, final Tile replacementTile, final String targetTile, final CataclysmMap map, final List<Point> toFill) {
		if (x < 0 || y < 0 || x >= CataclysmMap.SIZE || y >= CataclysmMap.SIZE) {
			return false;
		}
		CataclysmMap.Layer layer = replacementTile.isFurniture() ? CataclysmMap.Layer.FURNITURE : CataclysmMap.Layer.TERRAIN;
		return !toFill.contains(new Point(x, y)) && !isSameTile(map.getTileAt(x, y, layer), replacementTile.getID()) && map.getTileAt(x, y, layer) != null && isSameTile(map.getTileAt(x, y, layer), targetTile);
	}

	private boolean isSameTile(final String tile1, final String tile2) {
		if ((tile1.endsWith("_v") || tile1.endsWith("_h")) && (tile2.endsWith("_v") || tile2.endsWith("_h"))) {
			return tile1.substring(0, tile1.lastIndexOf("_")).equals(tile2.substring(0, tile2.lastIndexOf("_")));
		}
		return tile1.equals(tile2);
	}

}
