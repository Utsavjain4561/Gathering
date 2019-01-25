package project.hack36.hack362k19;

/**
 * Created by user on 1/26/19.
 */

public class AuthDetails {

    private String name;
    private String password;

    public AuthDetails(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
