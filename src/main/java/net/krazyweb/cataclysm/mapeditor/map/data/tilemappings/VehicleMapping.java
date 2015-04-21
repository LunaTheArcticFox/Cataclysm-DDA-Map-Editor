package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

public class VehicleMapping extends TileMapping {

	public String vehicle;
	public int chance, fuel, status;

	public VehicleMapping(final String vehicle, final int chance, final int fuel, final int status) {
		this.vehicle = vehicle;
		this.chance = chance;
		this.fuel = fuel;
		this.status = status;
	}

	@Override
	public String getJson() {
		return "{ \"vehicle\": \"" + vehicle + "\", \"chance\": " + chance + ", \"fuel\": " + fuel + ", \"status\": " + status + " }";
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		VehicleMapping that = (VehicleMapping) o;

		if (chance != that.chance) return false;
		if (fuel != that.fuel) return false;
		if (status != that.status) return false;
		return vehicle.equals(that.vehicle);

	}

	@Override
	public int hashCode() {
		int result = vehicle.hashCode();
		result = 31 * result + chance;
		result = 31 * result + fuel;
		result = 31 * result + status;
		return result;
	}

	@Override
	public VehicleMapping copy() {
		return new VehicleMapping(vehicle, chance, fuel, status);
	}

	@Override
	public String toString() {
		return "[Vehicle: " + vehicle + ", Chance: " + chance + ", Fuel: " + fuel + ", Status: " + status + "]";
	}

}
