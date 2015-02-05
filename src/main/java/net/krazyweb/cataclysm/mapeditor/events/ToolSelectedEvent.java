package net.krazyweb.cataclysm.mapeditor.events;

import net.krazyweb.cataclysm.mapeditor.tools.Tool;

public class ToolSelectedEvent {

	private final Tool tool;

	public ToolSelectedEvent(final Tool tool) {
		this.tool = tool;
	}

	public Tool getTool() {
		return tool;
	}

}
