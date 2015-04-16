package net.krazyweb.util;

import javafx.fxml.FXMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;

public class FXMLHelper {

	private static final Logger log = LogManager.getLogger(FXMLHelper.class);

	public static Optional<FXMLLoader> loadFXML(final String fxmlLocation) {

		FXMLLoader fxmlLoader = new FXMLLoader(FXMLHelper.class.getResource(fxmlLocation));
		try {
			fxmlLoader.load();
		} catch (IllegalStateException | IOException e) {
			log.error("Error while attempting to load '" + fxmlLocation + "':", e);
			return Optional.empty();
		}

		return Optional.of(fxmlLoader);

	}

}
