package metifikys;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import metifikys.list.ListProcessor;
import metifikys.list.controller.ListsController;
import metifikys.list.controller.comands.FileCommand;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    private ListsController listsController;

    @FXML private ListView<MutablePair<String, Path>> leftList;
    @FXML private TextField leftText;
    @FXML private ComboBox<String> leftDrivers;

    @FXML private ListView<MutablePair<String, Path>> rightList;
    @FXML private TextField rightText;
    @FXML private ComboBox<String> rightDrivers;


    @FXML private Button copyButton;
    @FXML private Button cutButton;
    @FXML private Button deleteButton;
    @FXML private Button mkFolderButton;

    @FXML
    private void initialize() {

//        FileSystemView fsv = FileSystemView.getFileSystemView();

        List<String> names = new ArrayList<>();
        for(File path : File.listRoots())
            names.add(path.toString());

        ObservableList<String> strings = FXCollections.observableArrayList(names);
        leftDrivers.setItems(strings);
        rightDrivers.setItems(strings);

        // todo from config file
        leftDrivers.getSelectionModel().select(0);
        rightDrivers.getSelectionModel().select(0);

        listsController = ListsController.of(
                ListProcessor.of(leftList, leftText, leftDrivers),
                ListProcessor.of(rightList, rightText, rightDrivers)
        );

        System.out.println("initialized");


        // todo from config file
        setOnClickCommandInButton(copyButton,     FileCommand.COPY);
        setOnClickCommandInButton(cutButton,      FileCommand.CUT);
        setOnClickCommandInButton(deleteButton,   FileCommand.DELETE);
        setOnClickCommandInButton(mkFolderButton, FileCommand.CREATE_FOLDER);
    }


    private void setOnClickCommandInButton(Button button, FileCommand command){
        button.setOnMouseClicked(event -> listsController.processCommand(command));
    }

    public void setScene(Scene scene) {

        scene.setOnKeyPressed(event -> {

            // todo from config file
            switch (event.getCode()){
                case F5: listsController.processCommand(FileCommand.COPY);   break;
                case F6: listsController.processCommand(FileCommand.CUT);    break;
                case F7: listsController.processCommand(FileCommand.CREATE_FOLDER); break;
                case F8: listsController.processCommand(FileCommand.DELETE); break;
            }

        });


        final KeyCombination leftComb  = new KeyCodeCombination(KeyCode.F1, KeyCombination.ALT_DOWN);
        final KeyCombination rightComb = new KeyCodeCombination(KeyCode.F2, KeyCombination.ALT_DOWN);

        scene.setOnKeyReleased(event -> {

            if (leftComb.match(event)){
                listsController.focusOnComboBoxLeft();
            }
            else if (rightComb.match(event)){
                listsController.focusOnComboBoxRight();
            }
            else {
                switch (event.getCode()) {
                    case TAB:
                        listsController.changeFocus();
                        break;
                }
            }
        });
    }
}
