package application;

import java.util.List;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and role.
 */
public class User {
    private String userName;
    private String password;
    private String realName;
    private String email;
    private List<String> role;

    // Constructor to initialize a new User object with userName, password, and role.
    public User( String userName, String password, String realName, String email, List<String> roles) {
        this.userName = userName;
        this.password = password;
        this.realName = realName;
        this.email = email;
        this.role = roles;
    }
    
    // Sets the role of the user.
    public void setRole(List<String> role) {
    	this.role=role;
    }
    
    public void setRealName(String realName) { this.realName = realName; }
    public void setEmail(String email) { this.email = email; }

    public String getUserName()     { return userName; }
    public String getPassword()     { return password; }
    public String getRealName()     { return realName; }
    public String getEmail()        { return email;    }
    public List<String> getRole()   { return role;     }
}
