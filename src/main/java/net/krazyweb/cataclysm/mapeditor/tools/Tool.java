package net.krazyweb.cataclysm.mapeditor.tools;

import javafx.scene.canvas.Canvas;

public abstract class Tool {

	//TODO Pass in the actual map array and selected tile type in addition to x, y, and canvas
	public void click(final int x, final int y, final Canvas canvas) {} //Single point click and release
	public void drag(final int x, final int y, final Canvas canvas) {} //Click and drag for each movement
	public void dragStart(final int x, final int y, final Canvas canvas) {} //Start of drag, single point
	public void dragEnd(final int x, final int y, final Canvas canvas) {} //End of drag, single point

}
