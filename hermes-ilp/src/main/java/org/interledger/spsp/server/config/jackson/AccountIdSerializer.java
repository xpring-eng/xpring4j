package org.interledger.spsp.server.config.jackson;

import org.interledger.connector.accounts.AccountId;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

import java.io.IOException;

/**
 * Jackson serializer {@link AccountId}.
 */
public class AccountIdSerializer extends StdScalarSerializer<AccountId> {

  public AccountIdSerializer() {
    super(AccountId.class, false);
  }

  @Override
  public void serialize(AccountId accountId, JsonGenerator gen, SerializerProvider provider) throws IOException {
    gen.writeString(accountId.value());
  }
}
