package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

import java.util.Optional;

public class ToiletMapping extends TileMapping {

	public Optional<Integer> minAmount = Optional.empty();
	public Optional<Integer> maxAmount = Optional.empty();

	public ToiletMapping() {

	}

	public ToiletMapping(final Integer minAmount, final Integer maxAmount) {
		this.minAmount = Optional.ofNullable(minAmount);
		this.maxAmount = Optional.ofNullable(maxAmount);
	}

	@Override
	public String getJson() {
		if (minAmount.isPresent() && maxAmount.isPresent()) {
			return "{ \"amount\": [ " + minAmount + ", " + maxAmount + " ] }";
		} else {
			return "{ }";
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ToiletMapping that = (ToiletMapping) o;

		if (minAmount != null ? !minAmount.equals(that.minAmount) : that.minAmount != null) return false;
		return !(maxAmount != null ? !maxAmount.equals(that.maxAmount) : that.maxAmount != null);

	}

	@Override
	public int hashCode() {
		int result = minAmount != null ? minAmount.hashCode() : 0;
		result = 31 * result + (maxAmount != null ? maxAmount.hashCode() : 0);
		return result;
	}

	@Override
	public ToiletMapping copy() {
		return new ToiletMapping(minAmount.orElse(null), maxAmount.orElse(null));
	}

	@Override
	public String toString() {
		return "[Toilet, Min Amount: " + minAmount.orElse(null) + ", Max Amount:" + maxAmount.orElse(null) + "]";
	}

}
