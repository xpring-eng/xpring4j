package org.interledger.spsp.server.grpc;

import org.interledger.link.http.JwtAuthSettings;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.google.common.collect.Lists;
import okhttp3.HttpUrl;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * For testing purposes to simulate a JWKS server. Creates RSA keys in-memory that can be used to created signed
 * JWTs. Also provides a {@link JwksServer#getJwks()} method to create a JWKS response that can be served up
 * by fake JWKS server like WireMock for {@code GET /.well-known/jwks.json requests}.
 */
public class JwksServer {

  public static final List<String> KEY_IDS = Lists.newArrayList("key1", "key2", "key3");
  private List<KeyPair> keyPairs;

  public JwksServer() {
    resetKeyPairs();
  }

  /**
   * Resets and regenerates new RSA key pairs
   */
  public void resetKeyPairs() {
    try {
      KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
      kpg.initialize(2048);
      this.keyPairs = KEY_IDS.stream()
        .map($ -> kpg.generateKeyPair())
        .collect(Collectors.toList());
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Creates a signed JWT using the provide auth settings (using default key 0)
   * @param jwtAuthSettings
   * @param expiresAt jwt expiration
   * @return
   */
  public String createJwt(JwtAuthSettings jwtAuthSettings, Instant expiresAt) {
    return createJwtUsingKey(jwtAuthSettings, expiresAt, 0);
  }

  /**
   * Creates a signed JWT using the provide auth settings
   * @param jwtAuthSettings
   * @param expiresAt jwt expiration
   * @param keyNum specific key number to use
   * @return signed jwt
   */
  public String createJwtUsingKey(JwtAuthSettings jwtAuthSettings, Instant expiresAt, int keyNum) {
    if (keyNum > KEY_IDS.size()) {
      throw new IllegalArgumentException(keyNum + " out of bounds");
    }
    KeyPair keyPair = keyPairs.get(keyNum);
    String keyId = KEY_IDS.get(keyNum);
    return JWT.create()
      .withAudience(jwtAuthSettings.tokenAudience().get())
      .withKeyId(keyId)
      .withIssuer(jwtAuthSettings.tokenIssuer().get().toString())
      .withSubject(jwtAuthSettings.tokenSubject())
      .withExpiresAt(Date.from(expiresAt))
      .sign(Algorithm.RSA256(new RSAKeyProvider() {
        @Override
        public RSAPublicKey getPublicKeyById(String keyId) {
          return (RSAPublicKey) keyPair.getPublic();
        }

        @Override
        public RSAPrivateKey getPrivateKey() {
          return (RSAPrivateKey) keyPair.getPrivate();
        }

        @Override
        public String getPrivateKeyId() {
          return keyId;
        }
      }));
  }

  /**
   * Creates JWKS response with the public keys for this server
   * @return
   */
  public JwksResponse getJwks() {
    return new JwksResponse(
      IntStream
        .range(0, KEY_IDS.size())
        .mapToObj(index -> generateJWK(keyPairs.get(index).getPublic(), KEY_IDS.get(index)))
      .collect(Collectors.toList()));
  }

  public int getKeyCount() {
    return keyPairs.size();
  }

  private Map<String, Object> generateJWK(PublicKey publicKey, String keyId){
    RSAPublicKey rsa = (RSAPublicKey) publicKey;
    Map<String, Object> values = new HashMap<>();
    values.put("kty", rsa.getAlgorithm()); // getAlgorithm() returns kty not algorithm
    values.put("kid", keyId);
    values.put("n", Base64.getEncoder().encodeToString(rsa.getModulus().toByteArray()));
    values.put("e", Base64.getEncoder().encodeToString(rsa.getPublicExponent().toByteArray()));
    values.put("alg", "RS256");
    values.put("use", "sig");
    return values;
  }

  /**
   * JWKS response object
   */
  public static class JwksResponse {
    private final List<Map<String, Object>> keys;

    public JwksResponse(List<Map<String, Object>> keys) {
      this.keys = keys;
    }

    public List<Map<String, Object>> getKeys() {
      return keys;
    }
  }

  public static void main(String[] args) {
    JwksServer jwtServer = new JwksServer();
    System.out.println(
      jwtServer.createJwt(JwtAuthSettings.builder()
      .tokenIssuer(HttpUrl.parse("http://somedomain.com"))
      .tokenSubject("test")
      .tokenAudience("some-audience")
      .build(),
      Instant.now().plusSeconds(30)
    ));
  }


}
