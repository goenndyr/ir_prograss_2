package pa1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DocumentGetter {

	private Connection conn;

    private PreparedStatement selectURL;
    private PreparedStatement selectTitle;

    public DocumentGetter() {
    	String dbPath = "nyt.sqlite";
        try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
			selectURL = conn.prepareStatement("SELECT url FROM docs WHERE did = ?");
	        selectTitle = conn.prepareStatement("SELECT title FROM docs WHERE did = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

   public String getURL(long did) {
        String url = null;
        ResultSet rs = null;
        
        
        try {
        	selectURL.setLong(1, did);
			rs = selectURL.executeQuery();
			url = (rs.next() ? rs.getString(1) : url);
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return url;
    }

    public String getTitle(long did) {
        String title = null;
        ResultSet rs = null;
        try {
            selectTitle.setLong(1, did);
            rs = selectTitle.executeQuery();
            title = (rs.next() ? rs.getString(1) : title);
        } catch (SQLException e) {
        	e.printStackTrace();
        }
        return title;
    }

}
