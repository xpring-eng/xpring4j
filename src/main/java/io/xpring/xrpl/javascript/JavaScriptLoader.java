package io.xpring.xrpl.javascript;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Function;
import com.eclipsesource.v8.V8Object;
import com.google.common.io.Resources;
import org.graalvm.polyglot.Value;

import java.net.URL;
import java.nio.charset.StandardCharsets;

// TODO(keefertaylor): This class is used across Pay ID and XRP. Refactor to Common.
/**
 * Provides helper functions for loading JavaScript.
 */
public class JavaScriptLoader {

  /**
   * Error messages for exceptions.
   */
  private static final String missingIndexJS =
      "Could not load JavaScript resources for the XRP Ledger. Check that `index.js` exists and is well formed.";
  private static final String missingXpringCommonJS =
      "Could not find global XpringCommonJS in Context. Check that `XpringCommonJS.default` is defined as a "
          + "global variable.";
  private static final String missingResource = "Could not find the requested resource: ";

  /**
   * The name of the JavaScript file to load from.
   */
  private static final String javaScriptResourceName = "/index.js";

  /**
   * The identifier for the JavaScript language in the graalvm polygot package.
   */
  private static final String javaScriptLanguageIdentifier = "js";
  private static V8Object root;
  private static V8 runtime;

  static {
    init();
  }

  public static void init() {
    runtime = V8.createV8Runtime();
    String text = readXpringJs();
    try {
      runtime.executeScript(text);
      root = runtime.getObject("XpringCommonJS");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static String readXpringJs() {
    try {
      URL url = Resources.getResource("./index.js");
      return Resources.toString(url, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Please do not initialize this static utility class.
   */
  private JavaScriptLoader() {
  }

  /**
   * Load a class or function as a keyed subscript from the given value.
   *
   * @param resourceName The name of the resource to load.
   * @param value The value to load a resource from.
   * @return A {@link Value} referring to the requested resource.
   * @throws JavaScriptLoaderException An exception if the javascript could not be loaded.
   */
  public static V8Object loadResource(String resourceName, V8Object value) throws JavaScriptLoaderException {
    V8Object resource = value.getObject(resourceName);
    if (resource.isUndefined()) {
      throw new JavaScriptLoaderException(missingResource + resourceName);
    }

    return resource;
  }

  public static V8Object getContext() {
    return root;
  }

  public static V8Array newV8Array(Object... args) {
    V8Array array = new V8Array(runtime);
    for (Object arg: args) {
      array.push(arg);
    }
    return array;
  }


  private static V8Function newInstanceFunction(V8 runtime, String clazz) {
    return (V8Function) runtime.executeObjectScript(
        "(function() { return function(clazz) { return new XpringCommonJS." + clazz + "(...Array.prototype.slice.call(arguments, 1)); } })();");
  }

  public static V8Object newWallet(Object... args) {
    V8Array v8Array = new V8Array(runtime).push("Wallet");
    for(Object arg: args) {
      v8Array.push(arg);
    }
    Object wallet = newInstanceFunction(runtime, "Wallet").call(runtime, v8Array);
    v8Array.release();
    return (V8Object) wallet;
  }

  public static V8Object newWalletFactory() {
    V8Array v8Array = new V8Array(runtime).push("WalletFactory");
    Object walletFactory = newInstanceFunction(runtime, "WalletFactory").call(runtime, v8Array);
    v8Array.release();
    return (V8Object) walletFactory;
  }


//  public static V8Object newInstanceFunction(String ) {
//    return (V8Function) runtime.executeObjectScript(
//        "(function() { return new clazz(...Array.prototype.slice.call(arguments, 1)); }()");
//  }
}
