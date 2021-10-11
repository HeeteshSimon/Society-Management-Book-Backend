package rest.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.log4j.Logger;

import rest.Beans.Flats;
import rest.connection.MyConnection;

public class FlatsDao {
	static Logger log = Logger.getLogger(FlatsDao.class.getName());
	public String getFlats() {
		MyConnection myConnection = new MyConnection();
		Connection connection = myConnection.getConnection();
		ArrayList<Flats> flatList = new ArrayList<Flats>();
		String jsonString = null;

		JsonObjectBuilder res = Json.createObjectBuilder();
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		if (connection != null) {
			try {
				PreparedStatement pstmt = null;
				String query = null;
				query = "SELECT flatNumber FROM Flat";
				pstmt = connection.prepareStatement(query);
				ResultSet result = pstmt.executeQuery(query);
				while (result.next()) {
					Flats flat = new Flats();
					flat.setFlatNumber(result.getString(1));
					flatList.add(flat);

				}
				connection.close();
			}

			catch (SQLException e) {
				log.error(e.getStackTrace());

			}

			if (flatList.size() > 0) {
				String recordsJson = gson.toJson(flatList);
				res.add("records", recordsJson);
			} else {
				res.add("records", "no records found");
			}

			JsonObject jsonObject = res.build();

			StringWriter writer = new StringWriter();
			Json.createWriter(writer).write(jsonObject);
			jsonString = writer.toString();

		}
		return jsonString;
	}
}