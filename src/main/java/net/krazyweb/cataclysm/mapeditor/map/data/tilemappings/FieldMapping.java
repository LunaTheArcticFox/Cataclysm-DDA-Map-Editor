package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

import java.util.Optional;

public class FieldMapping extends TileMapping {

	public String field;
	public Optional<Integer> age = Optional.empty();
	public Optional<Integer> density = Optional.empty();

	public FieldMapping(final String field) {
		this.field = field;
	}

	public FieldMapping(final String field, final Integer age, final Integer density) {
		this.field = field;
		this.age = Optional.ofNullable(age);
		this.density = Optional.ofNullable(density);
	}

	@Override
	public String getJson() {
		String output = "{ \"field\": \"" + field;
		if (age.isPresent()) {
			output += ", \"age\": " + age.get();
		}
		if (density.isPresent()) {
			output += ", \"density\": " + density.get();
		}
		return output + " }";
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FieldMapping that = (FieldMapping) o;

		if (!field.equals(that.field)) return false;
		if (age != null ? !age.equals(that.age) : that.age != null) return false;
		return !(density != null ? !density.equals(that.density) : that.density != null);

	}

	@Override
	public int hashCode() {
		int result = field.hashCode();
		result = 31 * result + (age != null ? age.hashCode() : 0);
		result = 31 * result + (density != null ? density.hashCode() : 0);
		return result;
	}

	@Override
	public FieldMapping copy() {
		return new FieldMapping(field, age.orElse(null), density.orElse(null));
	}

	@Override
	public String toString() {
		return "[Field: " + field + ", Age: " + age.orElse(null) + ", Density: " + density.orElse(null) + "]";
	}

}
