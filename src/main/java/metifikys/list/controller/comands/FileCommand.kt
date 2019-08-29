package metifikys.list.controller.comands

import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.TextInputDialog
import metifikys.list.ListProcessor
import org.apache.commons.io.FileUtils

import javax.swing.*
import java.io.File
import java.io.IOException
import java.nio.file.Files

// todo progress for File commands
// todo FilesHelper
enum class FileCommand {


    COPY {
        override fun process(from: ListProcessor, to: ListProcessor) {

            try {
                val srcFile = from.selected.toFile()

                if (srcFile.isFile)
                    FileUtils.copyFileToDirectory(srcFile, to.currentPath!!.toFile())
                else
                    FileUtils.copyDirectoryToDirectory(srcFile, to.currentPath!!.toFile())
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    },
    CUT {
        override fun process(from: ListProcessor, to: ListProcessor) {

            try {
                val srcFile = from.selected.toFile()

                if (srcFile.isFile) {
                    FileUtils.copyFileToDirectory(srcFile, to.currentPath!!.toFile())
                    FileUtils.deleteQuietly(srcFile)
                } else {
                    FileUtils.copyDirectoryToDirectory(srcFile, to.currentPath!!.toFile())
                    FileUtils.deleteDirectory(srcFile)
                }


            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    },
    DELETE {
        override fun process(from: ListProcessor, to: ListProcessor) {

            val file = from.selected.toFile()

            Alert(Alert.AlertType.WARNING,
                    "Do you want to delete " + file.name + "?",
                    ButtonType.CANCEL, ButtonType.OK)
                    .showAndWait()
                    .ifPresent {
                        if (!it.getButtonData().isCancelButton()){
                            if (file.isFile) {
                                file.delete()
                            }
                            else {
                                try {
                                    Files.delete(file.toPath())
                                }
                                catch (ex: IOException) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
        }
    },
    CREATE_FOLDER {
        override fun process(from: ListProcessor, to: ListProcessor) {

            val inputDialog = TextInputDialog("")
            inputDialog.title = "New folder"
            //            inputDialog.setContentText("Input new folder name");
            inputDialog.headerText = "Input new folder name"

            inputDialog.showAndWait().ifPresent { rs ->
                if (!rs.isEmpty()) {
                    val file = File(from.currentPath!!.toFile(), rs)
                    if (file.exists()) {
                        // todo message
                        println("is exist")
                    } else {
                        file.mkdirs()
                    }
                }
            }
        }
    };

    abstract fun process(from: ListProcessor, to: ListProcessor)
}