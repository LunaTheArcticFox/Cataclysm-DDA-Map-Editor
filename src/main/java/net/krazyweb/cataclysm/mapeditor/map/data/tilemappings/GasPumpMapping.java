package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

public class GasPumpMapping extends TileMapping {

	public int minAmount, maxAmount;

	public GasPumpMapping() {

	}

	public GasPumpMapping(final int minAmount, final int maxAmount) {
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
	}

	@Override
	public String getJson() {
		return "{ \"amount\": [ " + minAmount + ", " + maxAmount + " ] }";
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

	@Override
	public String toString() {
		return "[Gas Pump, Min Amount: " + minAmount + ", Max Amount:" + maxAmount + "]";
	}

}
