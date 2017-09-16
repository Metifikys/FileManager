package metifikys.list.controller;

import metifikys.list.ListProcessor;
import metifikys.list.controller.comands.FileCommand;
import metifikys.state.Configurable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class ListsController implements Configurable {

    private ListProcessor left;
    private ListProcessor right;
    private ListProcessor current;

    private ListsController(ListProcessor left, ListProcessor right) {
        this.left = left;
        this.right = right;
    }

    public static ListsController of(ListProcessor left, ListProcessor right){
        ListsController listsController = new ListsController(left, right);

        left .getFocusProperty().addListener((observable, oldValue, newValue) -> listsController.current = left);
        right.getFocusProperty().addListener((observable, oldValue, newValue) -> listsController.current = right);
        return listsController;
    }

    public void processCommand(FileCommand fileCommand){

        ListProcessor to =  left == current ? right : left;
        fileCommand.process(current, to);

        left.refreshByPath();
        right.refreshByPath();
    }

    public void changeFocus(){

        current = left == current ? right : left;
        current.setFocusOnList();
    }

    public void focusOnComboBoxLeft(){
        left.setFocusOnComboBox();
    }

    public void focusOnComboBoxRight(){
        right.setFocusOnComboBox();
    }


    @Override
    public void save(Preferences prefs) {
        left.save(prefs);
        right.save(prefs);
    }

    @Override
    public void load(Preferences prefs) {
        right.load(prefs);
        left.load(prefs);
    }
}