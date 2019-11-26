package eu.europa.ec.fisheries.uvms.activity.rest;

public class LoginResponse {

    private int id;
    private int personalNumber;
    private String jwtToken;

    public LoginResponse(int id, int personalNumber, String jwtToken) {
        this.id = id;
        this.personalNumber = personalNumber;
        this.jwtToken = jwtToken;
    }

    public int getId() {
        return id;
    }

    public int getPersonalNumber() {
        return personalNumber;
    }

    public String getJwtToken() {
        return jwtToken;
    }
}
