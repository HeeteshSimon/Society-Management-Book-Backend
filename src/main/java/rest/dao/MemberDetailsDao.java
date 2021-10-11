
package rest.dao;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import rest.Beans.Member;
import rest.connection.MyConnection;

@Component("memberDao")
public class MemberDetailsDao {
	static Logger log = Logger.getLogger(MemberDetailsDao.class.getName());
	@Autowired
	MyConnection myConnection;

	public String getAll() {
		Connection connection = myConnection.getConnection();
		String jsonString = "";

		JsonObjectBuilder res = Json.createObjectBuilder();

		ArrayList<Member> usersList = new ArrayList<Member>();

		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		try {

			PreparedStatement pstmt = null;
			String query = null;
			query = "SELECT `flatNumber`,`userName`,`firstName`,`lastName`, `userPassword`, `emailId`, `userRole`,`membershipJoin`,`membershipEnd` FROM `Users` WHERE `userRole`='member'";
			pstmt = connection.prepareStatement(query);
			ResultSet result = pstmt.executeQuery(query);
			while (result.next()) {
				Member member = new Member();
				member.setFlatNumber(result.getInt("flatNumber"));
				member.setUserName(result.getString("userName"));
				member.setFirstName(result.getString("firstName"));
				member.setLastName(result.getString("lastName"));
				member.setUserPassword(result.getString("userPassword"));
				member.setEmailId(result.getString("emailId"));
				member.setUserRole(result.getString("userRole"));
				member.setMembershipJoin(result.getString("membershipJoin"));
				member.setMembershipEnd(result.getString("membershipEnd"));
				usersList.add(member);
			}
			connection.close();

			if (usersList.size() > 0) {
				String usersJson = gson.toJson(usersList);
				res.add("records", usersJson);

			} else {
				res.add("records", "no records found");
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

	public String add(Member member) {

		Connection connection = myConnection.getConnection();
		String jsonString = null;
		JsonObjectBuilder res = Json.createObjectBuilder();
		try {

			PreparedStatement pstmt1 = null;
			PreparedStatement pstmt2 = null;
			PreparedStatement pstmt3 = null;
			String query1 = null;
			String query2 = null;
			String query3 = null;

			query3 = "SELECT (SELECT COUNT(*) FROM users) AS count1,(SELECT COUNT(*) FROM Flat) AS count2";
			pstmt3 = connection.prepareStatement(query3);
			ResultSet result3 = pstmt3.executeQuery(query3);
			if (result3.next()) {
				if (result3.getInt(1) == result3.getInt(2)) {

					res = Json.createObjectBuilder().add("status", false).add("message", "All Rooms are Booked");

				} else {
					query2 = "SELECT * FROM Users WHERE flatNumber=" + member.getFlatNumber();
					pstmt2 = connection.prepareStatement(query2);
					ResultSet result2 = pstmt2.executeQuery(query2);
					if (result2.next()) {
						res = Json.createObjectBuilder().add("status", false).add("message", "Room already Allocated");
					} else {
						query1 = "INSERT INTO Users(`flatNumber`, `userName`,`firstName`, `lastname`, `userPassword`,`emailId`,`userRole`,`memberCount`,`membershipJoin`,`membershipEnd`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
						pstmt1 = connection.prepareStatement(query1);

						pstmt1.setInt(1, member.getFlatNumber());
						pstmt1.setString(2, member.getUserName());
						pstmt1.setString(3, member.getFirstName());
						pstmt1.setString(4, member.getLastName());
						pstmt1.setString(5, member.getUserPassword());
						pstmt1.setString(6, member.getEmailId());
						pstmt1.setString(7, member.getUserRole());
						pstmt1.setInt(8, member.getMemberCount());
						pstmt1.setString(9, member.getMembershipJoin());
						pstmt1.setString(10, member.getMembershipEnd());
						int result1 = pstmt1.executeUpdate();

						if (result1 > 0) {
							connection.close();
							res = Json.createObjectBuilder().add("status", true).add("message", "success");
//				        	 return "{status: true, message: success, batchId: "+batchMapped+"}";
						} else {
							connection.close();
							res = Json.createObjectBuilder().add("status", false).add("message", "error");
//				        	 return "{status: true, message: batch_id_not_mapped, batchId: not mapped}";
						}
					}
				}
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

	public String delete(String flatNumber) {

		Connection connection = myConnection.getConnection();
		String jsonString = null;

		JsonObjectBuilder res = Json.createObjectBuilder();

		try {

			PreparedStatement pstmt1 = null;
			PreparedStatement pstmt2 = null;
			String query1 = null;
			String query2 = null;
			query1 = "DELETE  FROM Users  WHERE flatNumber=?";
			query2 = "DELETE  FROM Records  WHERE flatNumber=?";
			// query = "UPDATE `Users` SET `userName`=NULL,`firstName`=NULL,`lastName`=NULL,
			// `userPassword`=NULL,`emailId`=NULL,`userRole`=NULL,`memberCount`=NULL,`membershipJoin`=NULL,`membershipEnd`=NULL
			// where `flatNumber`=? ";

			pstmt2 = connection.prepareStatement(query2);
			pstmt2.setInt(1, Integer.parseInt(flatNumber));
			int result2 = pstmt2.executeUpdate();

			pstmt1 = connection.prepareStatement(query1);
			pstmt1.setInt(1, Integer.parseInt(flatNumber));
			int result1 = pstmt1.executeUpdate();

			if (result1 > 0 && result2 > 0) {
				connection.close();
				res = Json.createObjectBuilder().add("status", true).add("message", "success");
			} else {
				connection.close();
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

	public String update(Member member) {

		Connection connection = myConnection.getConnection();
		String jsonString = null;

		JsonObjectBuilder res = Json.createObjectBuilder();
		try {

			PreparedStatement pstmt = null;
			String query = null;
			query = "UPDATE `Users` SET `userName`=?, "
					+ "`firstName`=?, `lastName`=?, `userPassword`=?,`emailId`=?,`memberCount`=?,`membershipJoin`=?,`membershipEnd`=?  where `flatNumber`=?";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, member.getUserName());
			pstmt.setString(2, member.getFirstName());
			pstmt.setString(3, member.getLastName());
			pstmt.setString(4, member.getUserPassword());
			pstmt.setString(5, member.getEmailId());
			// pstmt.setString(6, member.getUserRole());
			pstmt.setInt(6, member.getMemberCount());
			pstmt.setString(7, member.getMembershipJoin());
			pstmt.setString(8, member.getMembershipEnd());
			pstmt.setInt(9, member.getFlatNumber());
			int result = pstmt.executeUpdate();
			if (result > 0) {
				res = Json.createObjectBuilder().add("status", true).add("message", "success");
				connection.close();
			} else {
				connection.close();
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
