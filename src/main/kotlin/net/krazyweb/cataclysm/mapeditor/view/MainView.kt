package net.krazyweb.cataclysm.mapeditor.view

import javafx.scene.control.CheckMenuItem
import javafx.scene.control.MenuItem
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import tornadofx.View

class MainView : View("Cataclysm Map Editor - Untitled*") {

    override val root: BorderPane by fxml("/fxml/editorMain.fxml")

    val tilePickerPanel: VBox by fxid()
    val mapContainer: VBox by fxid()

    val undoButton: MenuItem by fxid()
    val redoButton: MenuItem by fxid()

    val showGridButton: CheckMenuItem by fxid()
    val showGroupsButton: CheckMenuItem by fxid()

    init {

    }

    fun newProject() {
        println("TEST")
    }

    fun newMap() {

    }

    fun openFile() {

    }

    fun saveFile() {

    }

    fun saveFileAs() {

    }

    fun revertFile() {

    }

    fun showOptions() {

    }

    fun exit() {

    }

    fun undo() {

    }

    fun redo() {

    }

    fun editMapProperties() {

    }

    fun editDefinitions() {

    }

    fun toggleGrid() {

    }

    fun toggleGroups() {

    }

    fun showPlaceGroupHelp() {

    }

}
