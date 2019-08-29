package metifikys.list

import javafx.embed.swing.SwingFXUtils
import javafx.scene.control.ListCell
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.lang3.tuple.MutablePair

import javax.swing.*
import javax.swing.filechooser.FileSystemView
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.util.HashMap

class AttachmentListCell : ListCell<MutablePair<String, Path>>() {

    public override fun updateItem(item: MutablePair<String, Path>?, empty: Boolean) {

        super.updateItem(item, empty)
        if (empty) {
            graphic = null
            text = null
        } else {
            val fxImage = getFileIcon(item?.value.toString())
            val imageView = ImageView(fxImage)
            graphic = imageView
            text = item?.key
        }
    }

    private interface IconGetter {
        fun getIconFroFile(file: File): javax.swing.Icon
    }

    private class WindowsIconGetter : IconGetter {

        private val view = FileSystemView.getFileSystemView()

        override fun getIconFroFile(file: File): Icon {
            return view.getSystemIcon(file)
        }
    }

    private class MacOsIconGetter : IconGetter {

        private val fc = javax.swing.JFileChooser()

        override fun getIconFroFile(file: File): Icon {
            return fc.ui.getFileView(fc).getIcon(file)
        }
    }

    companion object {

        private var iconGetter: IconGetter? = null
        private val mapOfFileExtToSmallIcon = HashMap<String, Image>()

        init {
            if (SystemUtils.IS_OS_MAC) {
                iconGetter = MacOsIconGetter()
            } else if (SystemUtils.IS_OS_WINDOWS) {
                iconGetter = WindowsIconGetter()
            }
        }

        private fun getFileExt(fname: String): String {
            var ext = "."
            val p = fname.lastIndexOf('.')
            if (p >= 0) {
                ext = fname.substring(p)
            }
            return ext
        }

        private fun getFileIcon(fname: String): Image? {

            val ext = getFileExt(fname)
            var fileIcon: Image? = mapOfFileExtToSmallIcon[ext]
            if (fileIcon == null) {
                var jswingIcon: javax.swing.Icon? = null

                val file = File(fname)
                if (file.exists()) {
                    jswingIcon = iconGetter!!.getIconFroFile(file)
                } else {
                    var tempFile: File? = null
                    try {
                        tempFile = File.createTempFile("icon", ext)
                        jswingIcon = iconGetter!!.getIconFroFile(tempFile)
                    } catch (ignored: IOException) {
                        // Cannot create temporary file.
                    } finally {
                        tempFile?.delete()
                    }
                }

                if (jswingIcon != null) {
                    fileIcon = jswingIconToImage(jswingIcon)
                    mapOfFileExtToSmallIcon[ext] = fileIcon
                }
            }

            return fileIcon
        }

        private fun jswingIconToImage(jswingIcon: javax.swing.Icon): Image {
            val bufferedImage = BufferedImage(jswingIcon.iconWidth, jswingIcon.iconHeight, BufferedImage.TYPE_INT_ARGB)

            jswingIcon.paintIcon(null, bufferedImage.graphics, 0, 0)
            return SwingFXUtils.toFXImage(bufferedImage, null)
        }
    }
}
