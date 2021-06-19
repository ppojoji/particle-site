package github.ppojoji.pmalert.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConnectionTest {

	public static void main(String[] args) throws SQLException {
		
		String url = "jdbc:mariadb://localhost:3306/airdb?allowPublicKeyRetrieval=true&useSSL=false";
		String id = "root";
		String password = "1234";
		
		DriverManager.registerDriver(new org.mariadb.jdbc.Driver());
		
		Connection con = DriverManager.getConnection(url, id, password);
		con.close();
	}
}
