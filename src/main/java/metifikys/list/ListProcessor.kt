package metifikys.list

import javafx.beans.property.ReadOnlyObjectProperty
import javafx.collections.FXCollections
import javafx.scene.control.ComboBox
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import metifikys.state.Configurable
import org.apache.commons.lang3.tuple.MutablePair
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Files.isDirectory
import java.nio.file.Path
import java.nio.file.Paths
import java.util.prefs.Preferences
import kotlin.streams.asSequence

class ListProcessor private constructor(private val pathListView: ListView<MutablePair<String, Path>>,
                                        private val text: TextField,
                                        private val drivers: ComboBox<String>) : Configurable {

    var currentPath: Path? = null
        private set


    // first directories, second files
    @Throws(IOException::class)
    private fun getFilesList(): MutableList<MutablePair<String, Path>> {

        return try {

            Files.list(currentPath)
                    .asSequence()
                    .sortedWith(compareBy { !isDirectory(it) })
                    .map { path -> MutablePair.of(path.fileName.toString(), path) }
                    .toMutableList()
        }
        catch (ex: java.nio.file.AccessDeniedException){
            // fix path in label
            currentPath = currentPath?.parent
            if (currentPath != null)
                getFilesList()
            else
                TODO("go to normal folder")
        }
    }

    val focusProperty: ReadOnlyObjectProperty<MutablePair<String, Path>>
        get() = pathListView.selectionModel.selectedItemProperty()

    val selected: Path
        get() = pathListView.selectionModel.selectedItem.getRight()

    init {

        pathListView.setCellFactory { list -> AttachmentListCell() }
        pathListView.setOnMouseClicked { `val` ->

            if (`val`.clickCount == 2) {

                val selectedItem = pathListView.selectionModel.selectedItem.getRight()
                if (selectedItem.toFile().isDirectory) {
                    currentPath = selectedItem
                    refreshByPath()
                }
            }

        }

        //        currentPath = Paths.get(drivers.getSelectionModel().getSelectedItem());
        drivers.valueProperty().addListener { event ->
            currentPath = Paths.get(drivers.selectionModel.selectedItem)
            refreshByPath()
        }
    }

    fun refreshByPath(): ListProcessor {
        text.text = currentPath!!.toString()

        try {
            pathListView.items.clear()

            val collect = getFilesList()
            val parent = currentPath!!.parent

            if (parent != null) {
                collect.add(0, MutablePair.of("<- Back", parent))
            }

            pathListView.setItems(FXCollections.observableArrayList(collect))

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return this
    }

    fun setFocusOnList() {

        var selectedIndex = pathListView.selectionModel.selectedIndex
        if (selectedIndex < 0) {
            selectedIndex = 0
        }

        pathListView.focusModel.focus(selectedIndex)
        pathListView.requestFocus()
    }

    fun setFocusOnComboBox() {
        drivers.requestFocus()
    }

    override fun save(prefs: Preferences) {
        prefs.put(pathListView.id, currentPath!!.toString())
    }

    override fun load(prefs: Preferences) {

        val prefsVal = prefs.get(pathListView.id, null)
        currentPath = if (prefsVal == null)
            Paths.get(drivers.selectionModel.selectedItem)
        else
            Paths.get(prefsVal)

        refreshByPath()
    }

    companion object {

        fun of(pathListView: ListView<MutablePair<String, Path>>, label: TextField, drivers: ComboBox<String>): ListProcessor {
            return ListProcessor(pathListView, label, drivers)
        }
    }
}