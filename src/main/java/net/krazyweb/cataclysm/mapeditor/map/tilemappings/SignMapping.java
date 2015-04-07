package net.krazyweb.cataclysm.mapeditor.map.tilemappings;

import java.util.ArrayList;
import java.util.List;

public class SignMapping extends TileMapping {

	public String signage;

	public SignMapping(final String signage) {
		this.signage = signage;
	}

	@Override
	public List<String> getJsonLines() {
		List<String> lines = new ArrayList<>();
		lines.add("{ \"signage\": \"" + signage + "\" }");
		return lines;
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SignMapping that = (SignMapping) o;

		return signage.equals(that.signage);

	}

	@Override
	public int hashCode() {
		return signage.hashCode();
	}

	@Override
	public SignMapping copy() {
		return new SignMapping(signage);
	}

}
