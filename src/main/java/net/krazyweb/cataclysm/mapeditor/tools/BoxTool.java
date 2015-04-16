package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import net.krazyweb.cataclysm.mapeditor.map.MapEditor;
import net.krazyweb.cataclysm.mapeditor.map.data.MapTile;

import java.util.HashSet;
import java.util.Set;

public class BoxTool extends Tool {

	private int startX;
	private int startY;

	private boolean dragging = false;

	@Override
	public void click(final MouseEvent event, final MapTile tile, final Node rootNode, final MapEditor map) {
		map.startEdit();
	}

	@Override
	public void release(final MouseEvent event, final MapTile tile, final Node rootNode, final MapEditor map) {
		dragging = false;
		startX = convertCoord(event.getX());
		startY = convertCoord(event.getY());
		dragEnd(event, tile, rootNode, map);
	}

	@Override
	public void dragStart(final MouseEvent event, final MapTile tile, final Node rootNode, final MapEditor map) {
		dragging = true;
		startX = convertCoord(event.getX());
		startY = convertCoord(event.getY());
	}

	@Override
	public void dragEnd(final MouseEvent event, final MapTile tile, final Node rootNode, final MapEditor map) {
		dragging = false;
		for (Point point : getBox(convertCoord(event.getX()), convertCoord(event.getY()))) {
			map.setTile(point.x, point.y, tile);
		}
		map.finishEdit("Box");
	}

	@Override
	public Set<Point> getHighlight(final int x, final int y, final MapTile tile, final MapEditor map) {
		return dragging ? getBox(x, y) : super.getHighlight(x, y, tile, map);
	}

	private Set<Point> getBox(final int x, final int y) {

		Set<Point> box = new HashSet<>();

		int xDirection = x > startX ? -1 : 1;
		int yDirection = y > startY ? -1 : 1;

		int xAmount = Math.abs(x - startX);
		int yAmount = Math.abs(y - startY);

		for (int lineX = 0; lineX <= xAmount; lineX++) {
			box.add(new Point(x + xDirection * lineX, startY));
			box.add(new Point(x + xDirection * lineX, y));
		}

		for (int lineY = 0; lineY <= yAmount; lineY++) {
			box.add(new Point(startX, y + yDirection * lineY));
			box.add(new Point(x, y + yDirection * lineY));
		}

		return box;

	}

}
