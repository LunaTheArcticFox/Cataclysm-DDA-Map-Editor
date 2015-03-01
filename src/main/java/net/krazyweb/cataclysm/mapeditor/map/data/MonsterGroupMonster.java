package net.krazyweb.cataclysm.mapeditor.map.data;

public class MonsterGroupMonster {

	public String monster = "";
	public int frequency;
	public int multiplier;

	public MonsterGroupMonster() {

	}

	public MonsterGroupMonster(final MonsterGroupMonster monsterGroupMonster) {
		this.monster = monsterGroupMonster.monster;
		this.frequency = monsterGroupMonster.frequency;
		this.multiplier = monsterGroupMonster.multiplier;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MonsterGroupMonster that = (MonsterGroupMonster) o;

		return frequency == that.frequency && multiplier == that.multiplier && monster.equals(that.monster);

	}

	@Override
	public int hashCode() {
		int result = monster.hashCode();
		result = 31 * result + frequency;
		result = 31 * result + multiplier;
		return result;
	}

	@Override
	public String toString() {
		return "MonsterGroupMonster[Monster: " + monster + ", Frequency: " + frequency + ", Multiplier: " + multiplier + "]";
	}

}
