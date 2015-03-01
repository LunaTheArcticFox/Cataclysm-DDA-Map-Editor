package net.krazyweb.cataclysm.mapeditor.map;

import javafx.scene.paint.Color;
import net.krazyweb.cataclysm.mapeditor.tools.Point;
import net.krazyweb.util.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class PlaceGroupZone implements Jsonable {

	//TODO More colors
	private static final Color[][] ZONE_COLORS = new Color[][] {
			new Color[] { Color.rgb(225, 50,  50,  0.3), Color.rgb(225, 50,  50,  0.85)},
			new Color[] { Color.rgb(225, 50,  225, 0.3), Color.rgb(225, 50,  225, 0.85)},
			new Color[] { Color.rgb(50,  100, 225, 0.3), Color.rgb(50,  100, 225, 0.85)},
			new Color[] { Color.rgb(50,  225, 225, 0.3), Color.rgb(50,  225, 225, 0.85)},
			new Color[] { Color.rgb(50,  225, 50,  0.3), Color.rgb(50,  225, 50,  0.85)},
			new Color[] { Color.rgb(225, 225, 50,  0.3), Color.rgb(225, 225, 50,  0.85)},
			new Color[] { Color.rgb(225, 125, 50,  0.3), Color.rgb(225, 125, 50,  0.85)},
	};

	private static int currentZoneColor = 0;

	public Rectangle bounds = new Rectangle();
	public Color fillColor;
	public Color strokeColor;
	public PlaceGroup group;

	public PlaceGroupZone(final PlaceGroupZone zone) {
		this.bounds = new Rectangle(zone.bounds);
		this.fillColor = zone.fillColor;
		this.strokeColor = zone.strokeColor;
		this.group = new PlaceGroup(zone.group);
	}

	public PlaceGroupZone(final PlaceGroup group) {
		this.group = group;
		fillColor = ZONE_COLORS[currentZoneColor][0];
		strokeColor = ZONE_COLORS[currentZoneColor][1];
		if (++currentZoneColor >= ZONE_COLORS.length) {
			currentZoneColor = 0;
		}
	}

	public PlaceGroupZone(final int x1, final int x2, final int y1, final int y2, final PlaceGroup group) {
		this(group);
		bounds = new Rectangle(x1, x2, y1, y2);
	}

	public void rotate() {

		Point[] points = new Point[] {
				new Point(bounds.x1, bounds.y1),
				new Point(bounds.x2 + 1, bounds.y1),
				new Point(bounds.x1, bounds.y2 + 1),
				new Point(bounds.x2 + 1, bounds.y2 + 1)
		};

		for (Point point : points) {

			point.x -= 12;
			point.y -= 12;

			int temp = point.x;
			point.x = -point.y;
			point.y = temp;

			point.x += 12;
			point.y += 12;

		}

		Point leastXY = null;
		Point greatestXY = null;

		for (Point point : points) {
			if (leastXY == null || (point.x <= leastXY.x && point.y <= leastXY.y)) {
				leastXY = point;
			}
			if (greatestXY == null || (point.x >= greatestXY.x && point.y >= greatestXY.y)) {
				greatestXY = point;
			}
		}

		if (leastXY != null) {
			bounds.x1 = leastXY.x;
			bounds.x2 = greatestXY.x - 1;
			bounds.y1 = leastXY.y;
			bounds.y2 = greatestXY.y - 1;
		} else {
			throw new IllegalStateException("Could not rotate a PlaceGroupZone; somehow leastXY ended up null." +
					" Coordinates pre-rotation: [" + bounds.x1 + ", " + bounds.x2 + ", " + bounds.y1 + ", " + bounds.y2 + "]");
		}

	}

	public boolean contains(final int x, final int y) {
		return bounds.contains(x, y);
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PlaceGroupZone zone = (PlaceGroupZone) o;

		return bounds.equals(zone.bounds) && group.equals(zone.group);

	}

	@Override
	public int hashCode() {
		int result = bounds.hashCode();
		result = 31 * result + group.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "[Group: " + group + ", Bounds: " + bounds + ", Fill Color: " + fillColor.toString() + ", Stroke Color: " + strokeColor + "]";
	}

	@Override
	public List<String> getJsonLines() {

		List<String> lines = new ArrayList<>();

		String line = "{ \"" + group.type + "\": \"" + group.group + "\", \"chance\": " + group.chance + ", \"x\": ";

		Rectangle croppedBounds = Rectangle.intersectionOf(new Rectangle(0, MapEditor.SIZE, 0, MapEditor.SIZE), bounds);

		if (croppedBounds.x1 != croppedBounds.x2) {
			line += "[ " + croppedBounds.x1 + ", " + croppedBounds.x2 + " ], ";
		} else {
			line += croppedBounds.x1 + ", ";
		}

		line += " \"y\": ";

		if (croppedBounds.y1 != croppedBounds.y2) {
			line += "[ " + croppedBounds.y1 + ", " + croppedBounds.y2 + " ]";
		} else {
			line += croppedBounds.y1 + "";
		}

		line += " }";

		lines.add(line);

		return lines;

	}

}
