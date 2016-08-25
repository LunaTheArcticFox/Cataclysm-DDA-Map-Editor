package net.krazyweb.cataclysm.mapeditor.app

import javafx.scene.Scene
import net.krazyweb.cataclysm.mapeditor.view.MainView
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import tornadofx.App
import tornadofx.View

class Launcher : App(MainView::class) {

	val log: Logger = LogManager.getLogger(Launcher::class.java)

	override fun createPrimaryScene(view: View) = Scene(view.root, 1100.0, 900.0)

}
