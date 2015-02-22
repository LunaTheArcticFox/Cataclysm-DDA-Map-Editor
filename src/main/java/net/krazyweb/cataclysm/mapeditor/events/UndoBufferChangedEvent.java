package net.krazyweb.cataclysm.mapeditor.events;

public class UndoBufferChangedEvent {

	private String undoText, redoText;

	public UndoBufferChangedEvent(final String undoText, final String redoText) {
		this.undoText = undoText;
		this.redoText = redoText;
	}

	public String getUndoText() {
		return undoText;
	}

	public String getRedoText() {
		return redoText;
	}

}
