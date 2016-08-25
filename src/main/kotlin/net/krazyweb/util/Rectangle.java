package net.krazyweb.util;

public class Rectangle {

	public int x1, y1, x2, y2;

	public Rectangle() {

	}

	public Rectangle(final int x1, final int x2, final int y1, final int y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}

	public Rectangle(final Rectangle rectangle) {
		this.x1 = rectangle.x1;
		this.x2 = rectangle.x2;
		this.y1 = rectangle.y1;
		this.y2 = rectangle.y2;
	}

	public int getArea() {
		return (x2 + 1 - x1) * (y2 + 1 - y1);
	}

	public boolean contains(final int x, final int y) {
		return x >= this.x1 && x < this.x2 + 1 && y >= this.y1 && y < this.y2 + 1;
	}

	public void shift(final int deltaX, final int deltaY) {
		x1 += deltaX;
		x2 += deltaX;
		y1 += deltaY;
		y2 += deltaY;
	}

	public int getWidth() {
		return x2 - x1 + 1;
	}

	public int getHeight() {
		return y2 - y1 + 1;
	}

	public static Rectangle intersectionOf(final Rectangle rectangle1, final Rectangle rectangle2) {

		Rectangle intersection = new Rectangle();

		intersection.x1 = Math.max(rectangle1.x1, rectangle2.x1);
		intersection.x2 = Math.min(rectangle1.x2, rectangle2.x2);
		intersection.y1 = Math.max(rectangle1.y1, rectangle2.y1);
		intersection.y2 = Math.min(rectangle1.y2, rectangle2.y2);

		return intersection;

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Rectangle rectangle = (Rectangle) o;

		return x1 == rectangle.x1 && x2 == rectangle.x2 && y1 == rectangle.y1 && y2 == rectangle.y2;

	}

	@Override
	public int hashCode() {
		int result = x1;
		result = 31 * result + y1;
		result = 31 * result + x2;
		result = 31 * result + y2;
		return result;
	}

	@Override
	public String toString() {
		return "Rectangle[" + x1 + ", " + x2 + ", " + y1 + ", " + y2 + "]";
	}

}