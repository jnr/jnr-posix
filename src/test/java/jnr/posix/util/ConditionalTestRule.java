package jnr.posix.util;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public abstract class ConditionalTestRule implements TestRule {
    public Statement apply(Statement base, Description description) {
        return statement(base);
    }

    private Statement statement(final Statement base) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if (isSatisfied()) {
                    base.evaluate();
                }
            }
        };
    }
    public abstract  boolean isSatisfied();
}
