package io.xpring.ilp;

import static org.junit.Assert.assertThrows;

import io.xpring.ilp.grpc.IlpCredentials;
import org.junit.Test;

public class IlpCredentialsTest {

  @Test
  public void illegalArgumentExceptionWhenTokenHasBearer() {
    // GIVEN an access token with a "Bearer " prefix
    String accessToken = "Bearer password";

    // WHEN IlpCredentials are built
    // THEN expect an IllegalArgumentException to be thrown
    assertThrows(
      "accessToken cannot start with \"Bearer \"",
      IllegalArgumentException.class,
      () -> IlpCredentials.build(accessToken)
    );
  }

}
