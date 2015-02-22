package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import net.krazyweb.cataclysm.mapeditor.Tile;
import net.krazyweb.cataclysm.mapeditor.map.CataclysmMap;

import java.util.HashSet;
import java.util.Set;

public class LineTool extends Tool {

	private int startX;
	private int startY;

	private boolean dragging = false;

	@Override
	public void release(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {
		dragging = false;
		startX = convertCoord(event.getX());
		startY = convertCoord(event.getY());
		dragEnd(event, tile, rootNode, map);
	}

	@Override
	public void dragStart(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {
		map.startEdit();
		startX = convertCoord(event.getX());
		startY = convertCoord(event.getY());
		dragging = true;
	}

	@Override
	public void dragEnd(final MouseEvent event, final Tile tile, final Node rootNode, final CataclysmMap map) {
		dragging = false;
		for (Point point : getLine(convertCoord(event.getX()), convertCoord(event.getY()))) {
			map.setTile(point.x, point.y, tile);
		}
		map.finishEdit("Line");
	}

	@Override
	public Set<Point> getHighlight(final int x, final int y, final Tile tile, final CataclysmMap map) {
		return dragging ? getLine(x, y) : super.getHighlight(x, y, tile, map);
	}

	private Set<Point> getLine(int x, int y) {

		int delta = 0;

		int dirX = Math.abs(x - startX);
		int dirY = Math.abs(y - startY);

		int dirX2 = (dirX << 1);
		int dirY2 = (dirY << 1);

		int ix = startX < x ? 1 : -1;
		int iy = startY < y ? 1 : -1;

		int lineX = startX;
		int lineY = startY;

		Set<Point> line = new HashSet<>();

		if (dirY <= dirX) {
			while (true) {
				line.add(new Point(lineX, lineY));
				if (lineX == x) {
					break;
				}
				lineX += ix;
				delta += dirY2;
				if (delta > dirX) {
					lineY += iy;
					delta -= dirX2;
				}
			}
		} else {
			while (true) {
				line.add(new Point(lineX, lineY));
				if (lineY == y) {
					break;
				}
				lineY += iy;
				delta += dirX2;
				if (delta > dirY) {
					lineX += ix;
					delta -= dirY2;
				}
			}
		}

		return line;

	}

}
