package metifikys

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import metifikys.state.StateController

// TODO Spring DI
class Start : Application() {

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        val fxmlFile = "/fxml/hello.fxml"
        val loader = FXMLLoader()
        val root = loader.load<Parent>(javaClass.getResourceAsStream(fxmlFile))
        val scene = Scene(root)

        loader.getController<MainController>()
                .setScene(scene)

        primaryStage.setOnCloseRequest { event -> StateController.saveElements() }
        primaryStage.title = "FileManager"
        primaryStage.scene = scene
        primaryStage.show()
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            launch(Start::class.java, *args)
        }
    }
}