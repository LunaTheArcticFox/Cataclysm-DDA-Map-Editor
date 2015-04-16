package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

import java.util.ArrayList;
import java.util.List;

public class VendingMachineMapping extends TileMapping {

	public String itemGroup;

	public VendingMachineMapping(final String itemGroup) {
		this.itemGroup = itemGroup;
	}

	@Override
	public List<String> getJsonLines() {
		List<String> lines = new ArrayList<>();
		lines.add("{ \"item_group\": \"" + itemGroup + "\" }");
		return lines;
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		VendingMachineMapping that = (VendingMachineMapping) o;

		return itemGroup.equals(that.itemGroup);

	}

	@Override
	public int hashCode() {
		return itemGroup.hashCode();
	}

	@Override
	public VendingMachineMapping copy() {
		return new VendingMachineMapping(itemGroup);
	}

}
