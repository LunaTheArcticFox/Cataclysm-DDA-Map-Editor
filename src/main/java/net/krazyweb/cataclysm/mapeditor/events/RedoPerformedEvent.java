package net.krazyweb.cataclysm.mapeditor.events;

public class RedoPerformedEvent {

	private String text;

	public RedoPerformedEvent(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

}
