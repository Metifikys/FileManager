package metifikys.state

import java.util.prefs.Preferences

/**
 * Created by Metifikys on 16.09.2017.
 */
interface Configurable {

    fun save(prefs: Preferences)
    fun load(prefs: Preferences)
}
