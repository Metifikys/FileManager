package metifikys.list.controller.comands;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import metifikys.list.ListProcessor;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

// todo progress for File commands
// todo FilesHelper
public enum FileCommand {


    COPY{
        @Override
        public void process(ListProcessor from, ListProcessor to) {

            try {
                File srcFile = from.getSelected().toFile();

                if (srcFile.isFile())
                    FileUtils.copyFileToDirectory(srcFile, to.getCurrentPath().toFile());
                else
                    FileUtils.copyDirectoryToDirectory(srcFile, to.getCurrentPath().toFile());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    },
    CUT{
        @Override
        public void process(ListProcessor from, ListProcessor to) {

            try {
                File srcFile = from.getSelected().toFile();

                if (srcFile.isFile()) {
                    FileUtils.copyFileToDirectory(srcFile, to.getCurrentPath().toFile());
                    FileUtils.deleteQuietly(srcFile);
                }
                else {
                    FileUtils.copyDirectoryToDirectory(srcFile, to.getCurrentPath().toFile());
                    FileUtils.deleteDirectory(srcFile);
                }


            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }
    },
    DELETE{
        @Override
        public void process(ListProcessor from, ListProcessor to) {

            File file = from.getSelected().toFile();

            new Alert(Alert.AlertType.WARNING,
                    "Do you want to delete " + file.getName() + "?",
                    ButtonType.CANCEL, ButtonType.OK)
                    .showAndWait()
                    .ifPresent(

                    rs ->{
                        if (rs.getButtonData().isCancelButton()){
                            return;
                        }
                        else {
                            if (file.isFile()) {
                                file.delete();
                            }
                            else {
                                try {
                                    FileUtils.deleteDirectory(file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
            );
        }
    },
    CREATE_FOLDER{
        @Override
        public void process(ListProcessor from, ListProcessor to) {

            TextInputDialog inputDialog = new TextInputDialog("");
            inputDialog.setTitle("New folder");
//            inputDialog.setContentText("Input new folder name");
            inputDialog.setHeaderText("Input new folder name");

            inputDialog.showAndWait().ifPresent(
                    rs -> {
                        if (!rs.isEmpty()){
                            File file = new File(from.getCurrentPath().toFile(), rs);
                            if (file.exists()){
                                // todo message
                                System.out.println("is exist");
                            }
                            else {
                                file.mkdirs();
                            }
                        }
                    }
            );
        }
    };

    public abstract void process(ListProcessor from, ListProcessor to);
}