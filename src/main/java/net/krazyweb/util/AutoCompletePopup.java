

package net.krazyweb.util;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Popup;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AutoCompletePopup {

	@SuppressWarnings("unused")
	private static Logger log = LogManager.getLogger(AutoCompletePopup.class);

	private Popup popup = new Popup();
	private ListView<String> optionList;
	private TextField textField;

	private AutoCompletePopup() {

		popup.setAutoHide(false);
		popup.setHideOnEscape(true);
		popup.setWidth(100);
		popup.setHeight(300);

	}

	public static void bind(final TextField textField, final ObservableList<String> options) {

		options.sort((o1, o2) -> {

			double distance1 = StringUtils.getJaroWinklerDistance(textField.getText(), o1);
			double distance2 = StringUtils.getJaroWinklerDistance(textField.getText(), o2);

			if (distance1 > distance2) {
				return -1;
			} else if (distance1 < distance2) {
				return 1;
			}

			return 0;

		});

		AutoCompletePopup autoCompletePopup = new AutoCompletePopup();
		autoCompletePopup.optionList = new ListView<>(options);
		autoCompletePopup.textField = textField;

		autoCompletePopup.popup.getContent().add(autoCompletePopup.optionList);

		textField.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
			if (isFocused) {
				autoCompletePopup.show();
			} else {
				autoCompletePopup.popup.hide();
			}
		});

		textField.textProperty().addListener((observable, oldValue, newValue) -> {

			options.sort((o1, o2) -> {

				double distance1 = StringUtils.getJaroWinklerDistance(newValue, o1);
				double distance2 = StringUtils.getJaroWinklerDistance(newValue, o2);

				if (distance1 > distance2) {
					return -1;
				} else if (distance1 < distance2) {
					return 1;
				}

				return 0;

			});

			autoCompletePopup.optionList.getSelectionModel().selectFirst();

		});

		autoCompletePopup.optionList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		autoCompletePopup.optionList.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() >= 2) {
				textField.setText(autoCompletePopup.optionList.getSelectionModel().getSelectedItem());
				autoCompletePopup.popup.hide();
				Platform.runLater(textField::end);
			}
		});

		autoCompletePopup.optionList.setOnKeyPressed(event -> {

			switch (event.getCode()) {
				case ENTER:
					textField.setText(autoCompletePopup.optionList.getSelectionModel().getSelectedItem());
					autoCompletePopup.popup.hide();
					Platform.runLater(textField::end);
					break;
				case ESCAPE:
					autoCompletePopup.popup.hide();
					break;
				case UP:
					if (autoCompletePopup.optionList.getSelectionModel().isSelected(0)) {
						autoCompletePopup.popup.hide();
					}
					break;
				case A:
					if (event.isControlDown()) {
						autoCompletePopup.textField.selectAll();
						event.consume();
					}
					break;
				case END:
					autoCompletePopup.textField.end();
					event.consume();
					break;
				case HOME:
					autoCompletePopup.textField.home();
					event.consume();
					break;
			}

		});

		textField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.DOWN) {
				autoCompletePopup.show();
			}
		});

	}

	private void show() {

		popup.show(textField,
				textField.getScene().getWindow().getX() + textField.localToScene(0, 0).getX() + textField.getScene().getX(),
				textField.getScene().getWindow().getY() + textField.localToScene(0, 0).getY() + textField.getScene().getY() + textField.heightProperty().get());
		optionList.getSelectionModel().selectFirst();

	}

}
