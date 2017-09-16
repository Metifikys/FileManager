package metifikys.list;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AttachmentListCell extends ListCell<MutablePair<String, Path>> {

    private static IconGetter iconGetter;
    private static Map<String, Image> mapOfFileExtToSmallIcon = new HashMap<String, Image>();

    static {
        if (SystemUtils.IS_OS_MAC){
            iconGetter = new MacOsIconGetter();
        }
        else if (SystemUtils.IS_OS_WINDOWS){
            iconGetter = new WindowsIconGetter();
        }
    }


    @Override
    public void updateItem(MutablePair<String, Path> item, boolean empty) {

        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
            setText(null);
        }
        else {
            Image fxImage = getFileIcon(item.getValue().toString());
            ImageView imageView = new ImageView(fxImage);
            setGraphic(imageView);
            setText(item.getKey());
        }
    }

    private static String getFileExt(String fname) {
        String ext = ".";
        int p = fname.lastIndexOf('.');
        if (p >= 0) {
            ext = fname.substring(p);
        }
        return ext;
    }

    private static Image getFileIcon(String fname) {

        final String ext = getFileExt(fname);
        Image fileIcon = mapOfFileExtToSmallIcon.get(ext);
        if (fileIcon == null) {
            javax.swing.Icon jswingIcon = null;

            File file = new File(fname);
            if (file.exists()) {
                jswingIcon = iconGetter.getIconFroFile(file);
            }
            else {
                File tempFile = null;
                try {
                    tempFile = File.createTempFile("icon", ext);
                    jswingIcon = iconGetter.getIconFroFile(tempFile);
                }
                catch (IOException ignored) {
                    // Cannot create temporary file.
                }
                finally {
                    if (tempFile != null)
                        tempFile.delete();
                }
            }

            if (jswingIcon != null) {
                fileIcon = jswingIconToImage(jswingIcon);
                mapOfFileExtToSmallIcon.put(ext, fileIcon);
            }
        }

        return fileIcon;
    }

    private static Image jswingIconToImage(javax.swing.Icon jswingIcon) {
        BufferedImage bufferedImage =
                new BufferedImage(jswingIcon.getIconWidth(), jswingIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

        jswingIcon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    private interface IconGetter{
        javax.swing.Icon getIconFroFile(File file);
    }

    private static class WindowsIconGetter implements IconGetter{

        private final FileSystemView view = FileSystemView.getFileSystemView();

        @Override
        public Icon getIconFroFile(File file) {
            return view.getSystemIcon(file);
        }
    }

    private static class MacOsIconGetter implements IconGetter{

        private final javax.swing.JFileChooser fc = new javax.swing.JFileChooser();

        @Override
        public Icon getIconFroFile(File file) {
           return fc.getUI().getFileView(fc).getIcon(file);
        }
    }
}
