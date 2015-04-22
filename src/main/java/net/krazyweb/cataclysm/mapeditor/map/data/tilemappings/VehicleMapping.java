package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

import java.util.Optional;

public class VehicleMapping extends TileMapping {

	public String vehicle;
	public Optional<Integer> chance = Optional.empty();
	public Optional<Integer> fuel = Optional.empty();
	public Optional<Integer> status = Optional.empty();

	public VehicleMapping(final String vehicle) {
		this.vehicle = vehicle;
	}

	public VehicleMapping(final String vehicle, final Integer chance, final Integer fuel, final Integer status) {
		this.vehicle = vehicle;
		this.chance = Optional.ofNullable(chance);
		this.fuel = Optional.ofNullable(fuel);
		this.status = Optional.ofNullable(status);
	}

	@Override
	public String getJson() {
		String output = "{ \"vehicle\": \"" + vehicle + "\"";
		if (chance.isPresent()) {
			output += ", \"chance\": " + chance.get();
		}
		if (fuel.isPresent()) {
			output += ", \"fuel\": " + fuel.get();
		}
		if (status.isPresent()) {
			output += ", \"status\": " + status.get();
		}
		return output + " }";
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		VehicleMapping that = (VehicleMapping) o;

		if (!vehicle.equals(that.vehicle)) return false;
		if (!chance.equals(that.chance)) return false;
		if (!fuel.equals(that.fuel)) return false;
		return status.equals(that.status);

	}

	@Override
	public int hashCode() {
		int result = vehicle.hashCode();
		result = 31 * result + chance.hashCode();
		result = 31 * result + fuel.hashCode();
		result = 31 * result + status.hashCode();
		return result;
	}

	@Override
	public VehicleMapping copy() {
		return new VehicleMapping(vehicle, chance.orElse(null), fuel.orElse(null), status.orElse(null));
	}

	@Override
	public String toString() {
		return "[Vehicle: " + vehicle + ", Chance: " + chance.orElse(null) + ", Fuel: " + fuel.orElse(null) + ", Status: " + status.orElse(null) + "]";
	}

}
