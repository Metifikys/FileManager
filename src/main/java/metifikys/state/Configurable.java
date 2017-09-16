package metifikys.state;

import java.util.prefs.Preferences;

/**
 * Created by Metifikys on 16.09.2017.
 */
public interface Configurable {

    void save(Preferences prefs);
    void load(Preferences prefs);
}
