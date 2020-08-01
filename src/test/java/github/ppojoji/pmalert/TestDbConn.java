package github.ppojoji.pmalert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestDbConn {

	public static void main(String[] args) throws SQLException {
		
		new org.mariadb.jdbc.Driver();
		
		String url = "jdbc:mariadb://localhost:3306/airdb";
		String user = "root";
		String password = "root";
		Connection con = DriverManager.getConnection(url, user, password);
		
		String query = "select seq, station_name from station";
		
		PreparedStatement stmt = con.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			String name = rs.getString("station_name");
			System.out.println(name);
		}
		con.close();
		
	}
}
