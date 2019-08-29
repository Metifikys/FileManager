package metifikys

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.stage.Stage
import metifikys.list.ListProcessor
import metifikys.list.controller.ListsController
import metifikys.list.controller.comands.FileCommand
import metifikys.state.StateController
import org.apache.commons.lang3.tuple.MutablePair

import java.io.File
import java.nio.file.Path
import java.util.ArrayList
import java.util.prefs.Preferences

class MainController {

    private var listsController: ListsController? = null

    @FXML
    private lateinit var leftList: ListView<MutablePair<String, Path>>
    @FXML
    private lateinit var leftText: TextField
    @FXML
    private lateinit var leftDrivers: ComboBox<String>

    @FXML
    private lateinit var rightList: ListView<MutablePair<String, Path>>
    @FXML
    private lateinit var rightText: TextField
    @FXML
    private lateinit var rightDrivers: ComboBox<String>


    @FXML
    private lateinit var copyButton: Button
    @FXML
    private lateinit var cutButton: Button
    @FXML
    private lateinit var deleteButton: Button
    @FXML
    private lateinit var mkFolderButton: Button

    @FXML
    fun initialize() {

        //        Preferences prefs = Preferences.userRoot().node("metifikys.FileManager");
        //        prefs.put("left", "left");
        //        System.out.println(prefs.get("left", ""));

        val names = ArrayList<String>()
        for (path in File.listRoots())
            names.add(path.toString())

        val strings = FXCollections.observableArrayList(names)
        leftDrivers.setItems(strings)
        rightDrivers.setItems(strings)

        // todo from config file
        leftDrivers.selectionModel.select(0)
        rightDrivers.selectionModel.select(0)

        listsController = ListsController.of(
                ListProcessor.of(leftList, leftText, leftDrivers),
                ListProcessor.of(rightList, rightText, rightDrivers)
        )
        StateController.addElement(listsController!!)

        println("initialized")


        // todo from config file
        setOnClickCommandInButton(copyButton, FileCommand.COPY)
        setOnClickCommandInButton(cutButton, FileCommand.CUT)
        setOnClickCommandInButton(deleteButton, FileCommand.DELETE)
        setOnClickCommandInButton(mkFolderButton, FileCommand.CREATE_FOLDER)
    }


    private fun setOnClickCommandInButton(button: Button, command: FileCommand) {
        button.setOnMouseClicked { event -> listsController!!.processCommand(command) }
    }

    fun setScene(scene: Scene) {

        scene.setOnKeyPressed { event ->

            // todo from config file
            when (event.code) {
                KeyCode.F5 -> listsController!!.processCommand(FileCommand.COPY)
                KeyCode.F6 -> listsController!!.processCommand(FileCommand.CUT)
                KeyCode.F7 -> listsController!!.processCommand(FileCommand.CREATE_FOLDER)
                KeyCode.F8 -> listsController!!.processCommand(FileCommand.DELETE)
            }

        }


        val leftComb = KeyCodeCombination(KeyCode.F1, KeyCombination.ALT_DOWN)
        val rightComb = KeyCodeCombination(KeyCode.F2, KeyCombination.ALT_DOWN)

        scene.setOnKeyReleased { event ->

            if (leftComb.match(event)) {
                listsController!!.focusOnComboBoxLeft()
            } else if (rightComb.match(event)) {
                listsController!!.focusOnComboBoxRight()
            } else {
                when (event.code) {
                    KeyCode.TAB -> listsController!!.changeFocus()
                }
            }
        }
    }
}
