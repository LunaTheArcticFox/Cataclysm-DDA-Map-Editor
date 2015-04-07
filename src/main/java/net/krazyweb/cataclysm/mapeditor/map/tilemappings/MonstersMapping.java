package net.krazyweb.cataclysm.mapeditor.map.tilemappings;

import java.util.ArrayList;
import java.util.List;

public class MonstersMapping extends TileMapping {

	public String monster;
	public double density;
	public int chance;

	public MonstersMapping(final String monster, final double density, final int chance) {
		this.monster = monster;
		this.density = density;
		this.chance = chance;
	}

	@Override
	public List<String> getJsonLines() {
		List<String> lines = new ArrayList<>();
		lines.add("{ \"monster\": \"" + monster + "\", \"density\": " + density + ", \"chance\": " + chance + " }");
		return lines;
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MonstersMapping that = (MonstersMapping) o;

		if (chance != that.chance) return false;
		if (density != that.density) return false;
		return monster.equals(that.monster);

	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = monster.hashCode();
		temp = Double.doubleToLongBits(density);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + chance;
		return result;
	}

	@Override
	public MonstersMapping copy() {
		return new MonstersMapping(monster, density, chance);
	}

}
