package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import net.krazyweb.cataclysm.mapeditor.TileSet;

public class LineTool extends Tool {

	public void drawPoint(final int x, final int y, final Canvas canvas) {
		GraphicsContext graphics2D = canvas.getGraphicsContext2D();
		//TODO cache subimage textures as needed
		//TODO use selected texture
		graphics2D.drawImage(TileSet.textures.get("t_dirtfloor"), x, y);
	}

	private int startX;
	private int startY;
	private boolean dragging = false;

	/*@Override
	public void drag(final int x, final int y, final Canvas canvas) {
		if (!dragging) {
			dragStart(x, y, canvas);
		}
	}

	@Override
	public void dragStart(final int x, final int y, final Canvas canvas) {
		System.out.println("Start");
		dragging = true;
		startX = x;
		startY = y;
	}

	@Override
	public void dragEnd(final int x, final int y, final Canvas canvas) {

		System.out.println("End");

		dragging = false;

		int dx = x - startX;
		int dy = y - startY;

		int d = 2 * dy - dx;

		drawPoint(x, y, canvas);

		int currentX = 0;
		int targetX = 0;

		if (dx > 0) {
			currentX = startX;
			targetX = x;
		} else {
			currentX = x;
			targetX = startX;
		}

		int traversalY = y;

		for (int i = currentX + 1; i <= targetX; i++) {
			if (d > 0) {
				drawPoint(currentX, ++traversalY, canvas);
				d = d + (2 * dy - 2 * dx);
			} else {
				drawPoint(currentX, traversalY, canvas);
				d = d + (2 * dy);
			}
		}

	}*/

}
