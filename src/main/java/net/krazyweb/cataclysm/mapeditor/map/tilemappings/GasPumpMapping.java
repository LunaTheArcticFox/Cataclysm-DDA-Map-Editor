package net.krazyweb.cataclysm.mapeditor.map.tilemappings;

import java.util.ArrayList;
import java.util.List;

public class GasPumpMapping extends TileMapping {

	public int minAmount, maxAmount;

	public GasPumpMapping() {

	}

	public GasPumpMapping(final int minAmount, final int maxAmount) {
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
	}

	@Override
	public List<String> getJsonLines() {
		List<String> lines = new ArrayList<>();
		lines.add("{ \"amount\": [ " + minAmount + ", " + maxAmount + " ] }");
		return lines;
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GasPumpMapping that = (GasPumpMapping) o;

		if (minAmount != that.minAmount) return false;
		return maxAmount == that.maxAmount;

	}

	@Override
	public int hashCode() {
		int result = minAmount;
		result = 31 * result + maxAmount;
		return result;
	}

	@Override
	public GasPumpMapping copy() {
		return new GasPumpMapping(minAmount, maxAmount);
	}

}
