package pa1;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Imports files and directories into our index.
 *
 * @author Klaus Berberich (klaus.berberich@htwsaar.de)
 * @author Benjamin Lautenberg
 * @author Phillip Persch
 * @author Thomas Spanier
 */
public class Importer {

	static final String dbPath = "nyt.sqlite";
	
	/**
	 * imports all files in directory
	 * @param root
	 */
	private void importDirectory(File root) {
		LinkedList<File> files = new LinkedList<File>();
		File db = new File(dbPath);
		Connection conn;
		Parser parser = new Parser();
		PreparedStatement insertIntoDocs = null;
		PreparedStatement insertIntoTfs = null;
		int i = 0;
		
		traverse(root, files);
		try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + db);
            conn.setAutoCommit(false);
            
            insertIntoDocs = conn.prepareStatement("INSERT INTO docs (did, title, url) VALUES (?, ?, ?)");
            insertIntoTfs = conn.prepareStatement("INSERT INTO tfs (did, term, tf) VALUES (?, ?, ?)");
            
            for(File file: files) {
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
    			
    			if(++i % 10 == 0) {
    				System.out.println("i: " + i);
    				insertIntoDocs.executeBatch();
    				insertIntoTfs.executeBatch();
    			}
    		}
            //Execute rest
            if(i % 10 != 0) {
            	insertIntoDocs.executeBatch();
    			insertIntoTfs.executeBatch();
            }
            
            String dls = "CREATE TABLE dls AS SELECT did, SUM(tf) AS len FROM tfs GROUP BY did;";
            PreparedStatement createDls = conn.prepareStatement(dls);
            createDls.execute();
            
            String dfs = "CREATE TABLE dfs AS SELECT term, COUNT(did) AS df FROM tfs GROUP BY term;"; 
            PreparedStatement createDfs = conn.prepareStatement(dfs);
            createDfs.execute();
            
            String d = "CREATE TABLE d AS SELECT COUNT(*) as size FROM docs;";
            PreparedStatement createD = conn.prepareStatement(d);
            createD.execute();

            conn.commit();
            conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	/**
	 * clears database
	 * clears all entries in docs and tfs
	 * drops databases dls, dfs and d
	 */
	private void clearAll() {
		File db = new File(dbPath);
		Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + db);
			PreparedStatement deleteFromDocs = conn.prepareStatement("DELETE FROM docs");
			PreparedStatement deleteFromTfs = conn.prepareStatement("DELETE FROM tfs");
			PreparedStatement deleteFromDls = conn.prepareStatement("DROP TABLE dls");
			PreparedStatement deleteFromDfs = conn.prepareStatement("DROP TABLE dfs");
			PreparedStatement deleteFromD = conn.prepareStatement("DROP TABLE d");
	        conn.setAutoCommit(false);
	        
	        deleteFromDocs.execute();
	        deleteFromTfs.execute();
	        deleteFromDls.execute();
	        deleteFromDfs.execute();
	        deleteFromD.execute();
	        conn.commit();
	        conn.close();
		} catch (SQLException e) {
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

    /**
     * Starts the program
     * Input c to clear database, i for insert values into database or exit to exit program
     */
    private void start() {
    	final String MSG_USAGE = "c for clear, i for insert, exit to exit";
    	final String MSG_START = "Input: ";
    	String nytPath = "nyt/data";
    	 
        Scanner in = new Scanner(System.in);
		while (true) {
			System.out.print(MSG_START);
			String tmp = in.nextLine();
			
			switch(tmp) {
				case "c":
					clearAll();
		        	System.out.println("cleared db");
					break;
				case "i":
					importDirectory(new File(nytPath));
					break;
				case "exit":
					in.close();
					System.exit(0);
					break;
				default:
					System.out.println(MSG_USAGE);
			}
	        
		}
    }
    
    
    public static void main(String[] args) {
    	new Importer().start();
    }

}
