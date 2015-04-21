package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

public class ToiletMapping extends TileMapping {

	public int minAmount, maxAmount;

	public ToiletMapping() {

	}

	public ToiletMapping(final int minAmount, final int maxAmount) {
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
	}

	@Override
	public String getJson() {
		if (minAmount == 0 && maxAmount == 0) {
			return "{ }";
		} else {
			return "{ \"amount\": [ " + minAmount + ", " + maxAmount + " ] }";
		}
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ToiletMapping that = (ToiletMapping) o;

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
	public ToiletMapping copy() {
		return new ToiletMapping(minAmount, maxAmount);
	}

	@Override
	public String toString() {
		return "[Toilet, Min Amount: " + minAmount + ", Max Amount:" + maxAmount + "]";
	}

}
