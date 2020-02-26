package io.xpring.ilp.util;

import org.interledger.spsp.server.grpc.CreateAccountResponse;

/**
 * Defines the keys that will be present in {@link CreateAccountResponse#getCustomSettingsMap()}.
 *
 * These keys follow a hierarchical dot-notation format, and mirror the keys created on the Java connector exactly.
 */
public class IlpAuthConstants {

  public static String DOT = ".";
  public static String ILP_OVER_HTTP = "ilpOverHttp";

  public static String OUTGOING = "outgoing";
  public static String INCOMING = "incoming";

  public static String AUTH_TYPE = "auth_type";
  public static String SIMPLE = "simple";
  public static String JWT = "jwt";

  public static String TOKEN_ISSUER = "token_issuer";
  public static String TOKEN_AUDIENCE = "token_audience";
  public static String TOKEN_SUBJECT = "token_subject";
  public static String TOKEN_EXPIRY = "token_expiry";

  // Used to grab the auth credential from custom settings...
  public static String SHARED_SECRET = "shared_secret";
  public static String AUTH_TOKEN = "auth_token";
  public static String URL = "url";

  public static String HTTP_INCOMING_AUTH_TYPE = ILP_OVER_HTTP + DOT + INCOMING + DOT + AUTH_TYPE;

  public static String INCOMING_JWT_SETINGS_PREFIX = ILP_OVER_HTTP + DOT + INCOMING + DOT + JWT;
  public static String INCOMING_SIMPLE_SETINGS_PREFIX = ILP_OVER_HTTP + DOT + INCOMING + DOT + SIMPLE;
  public static String HTTP_INCOMING_TOKEN_ISSUER = INCOMING_JWT_SETINGS_PREFIX + DOT + TOKEN_ISSUER;
  public static String HTTP_INCOMING_TOKEN_AUDIENCE = INCOMING_JWT_SETINGS_PREFIX + DOT + TOKEN_AUDIENCE;
  public static String HTTP_INCOMING_SHARED_SECRET = INCOMING_JWT_SETINGS_PREFIX + DOT + SHARED_SECRET;
  public static String HTTP_INCOMING_TOKEN_SUBJECT = INCOMING_JWT_SETINGS_PREFIX + DOT + TOKEN_SUBJECT;
  public static String HTTP_INCOMING_SIMPLE_AUTH_TOKEN = INCOMING_SIMPLE_SETINGS_PREFIX + DOT + AUTH_TOKEN;

  public enum AuthType {
    /**
     * <p>The actual incoming and outgoing shared-secrets are used as Bearer tokens in an HTTP Authorization
     * header.</p>
     */
    SIMPLE,

    /**
     * Use shared-secret symmetric keys to create and verify JWT_HS_256 tokens.
     */
    JWT_HS_256,

    /**
     * Use RSA asymmetric keys to create and verify JWT_RS_256 tokens.
     */
    JWT_RS_256
  }
}
