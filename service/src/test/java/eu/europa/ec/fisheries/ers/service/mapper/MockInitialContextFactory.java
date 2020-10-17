package eu.europa.ec.fisheries.ers.service.mapper;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;

/**
 * See <a href="https://stackoverflow.com/questions/17083142/junit-testing-jndi-initialcontext-outside-the-application-server#17083737">here</a>.
 */
public class MockInitialContextFactory implements InitialContextFactory {

	private static final ThreadLocal<Context> currentContext = new ThreadLocal<Context>();

	@Override
	public Context getInitialContext(Hashtable<?, ?> hashtable) throws NamingException {
		return currentContext.get();
	}

	public static void setCurrentContext(Context context) {
		currentContext.set(context);
	}

	public static void clearCurrentContext() {
		currentContext.remove();
	}
}
