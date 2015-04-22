package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

import java.util.Optional;

public class VendingMachineMapping extends TileMapping {

	public Optional<String> itemGroup = Optional.empty();

	public VendingMachineMapping() {

	}

	public VendingMachineMapping(final String itemGroup) {
		this.itemGroup = Optional.ofNullable(itemGroup);
	}

	@Override
	public String getJson() {
		if (itemGroup.isPresent()) {
			return "{ \"item_group\": \"" + itemGroup + "\" }";
		}
		return "{ }";
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
		return new VendingMachineMapping(itemGroup.orElse(null));
	}

	@Override
	public String toString() {
		return "[Vending Machine: " + itemGroup.orElse(null) + "]";
	}

}
