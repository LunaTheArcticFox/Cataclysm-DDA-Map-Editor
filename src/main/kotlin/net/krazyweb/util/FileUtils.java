package net.krazyweb.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {

	public static List<Path> listFiles(final Path path) throws IOException {
		List<Path> paths = new ArrayList<>();
		listFiles(path, paths);
		return paths;
	}

	public static void listFiles(final Path path, final List<Path> paths) throws IOException {
		if (Files.isDirectory(path)) {
			List<Path> files = Files.list(path).collect(Collectors.toList());
			for (Path path1 : files) {
				listFiles(path1, paths);
			}
		} else {
			paths.add(path);
		}
	}

}
