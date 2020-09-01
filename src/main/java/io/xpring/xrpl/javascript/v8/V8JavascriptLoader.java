package io.xpring.xrpl.javascript.v8;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Function;
import com.eclipsesource.v8.V8Object;
import com.google.common.io.Resources;

import java.net.URL;
import java.nio.charset.StandardCharsets;

public class V8JavascriptLoader {

  public static void main(String[] args) {
    new V8JavascriptLoader().init();
  }

  public void init() {
    V8 runtime = V8.createV8Runtime();
    String text = readXpringJs();
    try {
      runtime.executeScript(text);
      V8Object wallet = (V8Object) newInstanceFunction(runtime).call(runtime, new V8Array(runtime).push("test1").push("test2").push("test3"));
      System.out.println(runtime.executeJSFunction("XpringCommonJS.isValidClassicAddress.isHex", "AAA"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static V8Function newInstanceFunction(V8 runtime) {
    return (V8Function) runtime.executeObjectScript(
        "(function() { return function(clazz) { return new XpringCommonJS.Wallet(...Array.prototype.slice.call(arguments, 1)); } })();");
  }

  private String readXpringJs() {
    try {
      URL url = Resources.getResource("./index.js");
      return Resources.toString(url, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}




