package io.xpring.xrpl.model;

import io.xpring.xrpl.model.ImmutableXrpPath;
import org.immutables.value.Value;
import org.xrpl.rpc.v1.Payment.Path;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A path in the XRP Ledger.
 *
 * @see "https://xrpl.org/paths.html"
 */
@Value.Immutable
public interface XrpPath {
  static ImmutableXrpPath.Builder builder() {
    return ImmutableXrpPath.builder();
  }

  /**
   * The elements of this {@link XrpPath}.
   *
   * @return A List of {@link XrpPathElement}s that make up this {@link XrpPath}.
   */
  List<XrpPathElement> pathElements();

  /**
   * Constructs an {@link XrpPath} from a {@link org.xrpl.rpc.v1.Payment.Path}.
   *
   * @param path a {@link org.xrpl.rpc.v1.Payment.Path} (protobuf object) whose field values will be used
   *             to construct an {@link XrpPath}.
   * @return an {@link XrpPath} with its fields set via the analogous protobuf fields.
   * @see <a href="https://github.com/ripple/rippled/blob/develop/src/ripple/proto/org/xrpl/rpc/v1/transaction.proto#L237">
   * Path protocol buffer</a>
   */
  static XrpPath from(Path path) {
    List<XrpPathElement> pathElements = path.getElementsList()
        .stream()
        .map(XrpPathElement::from)
        .collect(Collectors.toList());
    return builder()
        .pathElements(pathElements)
        .build();
  }
}
