package net.krazyweb.cataclysm.mapeditor.map.undo;

public interface Action {
	public void execute();
	public void undo();
}
