package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PencilTool extends Tool {

	@Override
	public void click(final int x, final int y, final Canvas canvas) {
		GraphicsContext graphics2D = canvas.getGraphicsContext2D();
		graphics2D.setFill(Color.CHOCOLATE);
		graphics2D.fillRect(x, y, 32, 32);
		//TODO cache subimage textures as needed
		//TODO use selected texture
		//graphics2D.drawImage(SwingFXUtils.toFXImage(texture.value.getSubimage(32, 0, 32, 32), null), ((int) event.getX() / 32) * 32, ((int) event.getY() / 32) * 32);
		//System.out.println("Paint " + ((int) event.getX() / 32) * 32 + "," + ((int) event.getY() / 32) * 32);
	}

	@Override
	public void drag(final int x, final int y, final Canvas canvas) {
		click(x, y, canvas);
	}

}
