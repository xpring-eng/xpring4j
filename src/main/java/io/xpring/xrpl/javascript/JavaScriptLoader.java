package io.xpring.xrpl.javascript;

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
    private static final String missingIndexJS = "Could not load JavaScript resources for the XRP Ledger. Check that `index.js` exists and is well formed.";
    private static final String missingXpringCommonJS = "Could not find global XpringCommonJS in Context. Check that `XpringCommonJS.default` is defined as a global variable.";
    private static final String missingResource = "Could not find the requested resource: ";

    /**
     * The name of the JavaScript file to load from.
     */
    private static final String javaScriptResourceName = "/index.js";

    /**
     * The identifier for the JavaScript language in the graalvm polygot package.
     */
    private static final String javaScriptLanguageIdentifier = "js";

    private static final Context context;

    static {
        // Load the webpacked XpringCommonJS JavaScript file.
        context = Context.create(javaScriptLanguageIdentifier);
        Source source;
        try (InputStreamReader reader =
                 new InputStreamReader(JavaScriptLoader.class.getResourceAsStream(javaScriptResourceName))) {
            source = Source.newBuilder(javaScriptLanguageIdentifier, reader, javaScriptResourceName).build();
        } catch (Exception exception) {
            throw new RuntimeException(missingIndexJS);
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
     *
     * @return a new JavaScript context.
     */
    public static Context getContext() {
        return context;
    }

    /**
     * Load the value from the XpringCommonJS exports on the given JSContext.
     *
     * This method loads value from `XpringCommonJS.$value`.
     *
     * @param value:   The name of the value you are trying to load.
     * @param context: The context load from.
     *
     * @return A `Value` referring to the requested resource.
     *
     * @throws JavaScriptLoaderException An exception if the javascript could not be loaded.
     */
    public static Value loadResource(String value, Context context) throws JavaScriptLoaderException {
        Value root = context.getBindings(javaScriptLanguageIdentifier).getMember("XpringCommonJS");

        // If the root is not found throw an exception.
        if (root.isNull()) {
            throw new JavaScriptLoaderException(missingXpringCommonJS);
        }

        return loadResource(value, root);
    }

    /**
     * Load a class or function as a keyed subscript from the given value.
     *
     * @param resourceName: The name of the resource to load.
     * @param value:        The value to load a resource from.
     *
     * @return A {@link Value} referring to the requested resource.
     *
     * @throws JavaScriptLoaderException An exception if the javascript could not be loaded.
     */
    public static Value loadResource(String resourceName, Value value) throws JavaScriptLoaderException {
        Value resource = value.getMember(resourceName);
        if (resource.isNull()) {
            throw new JavaScriptLoaderException(missingResource + resourceName);
        }

        return resource;
    }
}
