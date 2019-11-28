package eu.europa.ec.fisheries.uvms.activity.service.auth;

import org.slf4j.LoggerFactory;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Optional;

@LocalBean
@Stateless
public class LdapAuthentication {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(LdapAuthentication.class.getName());

    private static final String LDAP_CONTEXT_NAME = "java:global/federation/ldap/test";

    public AuthenticateResult doAuthentication(String username, String password) {
        if (username == null || password == null || password.isEmpty()) {
            return new AuthenticateResult(false, null);
        }
        InitialContext initialContext = null;
        InitialContext authenticationContext = null;
        try {
            initialContext = InitialDirContext.doLookup(LDAP_CONTEXT_NAME);
            initialContext.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");

            final Optional<String> dn = findDN(username);
            if (dn.isPresent()) {
                initialContext.addToEnvironment(Context.SECURITY_PRINCIPAL, dn.get());
                initialContext.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            } else {
                LOG.debug("Failed to get distinguished name for user {}", username);
                return new AuthenticateResult(false, null);
            }

            // Does the actual authentication. Will throw exception if failed
            authenticationContext = new InitialContext(initialContext.getEnvironment());

            return new AuthenticateResult(true, dn.get());
        } catch (NamingException ex) {
            LOG.debug("Login failed", ex);
            return new AuthenticateResult(false, null);
        } finally {
            closeCtx(initialContext);
            closeCtx(authenticationContext);
        }
    }

    public boolean changePassword(String username, String password, String newPassword) {
        AuthenticateResult authenticateResult = doAuthentication(username, password);
        if (authenticateResult.isAuthenticated()) {
            return changePassword(authenticateResult.getDn(), newPassword);
        }
        return false;
    }

    private boolean changePassword(String dn, String newPassword) {
        InitialContext initialContext = null;
        try {
            initialContext = InitialDirContext.doLookup(LDAP_CONTEXT_NAME);
            initialContext.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");

            DirContext ctx = (DirContext) initialContext;

            ModificationItem[] mods = new ModificationItem[1];

            Attribute passwordAttribute = new BasicAttribute("userpassword", newPassword);

            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, passwordAttribute);

            ctx.modifyAttributes(dn, mods);
            return true;
        } catch (NamingException e) {
            LOG.debug("Password change failed", e);
        } finally {
            closeCtx(initialContext);
        }
        return false;
    }

    private Optional<String> findDN(String username) {
        DirContext ctx = null;
        try {
            ctx = InitialDirContext.doLookup(LDAP_CONTEXT_NAME);

            // Find DN using filter search
            SearchControls ctls = new SearchControls();
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> answer = ctx.search("dc=test, dc=com", "(uid= " + username + ")", ctls);

            if (answer.hasMore()) {
                SearchResult res = answer.next();
                return res != null ? Optional.of(res.getNameInNamespace()) : Optional.empty();
            } else
                return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            closeCtx(ctx);
        }
    }

    private void closeCtx(Context ctx) {
        try {
            if (ctx != null) {
                ctx.close();
            } else {
                LOG.warn("LDAP ctx was null.");
            }
        } catch (NamingException e) {
            // do nothing
        }
    }
}
