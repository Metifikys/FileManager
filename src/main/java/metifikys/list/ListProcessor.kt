package metifikys.list;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import metifikys.state.Configurable;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import static java.nio.file.Files.isDirectory;

public class ListProcessor implements Configurable {

    private ListView<MutablePair<String, Path>> pathListView;
    private TextField text;
    private ComboBox<String> drivers;

    private Path currentPath;

    private ListProcessor(ListView<MutablePair<String, Path>> pathListView, TextField label, ComboBox<String> drivers) {

        this.pathListView = pathListView;
        this.text = label;

        pathListView.setCellFactory(list -> new AttachmentListCell());
        pathListView.setOnMouseClicked(val -> {

            if (val.getClickCount() == 2){

                Path selectedItem = pathListView.getSelectionModel().getSelectedItem().getRight();
                if (selectedItem.toFile().isDirectory()){
                    currentPath = selectedItem;
                    refreshByPath();
                }
            }

        });

//        currentPath = Paths.get(drivers.getSelectionModel().getSelectedItem());
        drivers.valueProperty().addListener(
                event -> {
                    currentPath = Paths.get(drivers.getSelectionModel().getSelectedItem());
                    refreshByPath();
                }
        );

        this.drivers = drivers;
    }

    public static ListProcessor of(ListView<MutablePair<String, Path>> pathListView, TextField label, ComboBox<String> drivers) {
        return new ListProcessor(pathListView, label, drivers);
    }

    public ListProcessor refreshByPath() {
        text.setText(currentPath.toString());

        try {
            pathListView.getItems().clear();

            List<MutablePair<String, Path>> collect = getFilesList();
            Path parent = currentPath.getParent();

            if (parent != null) {
                collect.add(0, MutablePair.of("<- Back", parent));
            }
            
            pathListView.setItems(FXCollections.observableArrayList(collect));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    private List<MutablePair<String, Path>> getFilesList() throws IOException {

        return Files.list(currentPath)
                .sorted( // first directories, second files
                        (p1, p2) -> {

                            boolean directory1 = isDirectory(p1);
                            boolean directory2 = isDirectory(p2);

                            if (directory1 && directory2 || (!directory1 && !directory2)){
                                return p1.compareTo(p2);
                            }
                            else {
                                return directory2 ? 1 : -1;
                            }
                        }
                )
                .map(path -> MutablePair.of(path.getFileName().toString(), path))
                .collect(Collectors.toList());
    }

    public ReadOnlyObjectProperty<MutablePair<String, Path>> getFocusProperty(){
        return pathListView.getSelectionModel().selectedItemProperty();
    }

    public Path getCurrentPath(){
        return currentPath;
    }

    public Path getSelected(){
        return pathListView.getSelectionModel().getSelectedItem().getRight();
    }

    public void setFocusOnList(){

        int selectedIndex = pathListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0){
            selectedIndex = 0;
        }

        pathListView.getFocusModel().focus(selectedIndex);
        pathListView.requestFocus();
    }

    public void setFocusOnComboBox(){
        drivers.requestFocus();
    }

    @Override
    public void save(Preferences prefs) {
        prefs.put(pathListView.getId(), currentPath.toString());
    }

    @Override
    public void load(Preferences prefs) {

        String val = prefs.get(pathListView.getId(), null);
        currentPath = val == null
                ? Paths.get(drivers.getSelectionModel().getSelectedItem())
                : Paths.get(val);

        refreshByPath();
    }
}