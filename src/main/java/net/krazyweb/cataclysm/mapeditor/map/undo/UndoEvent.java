package net.krazyweb.cataclysm.mapeditor.map.undo;

import java.util.ArrayList;
import java.util.List;

public class UndoEvent {

	private String name = "";

	private List<Action> actions = new ArrayList<>();

	public void setName(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addAction(final Action action) {
		actions.add(action);
	}

	public void undo() {
		actions.forEach(Action::undo);
	}

	public void redo() {
		actions.forEach(Action::execute);
	}

	@Override
	public String toString() {
		return name;
	}

}
