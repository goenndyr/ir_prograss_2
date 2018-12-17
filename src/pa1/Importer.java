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

	static final String dbPath = "nyt.sqlite";
	
	private void importDirectory(File root) {
		LinkedList<File> files = new LinkedList<File>();
		File db = new File(dbPath);
		Connection conn;
		Parser parser = new Parser();
		PreparedStatement insertIntoDocs = null;
		PreparedStatement insertIntoTfs = null;
		int batchCount = 0;
		
		traverse(root, files);
		try {
            //Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + db);
            conn.setAutoCommit(false);
            
            insertIntoDocs = conn.prepareStatement("INSERT INTO docs (did, title, url) VALUES (?, ?, ?)");
            insertIntoTfs = conn.prepareStatement("INSERT INTO tfs (did, term, tf) VALUES (?, ?, ?)");
            
            for(File file: files) {
            	batchCount = 0;
    			Document doc = parser.parse(file);
    			System.out.println(doc.getId());
    			
    			//Insert into docs
    			insertIntoDocs.setLong(1, doc.getId());
    			insertIntoDocs.setString(2, doc.getTitle());
    			insertIntoDocs.setString(3, doc.getURL());
    			insertIntoDocs.addBatch();
    			
    			//insert into tfs-HashMap
    			HashMap<String, Integer> tfs = new HashMap<String, Integer>();
    			for(String c : doc.getContent()) {
    				if(tfs.containsKey(c)) {
    					tfs.put(c, tfs.get(c) + 1);
    				} else {
    					tfs.put(c, 0);
    				}
    			}
    			
    			//insert into tfsTable
    			for (String c : tfs.keySet()) {
                    insertIntoTfs.setLong(1, doc.getId());
                    insertIntoTfs.setString(2, c);
                    insertIntoTfs.setInt(3, tfs.get(c));
                    insertIntoTfs.addBatch();
                }
    			
    			if(++batchCount % 10 == 0) {
    				insertIntoDocs.executeBatch();
    				insertIntoTfs.executeBatch();
    			}
    		}
            /*
            if(batchCount % 10 != 0) {
            	insertIntoDocs.executeBatch();
    			insertIntoTfs.executeBatch();
            }*/
            
    		
            conn.commit();
            conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	private void deleteAll() {
		
		File db = new File(dbPath);
		Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + db);
			PreparedStatement deleteFromDocs = conn.prepareStatement("DELETE FROM docs");
			PreparedStatement deleteFromTfs = conn.prepareStatement("DELETE FROM tfs");
	        conn.setAutoCommit(false);
	        
	        deleteFromDocs.execute();
	        deleteFromTfs.execute();
	        conn.commit();
	        conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        
	}
	
	
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
    	System.out.println(file.getAbsolutePath());
    	LinkedList<File> xmlFiles = new LinkedList<File>();
        traverse(file, xmlFiles);

        // print file names and sizes
        for(File xmlFile : xmlFiles) {
            System.out.println(xmlFile.getAbsolutePath() + " " + xmlFile.length());
        }
    }

    public static void main(String[] args) {
    	
    	String nytPath = "nyt/data";
    	
    	/*File tmp = new File(dbPath);
    	System.out.println(tmp.getAbsolutePath());
    	*/
        Importer importer = new Importer();
        
        if(args.length > 0 && args[0].equals("-c")) {
        	importer.deleteAll();
    	} else {
    		importer.importDirectory(new File(nytPath));
    	}
    }

}
