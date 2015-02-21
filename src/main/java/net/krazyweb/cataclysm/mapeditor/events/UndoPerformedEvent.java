package net.krazyweb.cataclysm.mapeditor.events;

public class UndoPerformedEvent {

	private String text;

	public UndoPerformedEvent(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

}
