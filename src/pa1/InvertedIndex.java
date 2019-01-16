package pa1;

import java.io.File;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class InvertedIndex {

	static final String dbPath = "nyt.sqlite";
	
    private Connection conn;
    private PreparedStatement selectTFs;
    private PreparedStatement selectDF;
    private PreparedStatement selectSize;
    private PreparedStatement selectLength;

    public InvertedIndex() {
    	try {

    		File db = new File(dbPath);
			conn = DriverManager.getConnection("jdbc:sqlite:" + db);
			selectTFs = conn.prepareStatement("SELECT did, tf FROM tfs WHERE term = ?");
			selectDF = conn.prepareStatement("SELECT df FROM dfs WHERE term = ?");
	        selectSize = conn.prepareStatement("SELECT size FROM d");
	        selectLength = conn.prepareStatement("SELECT len FROM dls WHERE did = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
    }

    public List<Posting> getIndexList(String term) {
        List<Posting> indexList = new LinkedList<Posting>();
        ResultSet rs = null;
        
        try {
			selectTFs.setString(1, term);
			rs = selectTFs.executeQuery();
	        while (rs.next()) {
	            indexList.add(new Posting(rs.getLong(1), rs.getInt(2)));
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return indexList;
    }

    public int getDF(String term) {
        int df = 0;
        ResultSet rs = null;
        try {
            selectDF.setString(1, term);
            rs = selectDF.executeQuery();
            df = (rs.next() ? rs.getInt(1) : 0);
            
        } catch (SQLException e) {
        	e.printStackTrace();
        }
        return df;
    }

    public int getSize() {
        int size = 0;
        ResultSet rs = null;
        try {
            rs = selectSize.executeQuery();
            size = (rs.next() ? rs.getInt(1) : 0);
        } catch (SQLException e) {
        	e.printStackTrace();
        }
        return size;
    }

    public int getLength(long did) {
        int length = 0;
        ResultSet rs = null;
        try {
            selectLength.setLong(1, did);
            rs = selectLength.executeQuery();
            length = (rs.next() ? rs.getInt(1) : 0);
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
        return length;
    }


}
