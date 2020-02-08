package org.interledger.spsp.server.config.ildcp;

import org.interledger.codecs.ildcp.IldcpUtils;
import org.interledger.core.InterledgerPreparePacket;
import org.interledger.ildcp.IldcpFetcher;
import org.interledger.ildcp.IldcpRequestPacket;
import org.interledger.link.Link;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.Objects;

public class IldcpConfig {

  @Autowired
  private Link parentLink;

  @Bean
  private IldcpFetcher ildcpFetcher() {

    // Construct a lambda that implements the Fetch logic for IL-DCP.
    final IldcpFetcher ildcpFetcher = ildcpRequest -> {
      Objects.requireNonNull(ildcpRequest);

      final IldcpRequestPacket ildcpRequestPacket = IldcpRequestPacket.builder().build();
      final InterledgerPreparePacket preparePacket =
        InterledgerPreparePacket.builder().from(ildcpRequestPacket).build();

      // Fetch the IL-DCP response using the Link.
      return parentLink.sendPacket(preparePacket)
        .map(
          // If FulfillPacket...
          IldcpUtils::toIldcpResponse,
          // If Reject Packet...
          (interledgerRejectPacket) -> {
            throw new RuntimeException(
              String.format("IL-DCP negotiation failed! Reject: %s", interledgerRejectPacket)
            );
          }
        );
    };

    return ildcpFetcher;
  }

}
