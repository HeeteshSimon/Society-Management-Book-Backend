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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import rest.Beans.SocietyPaymentRecords;
import rest.connection.MyConnection;

public class SocietyPaymentRecordsDao {
	static Logger log = Logger.getLogger(SocietyPaymentRecordsDao.class.getName());
	@Autowired
	MyConnection myConnection=new MyConnection() ;
	
	public String getRecords(String type) {
		
		Connection connection = myConnection.getConnection();
		ArrayList<SocietyPaymentRecords> societyRecords = new ArrayList<SocietyPaymentRecords>();
		String jsonString = "";
		JsonObjectBuilder res = Json.createObjectBuilder();
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();

		if (connection != null && type.equals("yearly")) {

			try {
				PreparedStatement pstmt = null;
				String query = null;
				query = "select Case when ROW_NUMBER() over(Partition by year(dateOfPay) order by year(dateOfPay))=1 then year(dateOfPay) else '' end as year\n"
						+ ",expenseType,sum(amount)\n"
						+ "from Expenses group by expenseType,year(dateOfPay) order by year(dateOfPay) desc;";
				pstmt = connection.prepareStatement(query);
				ResultSet result = pstmt.executeQuery(query);
				while (result.next()) {

					SocietyPaymentRecords societyPaymentRecords = new SocietyPaymentRecords();

					societyPaymentRecords.setYear(result.getString(1));
					societyPaymentRecords.setExpenseType(result.getString(2));
					societyPaymentRecords.setAmount(result.getFloat(3));

					societyRecords.add(societyPaymentRecords);

				}
				connection.close();

			}

			catch (SQLException e) {
				log.error(e.getStackTrace());
			}

			if (societyRecords.size() > 0) {
				String recordsJson = gson.toJson(societyRecords);
				log.debug(recordsJson);
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
				query = "select Case when ROW_NUMBER() over(Partition by Concat(month(dateOfPay),\"/\",year(dateOfPay)) order by Concat(month(dateOfPay),\"/\",year(dateOfPay)))=1 then Concat(month(dateOfPay),\"/\",year(dateOfPay)) else '' end as month\n"
						+ ",expenseType,sum(amount),dateOfPay,modeOfPayment ,paymentReference ,expenseDescription,expenseId \n"
						+ "from Expenses group by expenseType,Concat(month(dateOfPay),\"/\",year(dateOfPay)) order by Concat(month(dateOfPay),\"/\",year(dateOfPay)) desc;";
				pstmt = connection.prepareStatement(query);
				ResultSet result = pstmt.executeQuery(query);
				while (result.next()) {
					SocietyPaymentRecords societyPaymentRecords = new SocietyPaymentRecords();

					societyPaymentRecords.setMonth(result.getString(1));
					societyPaymentRecords.setExpenseType(result.getString(2));
					societyPaymentRecords.setAmount(result.getFloat(3));
					societyPaymentRecords.setDate(result.getString(4));
					societyPaymentRecords.setModeOfPayment(result.getString(5));
					societyPaymentRecords.setPaymentReference(result.getString(6));
					if (result.getString(7).equals("")) {
						societyPaymentRecords.setExpenseDescription("None");
					} else {
						societyPaymentRecords.setExpenseDescription(result.getString(7));
					}

					societyPaymentRecords.setExpenseId(result.getString(8));
					log.debug(result.getString(8));
					societyRecords.add(societyPaymentRecords);

				}
				connection.close();

			}

			catch (SQLException e) {
				e.printStackTrace();

			}

			if (societyRecords.size() > 0) {
				String recordsJson = gson.toJson(societyRecords);
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
	
	public String addRecord(SocietyPaymentRecords societyPaymentRecords) {
		Connection connection = myConnection.getConnection();
		String jsonString = null;
		JsonObjectBuilder res = Json.createObjectBuilder();
		
		
		try {
			
			PreparedStatement pstmt = null;
			String query = null;
			query = "INSERT INTO `Expenses` (`expenseType`, `amount`, `dateOfPay`,`modeOfPayment`,`expenseDescription`,`paymentReference`) VALUES (?, ?, ?, ?, ?, ?)";
			pstmt = connection.prepareStatement(query);

			pstmt.setString(1, societyPaymentRecords.getExpenseType());
			pstmt.setFloat(2, societyPaymentRecords.getAmount());
			pstmt.setString(3, societyPaymentRecords.getDateOfPay());
			pstmt.setString(4, societyPaymentRecords.getModeOfPayment());
			pstmt.setString(5, societyPaymentRecords.getExpenseDescription());
			pstmt.setString(6, societyPaymentRecords.getPaymentReference());
			int result = pstmt.executeUpdate();
			connection.close();
			if (result > 0) {
				res = Json.createObjectBuilder().add("status", true).add("message", "success");

			} else {
				res = Json.createObjectBuilder().add("status", false).add("message", "error");

			}

		} catch (Exception e) {
			log.error(e.getStackTrace());
		}

		JsonObject jsonObject = res.build();
		StringWriter writer = new StringWriter();
		Json.createWriter(writer).write(jsonObject);
		jsonString = writer.toString();
		return jsonString;
	}
}