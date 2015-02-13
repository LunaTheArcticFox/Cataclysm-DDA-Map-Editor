package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FillTool extends Tool {

	private static class Point2D {

		private int x, y;

		private Point2D(final int x, final int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Point2D point2D = (Point2D) o;

			return x == point2D.x && y == point2D.y;
		}

		@Override
		public int hashCode() {
			int result = x;
			result = 31 * result + y;
			return result;
		}

	}

	@Override
	public void click(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {

		int x = convertCoord(event.getX());
		int y = convertCoord(event.getY());

		CataclysmMap.Layer layer = tile.isFurniture() ? CataclysmMap.Layer.FURNITURE : CataclysmMap.Layer.TERRAIN;

		String targetTile = map.getTileAt(x, y, layer);

		Stack<Point2D> fillQueue = new Stack<>();
		fillQueue.push(new Point2D(x, y));

		List<Point2D> toFill = new ArrayList<>();

		while (!fillQueue.isEmpty()) {
			Point2D point = fillQueue.pop();
			toFill.add(point);
			if (shouldFill(point.x + 1, point.y, tile, targetTile, map, toFill)) {
				fillQueue.push(new Point2D(point.x + 1, point.y));
			}
			if (shouldFill(point.x - 1, point.y, tile, targetTile, map, toFill)) {
				fillQueue.push(new Point2D(point.x - 1, point.y));
			}
			if (shouldFill(point.x, point.y + 1, tile, targetTile, map, toFill)) {
				fillQueue.push(new Point2D(point.x, point.y + 1));
			}
			if (shouldFill(point.x, point.y - 1, tile, targetTile, map, toFill)) {
				fillQueue.push(new Point2D(point.x, point.y - 1));
			}
		}

		for (Point2D point : toFill) {
			map.setTile(point.x, point.y, tile);
		}

	}

	private boolean shouldFill(final int x, final int y, final Tile replacementTile, final String targetTile, final CataclysmMap map, final List<Point2D> toFill) {
		CataclysmMap.Layer layer = replacementTile.isFurniture() ? CataclysmMap.Layer.FURNITURE : CataclysmMap.Layer.TERRAIN;
		if (toFill.contains(new Point2D(x, y))) {
			return false;
		}
		return !map.getTileAt(x, y, layer).equals(replacementTile.getID()) && map.getTileAt(x, y, layer) != null && map.getTileAt(x, y, layer).equals(targetTile);
	}

}
