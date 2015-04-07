package net.krazyweb.cataclysm.mapeditor.map.tilemappings;

import java.util.ArrayList;
import java.util.List;

public class TrapMapping extends TileMapping {

	public String trap;

	public TrapMapping(final String trap) {
		this.trap = trap;
	}

	@Override
	public List<String> getJsonLines() {
		List<String> lines = new ArrayList<>();
		lines.add(trap);
		return lines;
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TrapMapping that = (TrapMapping) o;

		return trap.equals(that.trap);

	}

	@Override
	public int hashCode() {
		return trap.hashCode();
	}

	@Override
	public TrapMapping copy() {
		return new TrapMapping(trap);
	}

}
