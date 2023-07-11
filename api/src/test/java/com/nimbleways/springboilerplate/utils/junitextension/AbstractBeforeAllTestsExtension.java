package com.nimbleways.springboilerplate.utils.junitextension;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

// https://stackoverflow.com/questions/43282798/in-junit-5-how-to-run-code-before-all-tests
public abstract class AbstractBeforeAllTestsExtension
        implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    @Override
    public void beforeAll(ExtensionContext context) {
        // We need to use a unique key here, across all usages of this particular extension.
        String uniqueKey = this.getClass().getName();
        Object value = context.getRoot().getStore(GLOBAL).get(uniqueKey);
        if (value == null) {
            // First invocation.
            context.getRoot().getStore(GLOBAL).put(uniqueKey, this);
            setup();
        }
    }

    // Callback that is invoked exactly once
    // before the start of all test containers.
    abstract void setup();

    // Callback that is invoked exactly once
    // after the end of all test containers.
    // Inherited from {@code CloseableResource}
    public abstract void close() throws Throwable;
}
