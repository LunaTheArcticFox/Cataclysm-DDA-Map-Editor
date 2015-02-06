package net.krazyweb.cataclysm.mapeditor.map;

public class PlaceGroupZone {

	private class Point2D {
		private int x, y;
		public Point2D(final int x, final int y) {
			this.x = x;
			this.y = y;
		}
	}

	public int x, y, w, h, chance;
	public PlaceGroup group;

	public PlaceGroupZone(final int x, final int y, final int w, final int h, final PlaceGroup group) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.chance = 100; //TODO
		this.group = group;
	}

	public void rotate() {

		Point2D[] points = new Point2D[] {
				new Point2D(x, y),
				new Point2D(x + w, y),
				new Point2D(x, y + h),
				new Point2D(x + w, y + h)
		};

		for (Point2D point : points) {

			point.x -= 12;
			point.y -= 12;

			int temp = point.x;
			point.x = -point.y;
			point.y = temp;

			point.x += 12;
			point.y += 12;

		}

		Point2D leastXY = null;
		Point2D greatestXY = null;

		for (Point2D point : points) {
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

}
