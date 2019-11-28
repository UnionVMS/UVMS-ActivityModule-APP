package eu.europa.ec.fisheries.uvms.activity.service.auth;

public class AuthenticateResult {
    private boolean authenticated;
    private String dn;

    public AuthenticateResult(boolean authenticated, String dn) {
        this.authenticated = authenticated;
        this.dn = dn;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getDn() {
        return dn;
    }
}
