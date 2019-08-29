package metifikys.state;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Created by Metifikys on 16.09.2017.
 */
public class StateController {

    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(StateController.class);
    private static List<Configurable> configurables = new ArrayList<>();

    public static void addElement(Configurable configurable){
        configurables.add(configurable);
        configurable.load(PREFERENCES);
    }


    public static void saveElements(){
        configurables.forEach(conf -> conf.save(PREFERENCES));
    }
}