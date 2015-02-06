package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.input.MouseButton;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;

import java.util.Stack;

public class FillTool extends Tool {

	private static class Point2D {
		private int x, y;
		private Point2D(final int x, final int y) {
			this.x = x;
			this.y = y;
		}
	}

	@Override
	public void click(final int x, final int y, final Tile tile, final MouseButton mouseButton, final CataclysmMap map) {

		CataclysmMap.Layer layer = tile.isFurniture() ? CataclysmMap.Layer.FURNITURE : CataclysmMap.Layer.TERRAIN;

		String targetTile = map.getTileAt(x, y, layer);

		Stack<Point2D> fillQueue = new Stack<>();
		fillQueue.push(new Point2D(x, y));

		while (!fillQueue.isEmpty()) {
			Point2D point = fillQueue.pop();
			map.setTile(point.x, point.y, tile);
			if (shouldFill(point.x + 1, point.y, tile, targetTile, map)) {
				fillQueue.push(new Point2D(point.x + 1, point.y));
			}
			if (shouldFill(point.x - 1, point.y, tile, targetTile, map)) {
				fillQueue.push(new Point2D(point.x - 1, point.y));
			}
			if (shouldFill(point.x, point.y + 1, tile, targetTile, map)) {
				fillQueue.push(new Point2D(point.x, point.y + 1));
			}
			if (shouldFill(point.x, point.y - 1, tile, targetTile, map)) {
				fillQueue.push(new Point2D(point.x, point.y - 1));
			}
		}

	}

	private boolean shouldFill(final int x, final int y, final Tile replacementTile, final String targetTile, final CataclysmMap map) {
		CataclysmMap.Layer layer = replacementTile.isFurniture() ? CataclysmMap.Layer.FURNITURE : CataclysmMap.Layer.TERRAIN;
		return !map.getTileAt(x, y, layer).equals(replacementTile.getID()) && map.getTileAt(x, y, layer) != null && map.getTileAt(x, y, layer).equals(targetTile);
	}

}
