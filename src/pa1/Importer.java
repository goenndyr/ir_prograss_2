package pa1;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Imports files and directories into our index.
 *
 * @author Klaus Berberich (klaus.berberich@htwsaar.de)
 */
public class Importer {

    /**
     * Traverses the given root recursively, collects all files with suffix
     * .xml, and appends them to the given list.
     *
     * @param root Root to be traversed.
     * @param files List to which found files are appended.
     */
    private void traverse(File root, List<File> files) {
        if (root.isFile()) {
            if (root.getAbsolutePath().endsWith(".xml")) {
                files.add(root);
            }
        } else {
            for (File file : root.listFiles()) {
                traverse(file, files);
            }
        }
    }

    public void importFile(File file) {
        LinkedList<File> xmlFiles = new LinkedList<File>();
        traverse(file, xmlFiles);

        // print file names and sizes
        for(File xmlFile : xmlFiles) {
            System.out.println(xmlFile.getAbsolutePath() + " " + xmlFile.length());
        }
    }

    public static void main(String[] args) {
        Importer importer = new Importer();
        importer.importFile(new File("/Users/kberberi/Downloads/nyt/data"));
    }

}
