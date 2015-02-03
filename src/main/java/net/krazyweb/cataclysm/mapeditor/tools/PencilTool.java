package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import net.krazyweb.cataclysm.mapeditor.TileSet;

public class PencilTool extends Tool {

	@Override
	public void click(final int x, final int y, final Canvas canvas) {
		GraphicsContext graphics2D = canvas.getGraphicsContext2D();
		//TODO cache subimage textures as needed
		//TODO use selected texture
		graphics2D.drawImage(TileSet.textures.get("t_dirtfloor"), x, y);
	}

	@Override
	public void drag(final int x, final int y, final Canvas canvas) {
		click(x, y, canvas);
	}

}
