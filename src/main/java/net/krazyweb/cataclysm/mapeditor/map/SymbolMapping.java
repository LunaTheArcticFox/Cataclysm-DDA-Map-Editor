package net.krazyweb.cataclysm.mapeditor.map;

public class SymbolMapping {

	protected char symbol;
	protected String terrain;
	protected String furniture;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SymbolMapping that = (SymbolMapping) o;

		if (symbol != that.symbol) return false;
		if (furniture != null ? !furniture.equals(that.furniture) : that.furniture != null) return false;
		if (terrain != null ? !terrain.equals(that.terrain) : that.terrain != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) symbol;
		result = 31 * result + (terrain != null ? terrain.hashCode() : 0);
		result = 31 * result + (furniture != null ? furniture.hashCode() : 0);
		return result;
	}

}
