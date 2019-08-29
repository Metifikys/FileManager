package metifikys.state

import java.util.ArrayList
import java.util.prefs.Preferences

/**
 * Created by Metifikys on 16.09.2017.
 */
object StateController {

    private val PREFERENCES = Preferences.userNodeForPackage(StateController::class.java!!)
    private val configurables = ArrayList<Configurable>()

    fun addElement(configurable: Configurable) {
        configurables.add(configurable)
        configurable.load(PREFERENCES)
    }


    fun saveElements() {
        configurables.forEach { conf -> conf.save(PREFERENCES) }
    }
}