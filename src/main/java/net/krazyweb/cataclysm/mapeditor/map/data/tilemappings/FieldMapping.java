package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

public class FieldMapping extends TileMapping {

	public String field;
	public int age, density;

	public FieldMapping(final String field, final int age, final int density) {
		this.field = field;
		this.age = age;
		this.density = density;
	}

	@Override
	public String getJson() {
		return "{ \"field\": \"" + field + "\", \"age\": " + age + ", \"density\": " + density + " }";
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FieldMapping that = (FieldMapping) o;

		if (age != that.age) return false;
		if (density != that.density) return false;
		return field.equals(that.field);

	}

	@Override
	public int hashCode() {
		int result = field.hashCode();
		result = 31 * result + age;
		result = 31 * result + density;
		return result;
	}

	@Override
	public FieldMapping copy() {
		return new FieldMapping(field, age, density);
	}

	@Override
	public String toString() {
		return "[Field: " + field + ", Age: " + age + ", Density: " + density + "]";
	}

}
