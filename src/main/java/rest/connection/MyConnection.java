package rest.connection;

import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("myConnection")
public class MyConnection {
	static Logger log = Logger.getLogger(MyConnection.class.getName());
	
	public Connection getConnection() {
		DataSource dataSource;
		Connection dataConnection = null;

		try {
			Context ic = new InitialContext();
			dataSource = (DataSource) ic.lookup("java:comp/env/jdbc/jit");
			dataConnection = dataSource.getConnection();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return dataConnection;
	}

}
