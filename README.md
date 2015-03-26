# Cataclysm: DDA Map Editor

This project is intended to create a simple, but powerful map editor to enable quick content creation for Cataclysm: Dark Days Ahead.

It is currently in alpha development. More information will be added soon.

Uses JavaFX 8.

## Running the Program

Running the program requires Java 8u40 or newer be installed.

Copy a few files from your Cataclysm game directory to "Sample Data/" in the following configuration to the folder from which you're running the program:
```
Sample Data/
	tileset/
		fallback.png
		tile_config.json
		tiles.png
		tileset.txt
	ags_terrain.json
	furniture.json
	terrain.json
```

Start the program from your IDE or double click the jar file.

## Building with Maven

Running ```mvn install``` will create a runnable jar file with dependencies. Be sure to copy the above ```Sample Data``` folder to wherever you're running the jar from.

Building this project requires JDK 8u40 or newer due to use of the new Dialogs API in JavaFX.
