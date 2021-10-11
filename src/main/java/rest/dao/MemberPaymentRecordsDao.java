//http://localhost:9090/sqlartifact/addUserRecord?flatNumber=102&amount=5000&dateOfPay=2021-05-01&modeOfPayment=Online&paymentReference=49494

package rest.dao;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import rest.Beans.UserPaymentRecords;
import rest.connection.MyConnection;

@Component("recordsDao")
public class MemberPaymentRecordsDao {
	static Logger log = Logger.getLogger(MemberPaymentRecordsDao.class.getName());
	@Autowired
	MyConnection myConnection;
	
	public String getAllRecords() {
		Connection connection = myConnection.getConnection();
		ArrayList<UserPaymentRecords> userRecords = new ArrayList<UserPaymentRecords>();
		String jsonString = "";
		JsonObjectBuilder res = Json.createObjectBuilder();
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		
		if (connection != null) {
			
			try {
				PreparedStatement pstmt = null;
				String query = null;
				query = "Select * FROM Records";
				pstmt = connection.prepareStatement(query);
				ResultSet result = pstmt.executeQuery(query);
				while (result.next()) {
					UserPaymentRecords userPaymentRecords = new UserPaymentRecords();
					userPaymentRecords.setRecordId(result.getInt(1));
					userPaymentRecords.setFlatNumber(result.getInt(2));
					userPaymentRecords.setAmount(result.getFloat(3));
					userPaymentRecords.setDateOfPay(result.getString(4));
					userPaymentRecords.setModeOfPayment(result.getString(5));
					userPaymentRecords.setPaymentReference(result.getString(6));
					userRecords.add(userPaymentRecords);

				}
				connection.close();

			}

			catch (SQLException e) {
				log.error(e.getStackTrace());
			}

			if (userRecords.size() > 0) {
				String recordsJson = gson.toJson(userRecords);
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
	
	
	
	public String getRecords(String flatNumber, String type) {
		
		Connection connection = myConnection.getConnection();
		ArrayList<UserPaymentRecords> userRecords = new ArrayList<UserPaymentRecords>();
		String jsonString = "";
		JsonObjectBuilder res = Json.createObjectBuilder();
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		if (connection != null && type.equals("yearly")) {
			try {
				PreparedStatement pstmt = null;
				String query = null;
				query = "Select r.recordId,r.flatNumber,u.firstName,u.lastName,sum(r.amount),year(r.dateOfPay) from Records r inner join Users u on u.flatNumber=r.flatNumber where r.flatNumber="
						+ flatNumber + " group by year(r.dateOfPay);";
				pstmt = connection.prepareStatement(query);
				ResultSet result = pstmt.executeQuery(query);
				while (result.next()) {
					UserPaymentRecords userPaymentRecords = new UserPaymentRecords();
					userPaymentRecords.setRecordId(result.getInt(1));
					userPaymentRecords.setFlatNumber(result.getInt(2));
					userPaymentRecords.setFirstName(result.getString(3));
					userPaymentRecords.setLastName(result.getString(4));
					userPaymentRecords.setAmount(result.getFloat(5));
					userPaymentRecords.setDateOfPay(result.getString(6));
					userRecords.add(userPaymentRecords);

				}
				connection.close();

			}

			catch (SQLException e) {
				log.error(e.getStackTrace());
			}

			if (userRecords.size() > 0) {
				String recordsJson = gson.toJson(userRecords);
				res.add("records", recordsJson);
			} else {
				res.add("records", "no records found");
			}

			JsonObject jsonObject = res.build();

			StringWriter writer = new StringWriter();
			Json.createWriter(writer).write(jsonObject);
			jsonString = writer.toString();

		}
		if (connection != null && type.equals("monthly")) {
			try {
				PreparedStatement pstmt = null;
				String query = null;
				query = "Select r.recordId,r.flatNumber,u.firstName,u.lastName,r.amount,r.dateOfPay,r.modeOfPayment,r.paymentReference from Records r inner join Users u on u.flatNumber=r.flatNumber where r.flatNumber= "
						+ flatNumber;
				pstmt = connection.prepareStatement(query);
				ResultSet result = pstmt.executeQuery(query);
				while (result.next()) {
					UserPaymentRecords userPaymentRecords = new UserPaymentRecords();

					userPaymentRecords.setRecordId(result.getInt(1));
					userPaymentRecords.setFlatNumber(result.getInt(2));
					userPaymentRecords.setFirstName(result.getString(3));
					userPaymentRecords.setLastName(result.getString(4));
					userPaymentRecords.setAmount(result.getFloat(5));
					userPaymentRecords.setDateOfPay(result.getString(6));
					userPaymentRecords.setModeOfPayment(result.getString(7));
					userPaymentRecords.setPaymentReference(result.getString(8));
					userRecords.add(userPaymentRecords);

				}
				connection.close();

			}

			catch (SQLException e) {
				log.error(e.getStackTrace());
			}

			if (userRecords.size() > 0) {
				String recordsJson = gson.toJson(userRecords);
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

	

	public String addRecord(UserPaymentRecords records) {
		Connection connection = myConnection.getConnection();
		String jsonString = null;
		JsonObjectBuilder res = Json.createObjectBuilder();

		
		try {

			PreparedStatement pstmt = null;
			String query = null;
			query = "INSERT INTO `records` (`flatNumber`, `amount`, `dateOfPay`,`modeOfPayment`,`paymentReference`) VALUES (?,?,?,?,?);";
			pstmt = connection.prepareStatement(query);

			pstmt.setInt(1, records.getFlatNumber());
			pstmt.setFloat(2, records.getAmount());
			pstmt.setString(3, records.getDateOfPay());
			pstmt.setString(4, records.getModeOfPayment());
			pstmt.setString(5, records.getPaymentReference());
			int result = pstmt.executeUpdate();
			connection.close();
			if (result > 0) {
				res = Json.createObjectBuilder().add("status", true).add("message", "success");

			} else {
				res = Json.createObjectBuilder().add("status", false).add("message", "failure");

			}

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		JsonObject jsonObject = res.build();
		StringWriter writer = new StringWriter();
		Json.createWriter(writer).write(jsonObject);
		jsonString = writer.toString();
		return jsonString;

	}

	public String updateRecord(UserPaymentRecords record) {
		Connection connection = myConnection.getConnection();
		String jsonString = null;
		JsonObjectBuilder res = Json.createObjectBuilder();

		try {
			
			PreparedStatement pstmt = null;
			String query = null;
			query = "UPDATE `records` SET `amount`=?, `dateOfPay`=?,`modeOfPayment`=?,`paymentReference`=? WHERE `recordId`=?";
			pstmt = connection.prepareStatement(query);

			pstmt.setFloat(1, record.getAmount());
			pstmt.setString(2, record.getDateOfPay());
			pstmt.setString(3, record.getModeOfPayment());
			pstmt.setString(4, record.getPaymentReference());
			pstmt.setInt(5, record.getRecordId());
			int result = pstmt.executeUpdate();
			connection.close();
			if (result > 0) {
				res = Json.createObjectBuilder().add("status", true).add("message", "success");

			} else {
				res = Json.createObjectBuilder().add("status", false).add("message", "failure");
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		JsonObject jsonObject = res.build();
		StringWriter writer = new StringWriter();
		Json.createWriter(writer).write(jsonObject);
		jsonString = writer.toString();
		return jsonString;

	}
	
public String deleteRecord(String rid) {
		
	Connection connection = myConnection.getConnection();
	String jsonString = null;
	JsonObjectBuilder res = Json.createObjectBuilder();

		try {
			
			PreparedStatement pstmt = null;
			String query = null;
			query = "DELETE FROM `records` WHERE `recordId`=?";
			pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, Integer.parseInt(rid));
			int result = pstmt.executeUpdate();
			connection.close();
			if (result > 0) {
				res = Json.createObjectBuilder().add("status", true).add("message", "success");
			} else {
				res = Json.createObjectBuilder().add("status", false).add("message", "error");

			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		JsonObject jsonObject = res.build();
		StringWriter writer = new StringWriter();
		Json.createWriter(writer).write(jsonObject);
		jsonString = writer.toString();
		return jsonString;
	}

}
