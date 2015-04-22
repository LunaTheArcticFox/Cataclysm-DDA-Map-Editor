package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

import java.util.Optional;

public class MonsterMapping extends TileMapping {

	public String monster;
	public Optional<Boolean> friendly = Optional.empty();
	public Optional<String> name = Optional.empty();

	public MonsterMapping(final String monster) {
		this.monster = monster;
	}

	public MonsterMapping(final String monster, final Boolean friendly, final String name) {
		this.monster = monster;
		this.friendly = Optional.ofNullable(friendly);
		this.name = Optional.ofNullable(name);
	}

	@Override
	public String getJson() {
		String output = "{ \"monster\": \"" + monster + "\"";
		if (friendly.isPresent()) {
			output += ", \"friendly\": " + friendly.get();
		}
		if (name.isPresent()) {
			output += ", \"name\": " + name.get();
		}
		return output + " }";
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MonsterMapping that = (MonsterMapping) o;

		if (!monster.equals(that.monster)) return false;
		if (!friendly.equals(that.friendly)) return false;
		return name.equals(that.name);

	}

	@Override
	public int hashCode() {
		int result = monster.hashCode();
		result = 31 * result + friendly.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}

	@Override
	public MonsterMapping copy() {
		return new MonsterMapping(monster, friendly.orElse(null), name.orElse(null));
	}

	@Override
	public String toString() {
		return "[Monster: " + monster + ", Friendly: " + friendly.orElse(null) + ", Name: " + name.orElse(null) + "]";
	}

}
