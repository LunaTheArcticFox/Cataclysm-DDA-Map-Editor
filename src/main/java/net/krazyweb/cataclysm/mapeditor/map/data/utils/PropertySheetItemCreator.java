package net.krazyweb.cataclysm.mapeditor.map.data.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.krazyweb.cataclysm.mapeditor.map.data.OvermapEntry;
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

	public static ObservableList<PropertySheet.Item> getPropertySheetItems(final OvermapEntry overmapEntry) {

		ObservableList<PropertySheet.Item> items = FXCollections.observableArrayList();

		items.add(new PropertyItem(String.class, "Overmap", "ID", "The internally used id of the overmap.", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overmapEntry.id;
			}
			@Override
			public void setValue(Object value) {
				overmapEntry.id = (String) value;
			}
		}));

		items.add(new PropertyItem(String.class, "Overmap", "Name", "The display name of the overmap in game.", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overmapEntry.name;
			}
			@Override
			public void setValue(Object value) {
				overmapEntry.name = (String) value;
			}
		}));

		items.add(new PropertyItem(Boolean.class, "Overmap", "Rotate", "?", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overmapEntry.rotate;
			}
			@Override
			public void setValue(Object value) {
				overmapEntry.rotate = (Boolean) value;
			}
		}));

		items.add(new PropertyItem(Boolean.class, "Overmap", "Line Drawing", "?", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overmapEntry.lineDrawing;
			}
			@Override
			public void setValue(Object value) {
				overmapEntry.lineDrawing = (Boolean) value;
			}
		}));

		items.add(new PropertyItem(Integer.class, "Overmap", "Symbol", "The symbol visible on the overmap in game.", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overmapEntry.symbol;
			}
			@Override
			public void setValue(Object value) {
				overmapEntry.symbol = (Integer) value;
			}
		}));

		items.add(new PropertyItem(String.class, "Overmap", "Symbol Color", "The color of the symbol visible on the overmap in game.", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overmapEntry.symbolColor;
			}
			@Override
			public void setValue(Object value) {
				overmapEntry.symbolColor = (String) value;
			}
		}));

		items.add(new PropertyItem(Integer.class, "Overmap", "See Cost", "The difficulty of seeing past this terrain on the overmap in game.", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overmapEntry.seeCost;
			}
			@Override
			public void setValue(Object value) {
				overmapEntry.seeCost = (Integer) value;
			}
		}));

		items.add(new PropertyItem(String.class, "Overmap", "Extras", "?", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overmapEntry.extras;
			}
			@Override
			public void setValue(Object value) {
				overmapEntry.extras = (String) value;
			}
		}));

		items.add(new PropertyItem(Boolean.class, "Overmap", "Known Down", "?", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overmapEntry.knownDown;
			}
			@Override
			public void setValue(Object value) {
				overmapEntry.knownDown = (Boolean) value;
			}
		}));

		items.add(new PropertyItem(Boolean.class, "Overmap", "Known Up", "?", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overmapEntry.knownUp;
			}
			@Override
			public void setValue(Object value) {
				overmapEntry.knownUp = (Boolean) value;
			}
		}));

		items.add(new PropertyItem(Integer.class, "Overmap", "Monster Density", "The density of monsters generated for this overmap.", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overmapEntry.monsterDensity;
			}
			@Override
			public void setValue(Object value) {
				overmapEntry.monsterDensity = (Integer) value;
			}
		}));

		items.add(new PropertyItem(Boolean.class, "Overmap", "Sidewalk", "?", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overmapEntry.sidewalk;
			}
			@Override
			public void setValue(Object value) {
				overmapEntry.sidewalk = (Boolean) value;
			}
		}));

		items.add(new PropertyItem(Boolean.class, "Overmap", "Allow Road", "?", new FieldWrapper() {
			@Override
			public Object getValue() {
				return overmapEntry.allowRoad;
			}
			@Override
			public void setValue(Object value) {
				overmapEntry.allowRoad = (Boolean) value;
			}
		}));

		return items;

	}


}
