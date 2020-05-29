package io.xpring.xrpl.model;

import org.immutables.value.Value;
import org.xrpl.rpc.v1.Payment.Path;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A path in the XRP Ledger.
 *
 * @deprecated Please use the idiomatically named {@link XrpPath} instead.
 *
 * @see "https://xrpl.org/paths.html"
 */
@Deprecated
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Value.Immutable
public interface XRPPath {
  static ImmutableXRPPath.Builder builder() {
    return ImmutableXRPPath.builder();
  }

  /**
   * The elements of this {@link XRPPath}.
   *
   * @return A List of {@link XRPPathElement}s that make up this {@link XRPPath}.
   */
  List<XRPPathElement> pathElements();

  /**
   * Constructs an {@link XRPPath} from a {@link org.xrpl.rpc.v1.Payment.Path}.
   *
   * @param path a {@link org.xrpl.rpc.v1.Payment.Path} (protobuf object) whose field values will be used
   *             to construct an {@link XRPPath}.
   * @return an {@link XRPPath} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L237">
   * Path protocol buffer</a>
   */
  static XRPPath from(Path path) {
    List<XRPPathElement> pathElements = path.getElementsList()
        .stream()
        .map(XRPPathElement::from)
        .collect(Collectors.toList());
    return builder()
        .pathElements(pathElements)
        .build();
  }
}
