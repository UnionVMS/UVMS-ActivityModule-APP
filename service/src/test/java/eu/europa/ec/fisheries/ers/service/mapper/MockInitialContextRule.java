package eu.europa.ec.fisheries.ers.service.mapper;

import javax.naming.Context;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * See <a href="https://stackoverflow.com/questions/17083142/junit-testing-jndi-initialcontext-outside-the-application-server#17083737">here</a>.
 */
public class MockInitialContextRule implements TestRule {

	private final Context context;

	public MockInitialContextRule(Context context) {
		this.context = context;
	}

	@Override
	public Statement apply(Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.setProperty(Context.INITIAL_CONTEXT_FACTORY, MockInitialContextFactory.class.getName());
				MockInitialContextFactory.setCurrentContext(context);
				try {
					base.evaluate();
				} finally {
					System.clearProperty(Context.INITIAL_CONTEXT_FACTORY);
					MockInitialContextFactory.clearCurrentContext();
				}
			}
		};
	}
}
