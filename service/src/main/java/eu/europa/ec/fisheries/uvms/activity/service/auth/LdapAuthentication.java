package eu.europa.ec.fisheries.uvms.activity.service.auth;

import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Optional;

@LocalBean
@Stateless
public class LdapAuthentication {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(LdapAuthentication.class.getName());

    private static final String LDAP_CONTEXT_NAME = "java:global/federation/ldap/test";

    @Resource
    private EJBContext ejbContext;

    public boolean doAuthentication(String username, String password) {
        if (username == null || password == null || password.isEmpty()) {
            return false;
        }
        InitialContext ctx = null;
        InitialContext ctx2 = null;
        try {
            ctx = InitialDirContext.doLookup(LDAP_CONTEXT_NAME);
            ctx.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");

            final Optional<String> dn = findDN(username);
            if (dn.isPresent()) {
                ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, dn.get());
                ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            } else {
                LOG.debug("Failed to get distinguished name for user {}", username);
                return false;
            }

            // Does the actual authentication. Will throw exception if failed
            ctx2 = new InitialContext(ctx.getEnvironment());
        } catch (NamingException ex) {
            LOG.debug("Login failed", ex);
            return false;
        } finally {
            if (ctx2 != null) {
                try {
                    ctx2.close();
                } catch (NamingException e) {
                    // do nothing
                }
            }
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    // do nothing
                }
            }
        }
        return true;
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

    private void closeCtx(DirContext ctx) {
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
