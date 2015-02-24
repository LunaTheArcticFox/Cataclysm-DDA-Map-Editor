package net.krazyweb.cataclysm.mapeditor.map;

import javafx.scene.paint.Color;
import net.krazyweb.cataclysm.mapeditor.tools.Point;

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

	public int x, y, w, h;
	public Color fillColor;
	public Color strokeColor;
	public PlaceGroup group;

	public PlaceGroupZone(final PlaceGroupZone zone) {
		this.x = zone.x;
		this.y = zone.y;
		this.w = zone.w;
		this.h = zone.h;
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

	public PlaceGroupZone(final int x, final int y, final int w, final int h, final PlaceGroup group) {
		this(group);
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public void rotate() {

		Point[] points = new Point[] {
				new Point(x, y),
				new Point(x + w, y),
				new Point(x, y + h),
				new Point(x + w, y + h)
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
			x = leastXY.x;
			y = leastXY.y;
			w = Math.abs(greatestXY.x - leastXY.x);
			h = Math.abs(greatestXY.y - leastXY.y);
		} else {
			throw new IllegalStateException("Could not rotate a PlaceGroupZone; somehow leastXY ended up null." +
					" Coordinates pre-rotation: [" + x + ", " + y + ", " + w + ", " + h + "]");
		}

	}

	public boolean contains(final int x, final int y) {
		return x >= this.x && x < this.x + this.w && y >= this.y && y < this.y + this.h;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PlaceGroupZone that = (PlaceGroupZone) o;

		if (h != that.h) return false;
		if (w != that.w) return false;
		if (x != that.x) return false;
		if (y != that.y) return false;
		if (group != null ? !group.equals(that.group) : that.group != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		result = 31 * result + w;
		result = 31 * result + h;
		result = 31 * result + (group != null ? group.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "[Group: " + group + ", Area: [" + x + ", " + y + ", " + w + ", " + h + "], Fill Color: " + fillColor.toString() + ", Stroke Color: " + strokeColor + "]";
	}

	@Override
	public List<String> getJsonLines() {

		List<String> lines = new ArrayList<>();

		String line = "{ \"group\": \"" + group.group + "\", \"chance\": " + group.chance + ", \"x\": ";

		if (w != 1) {
			line += "[ " + x + ", " + (x - 1 + w) + " ], ";
		} else {
			line += x + ", ";
		}

		line += " \"y\": ";

		if (h != 1) {
			line += "[ " + y + ", " + (y - 1 + h) + " ]";
		} else {
			line += y + "";
		}

		line += " }";

		lines.add(line);

		return lines;

	}

}
