package io.xpring.javascript;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.InputStreamReader;

/**
 * Provides helper functions for loading JavaScript
 */
public class JavaScriptLoader {

    /**
     * Error messages for exceptions.
     */
    private static final String missingBundledJS = "Could not load JavaScript resources for the XRP Ledger. Check that `bundled.js` exists and is well formed.";
    private static final String missingEntryPoint = "Could not find global EntryPoint in Context. Check that `EntryPoint.default` is defined as a global variable.";
    private static final String missingResource = "Could not find the requested resource: ";

    /**
     * The name of the JavaScript file to load from.
     */
    private static final String javaScriptResourceName = "/bundled.js";

    /**
     * The identifier for the JavaScript language in the graalvm polygot package.
     */
    private static final String javaScriptLanguageIdentifier = "js";

    private static final Context context;

    static {
        // Load the bundled JavaScript file.
        context = Context.create(javaScriptLanguageIdentifier);
        Source source;
        try (InputStreamReader reader =
                 new InputStreamReader(JavaScriptLoader.class.getResourceAsStream(javaScriptResourceName))) {
            source = Source.newBuilder(javaScriptLanguageIdentifier, reader, javaScriptResourceName).build();
        } catch (Exception exception) {
            throw new RuntimeException(missingBundledJS);
        }

        // Load the file into the context and find the root object.
        context.eval(source);
    }

    /**
     * Please do not initialize this static utility class.
     */
    private JavaScriptLoader() {
    }

    /**
     * Load a JavaScript Context that contains all the JavaScript code for the Xpring ecosystem.
     */
    public static Context getContext() {
        return context;
    }

    /**
     * Load a class or function from the default entry point on the given JSContext.
     *
     * This method loads value from `EntryPoint.default.<value>`.
     *
     * @param resourceName: The name of the resource to load.
     * @param context:      The context load from.
     *
     * @return A `Value` referring to the requested resource.
     */
    public static Value loadResource(String resourceName, Context context) throws JavaScriptLoaderException {
        Value root = context.getBindings(javaScriptLanguageIdentifier).getMember("EntryPoint").getMember("default");

        // If the root is not found throw an exception.
        if (root.isNull()) {
            throw new JavaScriptLoaderException(missingEntryPoint);
        }

        return loadResource(resourceName, root);
    }

    /**
     * Load a class or function as a keyed subscript from the given value.
     *
     * @param resourceName: The name of the resource to load.
     * @param value:        The value to load a resource from.
     *
     * @return A `Value` referring to the requested resource.
     */
    public static Value loadResource(String resourceName, Value value) throws JavaScriptLoaderException {
        Value resource = value.getMember(resourceName);
        if (resource.isNull()) {
            throw new JavaScriptLoaderException(missingResource + resourceName);
        }

        return resource;
    }
}

