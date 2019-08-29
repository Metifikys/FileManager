package metifikys.list.controller

import metifikys.list.ListProcessor
import metifikys.list.controller.comands.FileCommand
import metifikys.state.Configurable

import java.nio.file.Path
import java.nio.file.Paths
import java.util.prefs.Preferences

class ListsController private constructor(private val left: ListProcessor, private val right: ListProcessor) : Configurable {
    private lateinit var current: ListProcessor

    fun processCommand(fileCommand: FileCommand) {

        val to = if (left === current) right else left
        fileCommand.process(current, to)

        left.refreshByPath()
        right.refreshByPath()
    }

    fun changeFocus() {

        current = if (left === current) right else left
        current.setFocusOnList()
    }

    fun focusOnComboBoxLeft() {
        left.setFocusOnComboBox()
    }

    fun focusOnComboBoxRight() {
        right.setFocusOnComboBox()
    }


    override fun save(prefs: Preferences) {
        left.save(prefs)
        right.save(prefs)
    }

    override fun load(prefs: Preferences) {
        right.load(prefs)
        left.load(prefs)
    }

    companion object {

        fun of(left: ListProcessor, right: ListProcessor): ListsController {
            val listsController = ListsController(left, right)

            left.focusProperty.addListener { _, _, _ -> listsController.current = left }
            right.focusProperty.addListener { _, _, _ -> listsController.current = right }
            return listsController
        }
    }
}