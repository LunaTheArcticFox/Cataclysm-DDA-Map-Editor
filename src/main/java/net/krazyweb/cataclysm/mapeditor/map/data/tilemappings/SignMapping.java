package net.krazyweb.cataclysm.mapeditor.map.data.tilemappings;

public class SignMapping extends TileMapping {

	public String signage;

	public SignMapping(final String signage) {
		this.signage = signage;
	}

	@Override
	public String getJson() {
		return "{ \"signage\": \"" + signage + "\" }";
	}

	@Override
	public boolean equals(final Object o) {

		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SignMapping that = (SignMapping) o;

		return signage.equals(that.signage);

	}

	@Override
	public int hashCode() {
		return signage.hashCode();
	}

	@Override
	public SignMapping copy() {
		return new SignMapping(signage);
	}

	@Override
	public String toString() {
		return "[Sign: " + signage + "]";
	}

}
