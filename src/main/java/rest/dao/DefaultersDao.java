package rest.dao;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import rest.Beans.Defaulters;
import rest.connection.MyConnection;

public class DefaultersDao {
	static Logger log = Logger.getLogger(DefaultersDao.class.getName());
	public String getList(String flatNumber) {
		JsonObjectBuilder res = Json.createObjectBuilder();
		ArrayList<Defaulters> userDefaulters = new ArrayList<Defaulters>();
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		MyConnection myConnection = new MyConnection();
		Connection connection = myConnection.getConnection();
		String jsonString = "";
		try {

			ArrayList<Integer> flatNumbers = new ArrayList<Integer>();

			PreparedStatement pstmt1 = null;
			PreparedStatement pstmt2 = null;
			PreparedStatement pstmt3 = null;
			String query1 = null;
			String query2 = null;
			String query3 = null;

			Float amount;
			Date date = new Date();
			LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			int month = localDate.getMonthValue();
			int year = localDate.getYear();
			String monthYear = String.valueOf(year) + "-" + String.valueOf(month);
			query3 = "SELECT MIN(year(r.dateOfPay)),MIN(month(r.dateOfPay)) FROM records r";
			pstmt3 = connection.prepareStatement(query3);
			ResultSet result3 = pstmt3.executeQuery(query3);
			int minMonth = 0, minYear = 0;
			while (result3.next()) {
				minMonth = result3.getInt(2);
				minYear = result3.getInt(1);
			}
			String minMonthYear = String.valueOf(minYear) + "-" + String.valueOf(minMonth);
			if (flatNumber.equals("all")) {
				query1 = "SELECT flatNumber FROM Users";
				pstmt1 = connection.prepareStatement(query1);
				ResultSet result = pstmt1.executeQuery(query1);
				while (result.next()) {
					flatNumbers.add(result.getInt(1));
				}
			} else {
				flatNumbers.add(Integer.parseInt(flatNumber));
			}

			for (int i = 0; i < flatNumbers.size(); i++) {
				ArrayList<String> monthAndYear = new ArrayList<String>();
				query2 = "Select Concat(year(r.dateOfPay),\"-\",month(r.dateOfPay)),u.firstName,u.lastName from records r inner join users u on u.flatNumber=r.flatNumber where r.flatNumber="
						+ flatNumbers.get(i);
				pstmt2 = connection.prepareStatement(query2);
				ResultSet result2 = pstmt2.executeQuery(query2);
				Defaulters defaulters = new Defaulters();
				int monthCount = 0;
				while (result2.next()) {
					monthAndYear.add(result2.getString(1));
					defaulters.setFirstName(result2.getString(2));
					defaulters.setLastName(result2.getString(3));
				}
				if (monthAndYear.size() > 0) {
					for (int j = 0; j <= Integer.parseInt(monthYear.substring(0, 4))
							- Integer.parseInt(minMonthYear.substring(0, 4)); j++) {
						if (!monthAndYear.get(j).substring(0, 4).equals(monthYear.substring(0, 4))) {
							for (int k = 1; k <= Integer.parseInt(monthYear.substring(5)); k++) {
								String gen_year = String.valueOf(j + Integer.parseInt(minMonthYear.substring(0, 4)))
										+ "-" + String.valueOf(k);
								if (Integer.parseInt(gen_year.substring(0, 4) + gen_year.substring(5)) > Integer
										.parseInt(monthYear.substring(0, 4) + monthYear.substring(5))) {
									break;
								}
								if (!monthAndYear.contains(gen_year)) {
									monthCount += 1;
								}
							}

						} else if (Integer.parseInt(monthYear.substring(0, 4)) == Integer
								.parseInt(minMonthYear.substring(0, 4))) {
							for (int m = 1; m <= Integer.parseInt(monthYear.substring(5)); m++) {
								String gen_year = String.valueOf(j + Integer.parseInt(minMonthYear.substring(0, 4)))
										+ "-" + String.valueOf(m);
								if (Integer.parseInt(gen_year.substring(0, 4) + gen_year.substring(5)) > Integer
										.parseInt(monthYear.substring(0, 4) + monthYear.substring(5))) {
									break;
								}
								if (!monthAndYear.contains(gen_year)) {
									monthCount += 1;
								}
							}
						} else {
							for (int l = 1; l <= 12; l++) {
								String gen_year = String.valueOf(j + Integer.parseInt(minMonthYear.substring(0, 4)))
										+ "-" + String.valueOf(l);
								if (Integer.parseInt(gen_year.substring(0, 4) + gen_year.substring(5)) > Integer
										.parseInt(monthYear.substring(0, 4) + monthYear.substring(5))) {
									break;
								}
								if (!monthAndYear.contains(gen_year)) {
									monthCount += 1;
								}
							}
						}

					}

					amount = (float) (monthCount * 0.2 * 3000);
					defaulters.setAmount(amount);
					defaulters.setMonths(monthCount);
					defaulters.setFlatNumber(String.valueOf(flatNumbers.get(i)));
					if (monthCount > 0)
						userDefaulters.add(defaulters);
				}

				else
					continue;
			}
			connection.close();
			String defJson = gson.toJson(userDefaulters);

			if (userDefaulters.size() > 0)
				res = Json.createObjectBuilder().add("records", defJson);
			else
				res = Json.createObjectBuilder().add("records", "no records found");

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