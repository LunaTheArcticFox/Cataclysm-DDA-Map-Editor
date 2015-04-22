package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

import java.util.Optional;

public class MonsterGroupMapping extends TileMapping {

	public String monster;
	public Optional<Double> density = Optional.empty();
	public Optional<Integer> chance = Optional.empty();

	public MonsterGroupMapping(final String monster) {
		this.monster = monster;
	}

	public MonsterGroupMapping(final String monster, final Double density, final Integer chance) {
		this.monster = monster;
		this.density = Optional.ofNullable(density);
		this.chance = Optional.ofNullable(chance);
	}

	@Override
	public String getJson() {
		String output = "{ \"monster\": \"" + monster + "\"";
		if (density.isPresent()) {
			output += ", \"density\": " + density.get();
		}
		if (chance.isPresent()) {
			output += ", \"chance\": " + chance.get();
		}
		return output + " }";
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MonsterGroupMapping that = (MonsterGroupMapping) o;

		if (!monster.equals(that.monster)) return false;
		if (!density.equals(that.density)) return false;
		return chance.equals(that.chance);

	}

	@Override
	public int hashCode() {
		int result = monster.hashCode();
		result = 31 * result + density.hashCode();
		result = 31 * result + chance.hashCode();
		return result;
	}

	@Override
	public MonsterGroupMapping copy() {
		return new MonsterGroupMapping(monster, density.orElse(null), chance.orElse(null));
	}

	@Override
	public String toString() {
		return "[Monster Group: " + monster + ", Density: " + density.orElse(null) + ", Chance: " + chance.orElse(null) + "]";
	}

}
