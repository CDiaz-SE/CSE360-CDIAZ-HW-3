package databasePart1;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import application.Answer;
import application.Question;
import application.User;


/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = "";

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			// statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}
	
	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "realName VARCHAR(255), "
				+ "email VARCHAR(255), "
				+ "role VARCHAR(5), "
				+ "oneTimePassword VARCHAR(8))";
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	    			+ "expires DATETIME NOT NULL, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
	    
	    String questionsTable = "CREATE TABLE IF NOT EXISTS Questions ("
	    	    + "id INT AUTO_INCREMENT PRIMARY KEY, "
	    	    + "poster VARCHAR(255), "
	    	    + "title VARCHAR(255), "
	    	    + "body VARCHAR(MAX), "
	    	    + "isResolved BOOLEAN DEFAULT FALSE, "
	    	    + "hasFollowUp BOOLEAN DEFAULT FALSE, "
	    	    + "followUpQuestionId INT)";
	    	statement.execute(questionsTable);
	    	
	        String answersTable = "CREATE TABLE IF NOT EXISTS Answers ("
	                + "id INT AUTO_INCREMENT PRIMARY KEY, "
	                + "questionID INT, "
	                + "poster VARCHAR(255), "
	                + "body VARCHAR(MAX), "
	                + "isSolution BOOLEAN DEFAULT FALSE, "
	                + "FOREIGN KEY (questionID) REFERENCES Questions(id) ON DELETE CASCADE)";
	        statement.execute(answersTable);
	}
//	private void addColIfNotExist(String tableName, String colName, String colType) throws SQLException {
//	    DatabaseMetaData meta = connection.getMetaData();
//	    try (ResultSet rs = meta.getColumns(null, null, tableName.toUpperCase(), colName.toUpperCase())) {
//	        if (!rs.next()) { // column does not exist
//	            String alter = "ALTER TABLE " + tableName + " ADD COLUMN " + colName + " " + colType;
//	            statement.execute(alter);
//	        }
//	    }
//	}

//	 private void otpColumnsExistenceCheck() throws SQLException {
//	    addColIfNotExist("cse360users", "oneTimePassword", "VARCHAR(255)");
//	    addColIfNotExist("cse360users", "otpExpiration", "TIMESTAMP");
//	}

	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, realName, email, role) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRealName());
			pstmt.setString(4, user.getEmail());
			pstmt.setString(5, "00000");
			pstmt.executeUpdate();
		}
		
		for (String i : user.getRole()) {
			addUserRole(user.getUserName(), i);
		}
	}

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	// gets all users into a list
	public List<User> getAllUsers() {
		List<User> users = new ArrayList<>();
		    
		String query = "SELECT userName, password, realName, email FROM cse360users";
		   try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		       ResultSet rs = pstmt.executeQuery();
		       while (rs.next()) {
		    	   String userName = rs.getString("userName");
		    	   String password = rs.getString("password");
		    	   String realName = rs.getString("realName");
		    	   String email = rs.getString("email");
		    	   List<String> roles = getUserRole(userName);
		        	
		    	   User user = new User(userName,password,realName,email,roles);
		    	   users.add(user);
		       }

		} catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return users;
	}
	
	// Retrieves the role of a user from the database using their UserName.
	public List<String> getUserRole(String userName) {
		int roleEncode = 0b0;
	    // Grab current role encoding from database
		String query = "SELECT role FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	        	roleEncode = Integer.parseInt(rs.getString("role")); // Retrieve the encoded role as binary
	            if (roleEncode == 0) {
	            	return null;
	            }
	        }      
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    // Decodes user role and creates a list of strings to return
	    List<String> roles = new ArrayList<>();
	    StringBuilder sb = new StringBuilder(Integer.toBinaryString(roleEncode));
	    String encodedString = sb.reverse().toString();
	    for (int i = 0 ; i < encodedString.length(); i++) {
	    	if (encodedString.charAt(i) == '1' ) {
	    		switch (i) {
	    			case 0: 
	    				roles.add("admin");
	    				break;
	    			case 1: 
	    				roles.add("student");
	    				break;
	    			case 2: 
	    				roles.add("reviewer");
	    				break;
	    			case 3: 
	    				roles.add("instructor");
	    				break;
	    			case 4: 
	    				roles.add("staff");
	    				break;
	    		}			
	    	}
	    }
	    
	    if (roles.size() != 0) { // Check if list of roles is empty
	    	return roles;
	    } else {
	    	return null;
	    }
	}
	
	// Adds a new role to existing user
	public void addUserRole(String userName, String role) {
		int roleEncode = 0;
		
		// Grab current role encoding from database
		String query = "SELECT role FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            roleEncode = Integer.parseInt(rs.getString("role")); // Retrieve the encoded role as binary
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    // Bitwise operations for encoding roles into database
	    switch (role) {
	    	case "admin":
	    		roleEncode = (roleEncode | 1);
	    		break;
	    	case "student":
	    		roleEncode = (roleEncode | 2);
	    		break;
	    	case "reviewer":
	    		roleEncode = (roleEncode | 4);
	    		break;
	    	case "instructor":
	    		roleEncode = (roleEncode | 8);
	    		break;
	    	case "staff":
	    		roleEncode = (roleEncode | 16);
	    		break;
	    }
	    
	    // Update database with new role encoding
	    String update = "Update cse360users SET role = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(update)) {
	    	pstmt.setString(1, Integer.toString(roleEncode));
	    	pstmt.setString(2, userName);
	    	pstmt.executeUpdate();
	    } catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void remUserRole(String userName, String role) {
		int roleEncode = 0b0;
		
		String query = "SELECT role FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            roleEncode = Integer.parseInt(rs.getString("role")); // Retrieve the encoded role from the database
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    // Bitwise operations for removing a role
	    switch (role) {
	    	case "admin":
	    		//roleEncode = (roleEncode & 30);
	    		break;
	    	case "student":
	    		roleEncode = (roleEncode & 29);
	    		break;
	    	case "reviewer":
	    		roleEncode = (roleEncode & 27);
	    		break;
	    	case "instructor":
	    		roleEncode = (roleEncode & 23);
	    		break;
	    	case "staff":
	    		roleEncode = (roleEncode & 15);
	    		break;
	    }
	    
	    // Update database with new encoding
	    String update = "Update cse360users SET role = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(update)) {
	    	pstmt.setString(1, Integer.toString(roleEncode));
	    	pstmt.setString(2, userName);
	    	pstmt.executeUpdate();
	    } catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getEmail(String userName) {
	    String query = "SELECT email FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("email"); // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	public String getRealName(String userName) {
	    String query = "SELECT realName FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("realName"); // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode() {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    LocalDateTime expires = LocalDateTime.now().plus(2, ChronoUnit.DAYS); // Create a expiration time
	    System.out.println(expires);
	    String query = "INSERT INTO InvitationCodes (code, expires) VALUES (?, ?)";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setTimestamp(2, Timestamp.valueOf(expires));
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// Checks an invitation code to check if it's expired
	public boolean CheckInvitationCodeExpiration(String code) {
		//NEED TO FINISH!!!
		String query = "SELECT * FROM InvitationCodes WHERE code = ? AND expires > CURRENT_TIMESTAMP";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	// Generates a one-time password for a user
	public String generateOneTimePassword(String userName) {
	    String otp = UUID.randomUUID().toString().substring(0, 8); // 8-char One-time-password
	    
	    String update = "UPDATE cse360users SET oneTimePassword = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(update)) {
	        pstmt.setString(1, otp);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return otp;
	}
	
	// checks if a one-time-password is valid
	public boolean validateOneTimePassword(String userName, String otp) {
	    String query = "SELECT oneTimePassword FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            String storedOtp = rs.getString("oneTimePassword");
	            if (storedOtp != null && storedOtp.equals(otp)) {
	                return true;
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// makes an OTP invalid after use
	public void clearOneTimePassword(String userName) {
	    String update = "UPDATE cse360users SET oneTimePassword = NULL WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(update)) {
	        pstmt.setString(1, userName);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	// updates a user's password
	public void updatePassword(String userName, String newPassword) {
	    String update = "UPDATE cse360users SET password = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(update)) {
	        pstmt.setString(1, newPassword);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	// Returns a list of all user names except the admin's username
    public List<String> getAllUserNamesExcept(String admin) {
        List<String> userNames = new ArrayList<>();
        String query = "SELECT userName FROM cse360users WHERE userName <> ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) 
        {
            pstmt.setString(1, admin);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                userNames.add(rs.getString("userName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userNames;
    }

    // Deletes a user by their userName
    public boolean deleteUser(String userName) {
        String query = "DELETE FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void addQuestion(Question question) throws SQLException {
    	String insertQuestion = "INSERT INTO Questions (poster, title, body) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertQuestion, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, question.getPoster());
			pstmt.setString(2, question.getTitle());
			pstmt.setString(3, question.getBody());
			pstmt.executeUpdate();
			
			
			// This is to update the question object with the newly created ID but is currently having issues
			/*
			try (ResultSet rs = statement.getGeneratedKeys()) {
				System.out.println("TEST");
				if (rs.next()) {
					question.setID((int) rs.getInt("id"));
					rs.close();
					return;
				}
			} */
		}
    }
    
    public void editQTitle(Question question, String title) throws SQLException {
    	String update = "UPDATE Questions SET title = ? WHERE id = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(update)) {
			pstmt.setString(1, title);
			pstmt.setInt(2, question.getID());
			pstmt.executeUpdate();
		}
    }
    
    public void editQTitle(int id, String title) throws SQLException {
    	String update = "UPDATE Questions SET title = ? WHERE id = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(update)) {
			pstmt.setString(1, title);
			pstmt.setInt(2, id);
			pstmt.executeUpdate();
		}
    }
    
    public void editQBody(Question question, String body) throws SQLException {
    	String update = "UPDATE Questions SET body = ? WHERE id = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(update)) {
			pstmt.setString(1, body);
			pstmt.setInt(2, question.getID());
			pstmt.executeUpdate();
		}
    }
    
    public void editQBody(int id, String body) throws SQLException {
    	String update = "UPDATE Questions SET body = ? WHERE id = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(update)) {
			pstmt.setString(1, body);
			pstmt.setInt(2, id);
			pstmt.executeUpdate();
		}
    }
    
    public void editABody(int id, String body) throws SQLException {
    	String update = "UPDATE Answers SET body = ? WHERE id = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(update)) {
			pstmt.setString(1, body);
			pstmt.setInt(2, id);
			pstmt.executeUpdate();
		}
    }
    
    public void deleteQuestion(Question question) throws  SQLException {
    	String delete = "DELETE FROM Questions WHERE id = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(delete)) {
    		pstmt.setInt(1, question.getID());
    		pstmt.executeUpdate();
    	}
    	
    	delete = "DELETE FROM Answers WHERE questionID = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(delete)) {
    		pstmt.setInt(1, question.getID());
    		pstmt.executeUpdate();
    	}
    }
    
    public void deleteQuestion(int id) throws  SQLException {
    	String delete = "DELETE FROM Questions WHERE id = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(delete)) {
    		pstmt.setInt(1, id);
    		pstmt.executeUpdate();
    	}
    	
    	delete = "DELETE FROM Answers WHERE questionID = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(delete)) {
    		pstmt.setInt(1, id);
    		pstmt.executeUpdate();
    	}
    }
    
    public List<Question> getQuestions() throws SQLException {
    	List<Question> result = new ArrayList<>();
    	String query = "SELECT id, poster, title, body, isResolved, hasFollowUp, followUpQuestionId FROM Questions";
    	
    	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
    		ResultSet rs = pstmt.executeQuery();
    		while (rs.next()) {
    			int id = rs.getInt("id");
    			String poster = rs.getString("poster");
    			String title = rs.getString("title");
    			String body = rs.getString("body");
    			boolean isResolved = rs.getBoolean("isResolved");
    			boolean hasFollowUp = rs.getBoolean("hasFollowUp");
    			int followUpId = rs.getInt("followUpQuestionId");
    			
    			Question question = new Question(id,poster, title, body);
    			question.setResolved(isResolved);
    			question.setHasFollowUp(hasFollowUp);
    			// set follow-up id (null if database value was NULL)
    			if (rs.wasNull()) {
    				question.setFollowUpQuestionId(null);
    			} else {
    				question.setFollowUpQuestionId(followUpId);
    			}
    			result.add(question);
    		}
    	} catch (SQLException e) {
	        e.printStackTrace();
	    }
    			
    	return result;
    }
    
    public Question getQuestionById(int qId) throws SQLException {
        String query = "SELECT id, poster, title, body, isResolved, hasFollowUp, followUpQuestionId FROM Questions WHERE id = ?";
        Question question = null;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, qId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String poster = rs.getString("poster");
                String title = rs.getString("title");
                String body = rs.getString("body");
                boolean isResolved = rs.getBoolean("isResolved");
                boolean hasFollowUp = rs.getBoolean("hasFollowUp");
                int followUpId = rs.getInt("followUpQuestionId");
                question = new Question(id, poster, title, body);
                question.setResolved(isResolved);
                question.setHasFollowUp(hasFollowUp);
                question.setFollowUpQuestionId(rs.wasNull() ? null : followUpId);
            }

            rs.close();
        }

        return question; // null if not found
    }
    
    public void addAnswer(Answer answer, Question question) throws SQLException {
    	String insertQuestion = "INSERT INTO Answers (questionID, poster, body) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertQuestion, PreparedStatement.RETURN_GENERATED_KEYS)) {
			pstmt.setInt(1, question.getID());
			pstmt.setString(2, answer.getPoster());
			pstmt.setString(3, answer.getBody());
			pstmt.executeUpdate();
		}
    }
    
    public void addAnswer(Answer answer, int id) throws SQLException {
    	String insertQuestion = "INSERT INTO Answers (questionID, poster, body) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertQuestion, PreparedStatement.RETURN_GENERATED_KEYS)) {
			pstmt.setInt(1, id);
			pstmt.setString(2, answer.getPoster());
			pstmt.setString(3, answer.getBody());
			pstmt.executeUpdate();
		}
    }
    
    public void deleteAnswer(Answer answer) throws  SQLException {
    	String delete = "DELETE FROM Answers WHERE id = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(delete)) {
    		pstmt.setInt(1, answer.getID());
    		pstmt.executeUpdate();
    	}
    }
    
    public void deleteAnswer(int ID) throws  SQLException {
    	String delete = "DELETE FROM Answers WHERE id = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(delete)) {
    		pstmt.setInt(1, ID);
    		pstmt.executeUpdate();
    	}
    }
    
    public List<Answer> getAnswers(Question question) throws SQLException {
    	List<Answer> result = new ArrayList<>();
    	String query = "SELECT id, questionID, poster, body, isSolution FROM Answers WHERE questionID = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
    		pstmt.setInt(1, question.getID());
    		ResultSet rs = pstmt.executeQuery();
    		while (rs.next()) {
    			int id = rs.getInt("id");
    			int qId = rs.getInt("questionID");
    			String poster = rs.getString("poster");
    			String body = rs.getString("body");
    			boolean isSolution = rs.getBoolean("isSolution");
    			Answer answer = new Answer(id, qId, poster, body);
    			answer.setSolution(isSolution);
    			result.add(answer);
    		}
    	} catch (SQLException e) {
	        e.printStackTrace();
	    }
    			
    	return result;
    }
    
    public List<Answer> getAnswers(int qID) throws SQLException {
    	List<Answer> result = new ArrayList<>();
    	String query = "SELECT id, questionID, poster, body, isSolution FROM Answers WHERE questionID = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
    		pstmt.setInt(1, qID);
    		ResultSet rs = pstmt.executeQuery();
    		while (rs.next()) {
    			int id = rs.getInt("id");
    			int qId = rs.getInt("questionID");
    			String poster = rs.getString("poster");
    			String body = rs.getString("body");
    			boolean isSolution = rs.getBoolean("isSolution");

    			Answer answer = new Answer(id,qId,poster,body);
    			// ensure the Answer object reflects the isSolution flag from DB
    			answer.setSolution(isSolution);
    			result.add(answer);
    		}
    	} catch (SQLException e) {
	        e.printStackTrace();
	    }
    			
    	return result;
    }
    
    public List<Answer> getAnswers() throws SQLException {
        List<Answer> allAnswers = new ArrayList<>();
        String query = "SELECT id, questionID, poster, body FROM Answers";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int questionID = rs.getInt("questionID");
                String poster = rs.getString("poster");
                String body = rs.getString("body");

                Answer answer = new Answer(id, questionID, poster, body);
                allAnswers.add(answer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return allAnswers;
    }
    
 
    public void markAnswerAsSolution(int questionId, int answerId) {
        String clearSolutions = "UPDATE Answers SET isSolution = FALSE WHERE questionID = ?";
        String markSolution = "UPDATE Answers SET isSolution = TRUE WHERE id = ?";
        String markQuestionResolved = "UPDATE Questions SET isResolved = TRUE WHERE id = ?";

        try (
            PreparedStatement clearStmt = connection.prepareStatement(clearSolutions);
            PreparedStatement markStmt = connection.prepareStatement(markSolution);
            PreparedStatement resolvedStmt = connection.prepareStatement(markQuestionResolved)
        ) {
            // Clear other solutions for this question
            clearStmt.setInt(1, questionId);
            clearStmt.executeUpdate();

            // Mark selected answer as the solution
            markStmt.setInt(1, answerId);
            markStmt.executeUpdate();

            // Mark the question as resolved
            resolvedStmt.setInt(1, questionId);
            resolvedStmt.executeUpdate();

            System.out.println("Answer " + answerId + " marked as solution for question " + questionId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void addFollowUpQuestion(int originalQuestionId, String poster, String title, String body) {
        String insertFollowUp = "INSERT INTO Questions (poster, title, body) VALUES (?, ?, ?)";
        String markHasFollowUp = "UPDATE Questions SET hasFollowUp = TRUE, followUpQuestionId = ? WHERE id = ?";

        try (
            PreparedStatement insertStmt = connection.prepareStatement(insertFollowUp, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement markStmt = connection.prepareStatement(markHasFollowUp)
        ) {
            // Insert follow-up question
            insertStmt.setString(1, poster);
            insertStmt.setString(2, "Follow-Up: " + title);
            insertStmt.setString(3, body);
            insertStmt.executeUpdate();

            try (ResultSet gen = insertStmt.getGeneratedKeys()) {
                int followUpId = -1;
                if (gen != null && gen.next()) {
                    followUpId = gen.getInt(1);
                }

                // Mark the original question as having a follow-up and store the ID if found
                if (followUpId != -1) {
                    markStmt.setInt(1, followUpId);
                } else {
                    markStmt.setNull(1, java.sql.Types.INTEGER);
                }
                markStmt.setInt(2, originalQuestionId);
                markStmt.executeUpdate();
            }

            System.out.println("Follow-up question added for question " + originalQuestionId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

}