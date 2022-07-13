package io.xpring.xrpl.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;
import com.google.protobuf.ByteString;
import com.ripple.core.coretypes.Amount;
import com.ripple.core.coretypes.Blob;
import com.ripple.core.coretypes.hash.Hash128;
import com.ripple.core.coretypes.hash.Hash256;
import com.ripple.core.coretypes.uint.UInt32;
import io.xpring.codec.addresses.XAddressCodec;
import io.xpring.xrpl.ClassicAddress;
import io.xpring.xrpl.ImmutableClassicAddress;
import org.xrpl.rpc.v1.CurrencyAmount;
import org.xrpl.rpc.v1.Memo;
import org.xrpl.rpc.v1.Transaction;

import java.util.Optional;

public abstract class BaseConverter {

  private ObjectMapper mapper = new ObjectMapper();

  protected Optional<Blob> getBlob(ByteString byteString) {
    if (byteString.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(Blob.fromBytes(byteString.toByteArray()));
  }

  protected Optional<Hash128> getHash128(ByteString byteString) {
    if (byteString.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(Hash128.fromBytes(byteString.toByteArray()));
  }

  protected Optional<UInt32> getNonZeroValue(int value) {
    if (value == 0) {
      return Optional.empty();
    }
    return Optional.of(new UInt32(value));
  }

  protected ClassicAddress convertDestinationAddress(org.xrpl.rpc.v1.Payment fromPayment) {
    if (fromPayment.getDestination().getValue().getAddress().startsWith("r")) {
      ImmutableClassicAddress.Builder builder = ClassicAddress.builder()
          .address(fromPayment.getDestination().getValue().getAddress())
          .isTest(false);
      if (fromPayment.hasDestinationTag()) {
        builder.tag(fromPayment.getDestinationTag().getValue());
      }
      return builder.build();
    } else {
      return XAddressCodec.decode(fromPayment.getDestination().getValue().getAddress());
    }
  }

  protected ClassicAddress convertSourceAddress(Transaction source) {
    if (source.getAccount().getValue().getAddress().startsWith("r")) {
      ImmutableClassicAddress.Builder builder = ClassicAddress.builder()
          .address(source.getAccount().getValue().getAddress())
          .isTest(false);
      return builder.build();
    } else {
      return XAddressCodec.decode(source.getAccount().getValue().getAddress());
    }
  }

  protected Optional<Hash256> convert(ByteString byteString) {
    if (byteString == null || byteString.size() == 0) {
      return Optional.empty();
    }
    return Optional.of(Hash256.fromBytes(byteString.toByteArray()));
  }

  protected String convertMemo(Memo memo) {
    JsonMemo jsonMemo = JsonMemo.builder()
        .data(BaseEncoding.base16().encode(memo.getMemoData().getValue().toString(Charsets.UTF_8).getBytes()))
        .format(BaseEncoding.base16().encode(memo.getMemoFormat().getValue().toString(Charsets.UTF_8).getBytes()))
        .type(BaseEncoding.base16().encode(memo.getMemoType().getValue().toString(Charsets.UTF_8).getBytes()))
        .build();
    try {
      return "{ \"Memo\": " + mapper.writeValueAsString(jsonMemo) + "}";
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  protected Optional<Amount> convertDrops(CurrencyAmount currencyAmount) {
    if (!currencyAmount.hasXrpAmount()) {
      return Optional.empty();
    }
    return Optional.of(Amount.fromDropString(currencyAmount.getXrpAmount().getDrops() + ""));
  }
}
