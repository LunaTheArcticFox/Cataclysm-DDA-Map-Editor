package net.krazyweb.cataclysm.mapeditor.map;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

public class DataFileWriter extends Service<Boolean> {

	private static Logger log = LogManager.getLogger(DataFileWriter.class);

	private Path path;

	private MapEditor map;

	public DataFileWriter(final Path path, final MapEditor map) {
		this.path = path;
		this.map = map;
	}

	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				save();
				return Boolean.TRUE;
			}
		};
	}

	public MapEditor getMap() {
		return map;
	}

	private void save() throws IOException {

		try {

			map.lastSavedState = new MapgenEntry(map.currentMap);

		} catch (Exception e) {
			log.error("Error while writing to map file '" + path.toAbsolutePath() + "':", e);
		}

	}

}
