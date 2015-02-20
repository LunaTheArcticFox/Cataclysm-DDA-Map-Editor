package net.krazyweb.cataclysm.mapeditor.events;

public class UpdateUndoTextEvent {

	private String text;

	public UpdateUndoTextEvent(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

}
