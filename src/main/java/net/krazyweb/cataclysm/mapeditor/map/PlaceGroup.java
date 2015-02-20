package net.krazyweb.cataclysm.mapeditor.map;

public class PlaceGroup {

	public String type;
	public String group;
	public int chance;

	public PlaceGroup() {

	}

	public PlaceGroup(final PlaceGroup placeGroup) {
		this.type = placeGroup.type;
		this.group = placeGroup.group;
		this.chance = placeGroup.chance;
	}

}
