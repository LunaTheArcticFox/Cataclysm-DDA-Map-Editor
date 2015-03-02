package net.krazyweb.cataclysm.mapeditor.map.data.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.krazyweb.cataclysm.mapeditor.map.data.OverMapEntry;
import org.controlsfx.control.PropertySheet;

public class PropertySheetItemCreator {

	private static interface FieldWrapper {
		Object getValue();
		void setValue(final Object value);
	}

	protected static class PropertyItem implements PropertySheet.Item {

		private Class<?> type;
		private String category;
		private String name;
		private String description;
		private FieldWrapper wrapper;

		public PropertyItem(Class<?> type, String category, String name, String description, FieldWrapper wrapper) {
			this.type = type;
			this.category = category;
			this.name = name;
			this.description = description;
			this.wrapper = wrapper;
		}

		@Override
		public Class<?> getType() {
			return type;
		}

		@Override
		public String getCategory() {
			return category;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public Object getValue() {
			return wrapper.getValue();
		}

		@Override
		public void setValue(Object value) {
			wrapper.setValue(value);
		}

	}

	public static ObservableList<PropertySheet.Item> getPropertySheetItems(final OverMapEntry overMapEntry) {

		ObservableList<PropertySheet.Item> items = FXCollections.observableArrayList();

		items.add(new PropertyItem(String.class, "Overmap", "ID", "The internally used id of the overmap.", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overMapEntry.id;
			}
			@Override
			public void setValue(Object value) {
				overMapEntry.id = (String) value;
			}
		}));

		items.add(new PropertyItem(String.class, "Overmap", "Name", "The display name of the overmap in game.", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overMapEntry.name;
			}
			@Override
			public void setValue(Object value) {
				overMapEntry.name = (String) value;
			}
		}));

		items.add(new PropertyItem(Boolean.class, "Overmap", "Rotate", "When checked, this overmap will be rotated by the game automatically.", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overMapEntry.rotate;
			}
			@Override
			public void setValue(Object value) {
				overMapEntry.rotate = (Boolean) value;
			}
		}));

		items.add(new PropertyItem(Integer.class, "Overmap", "Symbol", "The symbol visible on the overmap in game.", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overMapEntry.symbol;
			}
			@Override
			public void setValue(Object value) {
				overMapEntry.symbol = (Integer) value;
			}
		}));

		items.add(new PropertyItem(String.class, "Overmap", "Symbol Color", "The color of the symbol visible on the overmap in game.", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overMapEntry.symbolColor;
			}
			@Override
			public void setValue(Object value) {
				overMapEntry.symbolColor = (String) value;
			}
		}));

		items.add(new PropertyItem(Integer.class, "Overmap", "See Cost", "The difficulty of seeing past this terrain on the overmap in game.", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overMapEntry.seeCost;
			}
			@Override
			public void setValue(Object value) {
				overMapEntry.seeCost = (Integer) value;
			}
		}));

		items.add(new PropertyItem(String.class, "Overmap", "Extras", "The extras this overmap has.", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overMapEntry.extras;
			}
			@Override
			public void setValue(Object value) {
				overMapEntry.extras = (String) value;
			}
		}));

		items.add(new PropertyItem(Integer.class, "Overmap", "Monster Density", "The density of monsters generated for this overmap.", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overMapEntry.monsterDensity;
			}
			@Override
			public void setValue(Object value) {
				overMapEntry.monsterDensity = (Integer) value;
			}
		}));

		items.add(new PropertyItem(Boolean.class, "Overmap", "Sidewalk", "When checked, this overmap will be placed adjacent to a sidewalk.", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overMapEntry.sidewalk;
			}
			@Override
			public void setValue(Object value) {
				overMapEntry.sidewalk = (Boolean) value;
			}
		}));

		/*
			public String id;
	public String name;
	public boolean rotate;
	public int symbol;
	public String symbolColor;
	public int seeCost;
	public String extras;
	public int monsterDensity;
	public boolean sidewalk;
		 */

		return items;

	}


}
