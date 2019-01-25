package project.hack36.hack362k19;

/**
 * Created by user on 1/26/19.
 */

public class validationRequest {

    private String name, number, role;

    public validationRequest()
    {

    }

    public validationRequest(String name, String number, String role) {
        this.name = name;
        this.number = number;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
