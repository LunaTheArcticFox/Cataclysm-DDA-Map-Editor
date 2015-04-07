package net.krazyweb.cataclysm.mapeditor.map.tilemappings;

import java.util.ArrayList;
import java.util.List;

public class NPCMapping extends TileMapping {

	public String npcClass;

	public NPCMapping(final String npcClass) {
		this.npcClass = npcClass;
	}

	@Override
	public List<String> getJsonLines() {
		List<String> lines = new ArrayList<>();
		lines.add("{ \"class\": \"" + npcClass + "\" }");
		return lines;
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		NPCMapping that = (NPCMapping) o;

		return npcClass.equals(that.npcClass);

	}

	@Override
	public int hashCode() {
		return npcClass.hashCode();
	}

	@Override
	public NPCMapping copy() {
		return new NPCMapping(npcClass);
	}

}
