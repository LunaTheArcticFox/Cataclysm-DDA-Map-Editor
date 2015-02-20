package net.krazyweb.cataclysm.mapeditor.events;

public class UpdateRedoTextEvent {

	private String text;

	public UpdateRedoTextEvent(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

}
